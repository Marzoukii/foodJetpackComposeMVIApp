package com.example.foodapp.domain.model

/** Compte (non invité) visible par les admins, avec son rôle. */
data class TeamMemberModel(
    val id: String,
    val name: String?,
    val email: String,
    val isAdmin: Boolean
)
