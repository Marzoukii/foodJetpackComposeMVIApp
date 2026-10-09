package com.example.foodapp.domain.model

data class UserModel(
    val id: String,
    val name: String?,
    val email: String?,
    val photoUrl: String?,
    /** Session invitée (commande sur place) : la livraison demande un vrai compte. */
    val isAnonymous: Boolean = false
)
