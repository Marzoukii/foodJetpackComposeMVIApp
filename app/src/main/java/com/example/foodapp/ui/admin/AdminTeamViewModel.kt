package com.example.foodapp.ui.admin

import androidx.lifecycle.viewModelScope
import com.example.foodapp.R
import com.example.foodapp.data.NetworkResult
import com.example.foodapp.domain.model.TeamMemberModel
import com.example.foodapp.domain.usecase.GetCurrentUserUseCase
import com.example.foodapp.domain.usecase.ObserveTeamUseCase
import com.example.foodapp.domain.usecase.SetAdminUseCase
import com.example.foodapp.ui.base.MviViewModel
import com.example.foodapp.ui.util.uiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class AdminTeamViewModel @Inject constructor(
    getCurrentUserUseCase: GetCurrentUserUseCase,
    private val observeTeamUseCase: ObserveTeamUseCase,
    private val setAdminUseCase: SetAdminUseCase
) : MviViewModel<AdminTeamState, AdminTeamIntent, AdminTeamEffect>(
    AdminTeamState(currentUserId = getCurrentUserUseCase.execute()?.id)
) {

    private var teamJob: Job? = null

    init {
        observeTeam()
    }

    override fun onIntent(intent: AdminTeamIntent) {
        when (intent) {
            is AdminTeamIntent.QueryChanged -> setState { copy(query = intent.value) }
            is AdminTeamIntent.AdminToggled -> setAdmin(intent.member, intent.isAdmin)
            AdminTeamIntent.Retry -> observeTeam()
            AdminTeamIntent.BackClicked -> sendEffect(AdminTeamEffect.NavigateBack)
        }
    }

    private fun observeTeam() {
        teamJob?.cancel()
        teamJob = observeTeamUseCase.execute()
            .onStart { setState { copy(isLoading = true, error = null) } }
            .onEach { members -> setState { copy(isLoading = false, members = members) } }
            .catch {
                setState { copy(isLoading = false, error = uiText(R.string.admin_access_denied)) }
            }
            .launchIn(viewModelScope)
    }

    private fun setAdmin(member: TeamMemberModel, isAdmin: Boolean) {
        if (member.id == currentState.currentUserId || member.isAdmin == isAdmin) return
        setAdminUseCase.execute(member.id, isAdmin)
            .onEach { result ->
                val message = when (result) {
                    is NetworkResult.Success ->
                        uiText(if (isAdmin) R.string.admin_now_admin else R.string.admin_no_longer_admin, member.email)
                    is NetworkResult.Error -> uiText(R.string.admin_role_change_failed)
                }
                sendEffect(AdminTeamEffect.ShowMessage(message))
            }
            .launchIn(viewModelScope)
    }
}
