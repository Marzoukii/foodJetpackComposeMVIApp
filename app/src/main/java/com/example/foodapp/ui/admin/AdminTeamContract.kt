package com.example.foodapp.ui.admin

import com.example.foodapp.domain.model.TeamMemberModel
import com.example.foodapp.ui.util.UiText

data class AdminTeamState(
    val isLoading: Boolean = true,
    val error: UiText? = null,
    val members: List<TeamMemberModel> = emptyList(),
    /** Un admin ne peut pas se retirer lui-même son rôle (il perdrait l'accès à cet écran). */
    val currentUserId: String? = null,
    val query: String = ""
) {
    val filteredMembers: List<TeamMemberModel>
        get() = query.trim().takeIf { it.isNotEmpty() }?.let { q ->
            members.filter { it.email.contains(q, ignoreCase = true) || it.name?.contains(q, ignoreCase = true) == true }
        } ?: members
}

sealed interface AdminTeamIntent {
    data class QueryChanged(val value: String) : AdminTeamIntent
    data class AdminToggled(val member: TeamMemberModel, val isAdmin: Boolean) : AdminTeamIntent
    data object Retry : AdminTeamIntent
    data object BackClicked : AdminTeamIntent
}

sealed interface AdminTeamEffect {
    data object NavigateBack : AdminTeamEffect
    data class ShowMessage(val message: UiText) : AdminTeamEffect
}
