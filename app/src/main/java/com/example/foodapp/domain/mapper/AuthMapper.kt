package com.example.foodapp.domain.mapper

import com.example.foodapp.domain.model.UserModel
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class AuthMapper @Inject constructor() {

    fun mapUser(user: FirebaseUser?): UserModel? {
        if (user == null) return null
        return UserModel(
            id = user.uid,
            name = user.displayName,
            email = user.email,
            photoUrl = user.photoUrl?.toString()
        )
    }
}
