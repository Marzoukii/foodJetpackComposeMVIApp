package com.example.foodapp.domain.usecase

import com.example.foodapp.data.NetworkResult
import com.example.foodapp.data.repository.AuthRepository
import com.example.foodapp.domain.model.UserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    fun execute(): UserModel? = authRepository.getCurrentUser()
}

class GetAuthStateUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    fun execute(): Flow<UserModel?> = flow {
        authRepository.getAuthState().collect {
            emit(it)
        }
    }
}

class SignInUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    fun execute(email: String, password: String): Flow<NetworkResult<UserModel?>> = flow {
        authRepository.signIn(email, password).collect {
            emit(it)
        }
    }
}

class SignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    fun execute(name: String, email: String, password: String): Flow<NetworkResult<UserModel?>> = flow {
        authRepository.signUp(name, email, password).collect {
            emit(it)
        }
    }
}

class SignInWithGoogleUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    fun execute(idToken: String): Flow<NetworkResult<UserModel?>> = flow {
        authRepository.signInWithGoogle(idToken).collect {
            emit(it)
        }
    }
}

class SignInAsGuestUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    fun execute(): Flow<NetworkResult<UserModel?>> = flow {
        authRepository.signInAnonymously().collect {
            emit(it)
        }
    }
}

class SendPasswordResetUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    fun execute(email: String): Flow<NetworkResult<Unit?>> = flow {
        authRepository.sendPasswordReset(email).collect {
            emit(it)
        }
    }
}

class SignOutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    fun execute() {
        authRepository.signOut()
    }
}
