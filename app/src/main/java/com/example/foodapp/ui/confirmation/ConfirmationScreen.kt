package com.example.foodapp.ui.confirmation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.foodapp.domain.model.OrderStatus
import com.example.foodapp.ui.components.BoucheeIcons
import com.example.foodapp.ui.components.OutlinedCard
import com.example.foodapp.ui.components.PrimaryButton
import com.example.foodapp.ui.theme.FoodAppTheme
import com.example.foodapp.ui.theme.Spacing
import com.example.foodapp.ui.theme.Success
import com.example.foodapp.ui.theme.SuccessContainer

@Composable
fun ConfirmationRoute(
    onNavigateToHome: () -> Unit,
    viewModel: ConfirmationViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                ConfirmationEffect.NavigateToHome -> onNavigateToHome()
            }
        }
    }

    // Le retour système ramène aussi à l'accueil (le paiement n'a plus de sens).
    BackHandler { viewModel.onIntent(ConfirmationIntent.BackToHomeClicked) }

    ConfirmationScreen(state = state, onIntent = viewModel::onIntent)
}

@Composable
fun ConfirmationScreen(
    state: ConfirmationState,
    onIntent: (ConfirmationIntent) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.surface)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(start = Spacing.xl, end = Spacing.xl, top = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(SuccessContainer)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Success)
                ) {
                    Icon(BoucheeIcons.Check, contentDescription = null, tint = colors.onPrimary, modifier = Modifier.size(40.dp))
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(Spacing.s)) {
                Text("Commande confirmée", style = MaterialTheme.typography.headlineMedium.copy(fontSize = MaterialTheme.typography.headlineMedium.fontSize * 1.07f), textAlign = TextAlign.Center)
                Text(
                    "Merci ! Le restaurant prépare votre commande n° ${state.orderNumber}.",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = MaterialTheme.typography.bodyLarge.fontSize * 0.94f),
                    color = colors.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(300.dp)
                )
            }

            OutlinedCard(shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(Spacing.xl)) {
                    state.steps.forEachIndexed { index, step ->
                        TimelineStep(step = step, isLast = index == state.steps.lastIndex)
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(start = Spacing.xl, end = Spacing.xl, top = Spacing.l, bottom = Spacing.xxl)) {
            PrimaryButton(
                text = "Retour à l'accueil",
                onClick = { onIntent(ConfirmationIntent.BackToHomeClicked) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/** OrderTimeline : pastille + trait vertical + libellés. */
@Composable
private fun TimelineStep(step: OrderStep, isLast: Boolean) {
    val colors = MaterialTheme.colorScheme
    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val dotModifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
            when (step.status) {
                StepStatus.Done -> Box(dotModifier.background(Success), contentAlignment = Alignment.Center) {
                    Icon(BoucheeIcons.Check, contentDescription = null, tint = colors.onPrimary, modifier = Modifier.size(14.dp))
                }
                StepStatus.Current -> Box(dotModifier.border(7.dp, colors.primary, CircleShape))
                StepStatus.Upcoming -> Box(dotModifier.border(2.dp, colors.outlineVariant, CircleShape))
            }
            if (!isLast) {
                Box(
                    Modifier
                        .width(2.dp)
                        .height(30.dp)
                        .background(if (step.status == StepStatus.Done) Success else colors.outlineVariant)
                )
            }
        }
        Column(modifier = Modifier.padding(top = 2.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                step.label,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = if (step.status == StepStatus.Upcoming) FontWeight.Medium else FontWeight.Bold
                ),
                color = if (step.status == StepStatus.Upcoming) colors.onSurfaceVariant else colors.onSurface
            )
            Text(step.subtitle, style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun ConfirmationPreview() {
    FoodAppTheme {
        ConfirmationScreen(
            state = ConfirmationState(orderNumber = "482913", orderStatus = OrderStatus.PREPARING),
            onIntent = {}
        )
    }
}
