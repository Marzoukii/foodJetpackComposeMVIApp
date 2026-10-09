package com.example.foodapp.data.repository

import com.example.foodapp.data.NetworkResult
import com.example.foodapp.domain.model.AdminConfig
import com.example.foodapp.domain.model.TeamMemberModel
import com.example.foodapp.domain.model.UserModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

/**
 * Rôles dans Realtime Database :
 * - users/{uid} = { email, name } : annuaire des comptes, écrit par chaque compte à sa connexion
 * - admins/{uid} = true : écrit par un admin, ou par un compte propriétaire pour lui-même
 *   (les règles vérifient l'e-mail : l'app ne peut pas s'auto-promouvoir)
 */
class TeamRepository @Inject constructor(
    private val database: FirebaseDatabase
) {

    private val usersRef get() = database.getReference(USERS)
    private val adminsRef get() = database.getReference(ADMINS)

    /** Inscrit le compte dans l'annuaire et, pour un propriétaire, lui donne le rôle admin. */
    suspend fun syncProfile(user: UserModel) {
        val email = user.email ?: return
        if (user.isAnonymous) return
        runCatching {
            val profile = buildMap {
                put("email", email)
                user.name?.takeIf { it.isNotBlank() }?.let { put("name", it.take(100)) }
            }
            withTimeout(WRITE_TIMEOUT_MS) { usersRef.child(user.id).setValue(profile).await() }
        }
        if (email.lowercase() in AdminConfig.OWNER_EMAILS) {
            // Refusé par les règles si l'e-mail n'est pas vérifié : le compte reste simple client.
            runCatching {
                withTimeout(WRITE_TIMEOUT_MS) { adminsRef.child(user.id).setValue(true).await() }
            }
        }
    }

    /** Admin : tous les comptes, admins en premier puis par e-mail. */
    fun observeTeam(): Flow<List<TeamMemberModel>> =
        combine(usersRef.observe { it }, adminsRef.observe { it }) { users, admins ->
            val adminIds = admins.children.filter { it.getValue(Boolean::class.java) == true }.mapNotNull { it.key }.toSet()
            users.children.mapNotNull { child ->
                val id = child.key ?: return@mapNotNull null
                val email = child.child("email").getValue(String::class.java) ?: return@mapNotNull null
                TeamMemberModel(
                    id = id,
                    name = child.child("name").getValue(String::class.java),
                    email = email,
                    isAdmin = id in adminIds
                )
            }.sortedWith(compareByDescending<TeamMemberModel> { it.isAdmin }.thenBy { it.email })
        }

    /** Admin : donne ou retire le rôle admin d'un compte. */
    fun setAdmin(userId: String, isAdmin: Boolean): Flow<NetworkResult<Unit?>> = flow {
        try {
            val ref = adminsRef.child(userId)
            withTimeout(WRITE_TIMEOUT_MS) {
                if (isAdmin) ref.setValue(true).await() else ref.removeValue().await()
            }
            emit(NetworkResult.Success(Unit))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e))
        }
    }

    private fun <T> Query.observe(map: (DataSnapshot) -> T): Flow<T> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(map(snapshot))
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        addValueEventListener(listener)
        awaitClose { removeEventListener(listener) }
    }

    private companion object {
        const val USERS = "users"
        const val ADMINS = "admins"
        const val WRITE_TIMEOUT_MS = 15_000L
    }
}
