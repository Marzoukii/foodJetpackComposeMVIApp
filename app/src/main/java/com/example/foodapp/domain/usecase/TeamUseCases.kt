package com.example.foodapp.domain.usecase

import com.example.foodapp.data.NetworkResult
import com.example.foodapp.data.repository.TeamRepository
import com.example.foodapp.domain.model.TeamMemberModel
import com.example.foodapp.domain.model.UserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/** À chaque connexion : annuaire des comptes + rôle admin automatique des propriétaires. */
class SyncUserProfileUseCase @Inject constructor(
    private val teamRepository: TeamRepository
) {
    suspend fun execute(user: UserModel) {
        teamRepository.syncProfile(user)
    }
}

class ObserveTeamUseCase @Inject constructor(
    private val teamRepository: TeamRepository
) {
    fun execute(): Flow<List<TeamMemberModel>> = flow {
        teamRepository.observeTeam().collect {
            emit(it)
        }
    }
}

class SetAdminUseCase @Inject constructor(
    private val teamRepository: TeamRepository
) {
    fun execute(userId: String, isAdmin: Boolean): Flow<NetworkResult<Unit?>> = flow {
        teamRepository.setAdmin(userId, isAdmin).collect {
            emit(it)
        }
    }
}
