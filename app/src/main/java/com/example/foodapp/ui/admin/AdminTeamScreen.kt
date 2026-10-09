package com.example.foodapp.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.foodapp.domain.model.TeamMemberModel
import com.example.foodapp.ui.components.BackTopBar
import com.example.foodapp.ui.components.EmptyView
import com.example.foodapp.ui.components.ErrorView
import com.example.foodapp.ui.components.LoadingView
import com.example.foodapp.ui.components.OutlinedCard
import com.example.foodapp.ui.components.SearchField
import com.example.foodapp.ui.theme.FoodAppTheme
import com.example.foodapp.ui.theme.Spacing
import kotlinx.coroutines.launch

@Composable
fun AdminTeamRoute(
    onNavigateBack: () -> Unit,
    viewModel: AdminTeamViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                AdminTeamEffect.NavigateBack -> onNavigateBack()
                is AdminTeamEffect.ShowMessage -> {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    scope.launch { snackbarHostState.showSnackbar(effect.message) }
                }
            }
        }
    }

    AdminTeamScreen(state = state, snackbarHostState = snackbarHostState, onIntent = viewModel::onIntent)
}

/** Admin : liste des comptes clients, avec un interrupteur pour donner / retirer le rôle admin. */
@Composable
fun AdminTeamScreen(
    state: AdminTeamState,
    snackbarHostState: SnackbarHostState,
    onIntent: (AdminTeamIntent) -> Unit
) {
    Scaffold(
        topBar = {
            BackTopBar(
                title = "Équipe",
                onBack = { onIntent(AdminTeamIntent.BackClicked) },
                modifier = Modifier.statusBarsPadding()
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        when {
            state.isLoading -> LoadingView(Modifier.padding(padding))
            state.error != null -> ErrorView(
                message = state.error,
                onRetry = { onIntent(AdminTeamIntent.Retry) },
                modifier = Modifier.padding(padding)
            )
            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                SearchField(
                    value = state.query,
                    onValueChange = { onIntent(AdminTeamIntent.QueryChanged(it)) },
                    placeholder = "Rechercher un e-mail",
                    modifier = Modifier.padding(horizontal = Spacing.xl, vertical = Spacing.s)
                )
                if (state.filteredMembers.isEmpty()) {
                    EmptyView(message = "Aucun compte trouvé")
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(start = Spacing.xl, end = Spacing.xl, top = Spacing.s, bottom = Spacing.l),
                        verticalArrangement = Arrangement.spacedBy(Spacing.m)
                    ) {
                        items(state.filteredMembers, key = { it.id }) { member ->
                            TeamMemberRow(
                                member = member,
                                isCurrentUser = member.id == state.currentUserId,
                                onAdminChange = { onIntent(AdminTeamIntent.AdminToggled(member, it)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TeamMemberRow(member: TeamMemberModel, isCurrentUser: Boolean, onAdminChange: (Boolean) -> Unit) {
    val colors = MaterialTheme.colorScheme
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(horizontal = Spacing.l, vertical = Spacing.m),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.m)
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text(
                    member.name?.takeIf { it.isNotBlank() } ?: member.email,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    when {
                        isCurrentUser -> "${member.email} · vous"
                        else -> member.email
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    if (member.isAdmin) "Admin" else "Client",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (member.isAdmin) colors.primary else colors.onSurfaceVariant
                )
            }
            Switch(
                checked = member.isAdmin,
                onCheckedChange = onAdminChange,
                enabled = !isCurrentUser
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun AdminTeamPreview() {
    FoodAppTheme {
        AdminTeamScreen(
            state = AdminTeamState(
                isLoading = false,
                currentUserId = "1",
                members = listOf(
                    TeamMemberModel("1", "Chadi", "chadimarzouki@gmail.com", isAdmin = true),
                    TeamMemberModel("2", "Admin Demo", "admin.demo@bouchee.app", isAdmin = false),
                    TeamMemberModel("3", null, "client@exemple.fr", isAdmin = false)
                )
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onIntent = {}
        )
    }
}
