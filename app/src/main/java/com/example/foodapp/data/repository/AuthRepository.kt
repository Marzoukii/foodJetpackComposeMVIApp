package com.example.foodapp.data.repository

import com.example.foodapp.data.NetworkResult
import com.example.foodapp.domain.mapper.AuthMapper
import com.example.foodapp.domain.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val authMapper: AuthMapper
) {

    /** Utilisateur connecté au lancement (Firebase garde la session entre deux ouvertures). */
    fun getCurrentUser(): UserModel? = authMapper.mapUser(firebaseAuth.currentUser)

    /** Émet l'utilisateur à chaque connexion / déconnexion (null = déconnecté). */
    fun getAuthState(): Flow<UserModel?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(authMapper.mapUser(auth.currentUser))
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    fun signIn(email: String, password: String): Flow<NetworkResult<UserModel?>> = flow {
        try {
            val result = firebaseAuth.signInWithEmailAndPassword(email.trim(), password).await()
            emit(NetworkResult.Success(authMapper.mapUser(result.user)))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e))
        }
    }

    fun signUp(name: String, email: String, password: String): Flow<NetworkResult<UserModel?>> = flow {
        try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email.trim(), password).await()
            val user = result.user
            val profile = UserProfileChangeRequest.Builder().setDisplayName(name.trim()).build()
            user?.updateProfile(profile)?.await()
            user?.reload()?.await()
            emit(NetworkResult.Success(authMapper.mapUser(firebaseAuth.currentUser)))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e))
        }
    }

    fun signInWithGoogle(idToken: String): Flow<NetworkResult<UserModel?>> = flow {
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            emit(NetworkResult.Success(authMapper.mapUser(result.user)))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e))
        }
    }

    fun sendPasswordReset(email: String): Flow<NetworkResult<Unit?>> = flow {
        try {
            firebaseAuth.sendPasswordResetEmail(email.trim()).await()
            emit(NetworkResult.Success(Unit))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e))
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }
}
