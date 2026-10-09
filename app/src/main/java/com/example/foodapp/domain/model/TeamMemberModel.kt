package com.example.foodapp.domain.model

/** Compte (non invité) visible par les admins, avec son rôle. */
data class TeamMemberModel(
    val id: String,
    val name: String?,
    val email: String,
    val isAdmin: Boolean
)

object AdminConfig {
    /**
     * Comptes propriétaires : deviennent admin automatiquement à la connexion (e-mail vérifié).
     * Doit rester identique à la liste de database.rules.json, qui seule fait foi.
     */
    val OWNER_EMAILS = setOf("chadimarzouki@gmail.com")
}
