package com.example.foodapp.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.foodapp.ui.components.MealImage
import com.example.foodapp.ui.components.PrimaryButton
import com.example.foodapp.ui.theme.FoodAppTheme
import com.example.foodapp.ui.theme.Spacing

@Composable
fun OnboardingRoute(
    onNavigateToRegister: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                OnboardingEffect.NavigateToRegister -> onNavigateToRegister()
                OnboardingEffect.NavigateToLogin -> onNavigateToLogin()
            }
        }
    }

    OnboardingScreen(state = state, onIntent = viewModel::onIntent)
}

@Composable
fun OnboardingScreen(
    state: OnboardingState,
    onIntent: (OnboardingIntent) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.primary)
    ) {
        // Illustration : cercles concentriques + chips de catégories
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .statusBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .clip(CircleShape)
                    .background(colors.secondary),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(236.dp)
                        .clip(CircleShape)
                        .background(colors.surfaceContainer),
                    contentAlignment = Alignment.Center
                ) {
                    MealImage(
                        url = state.heroImageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(176.dp)
                            .clip(CircleShape)
                    )
                }
            }
            FloatingChip("Poulet", colors.surfaceContainer, colors.onSurface, Alignment.TopStart, x = 24.dp, y = 56.dp)
            FloatingChip("Pâtes", colors.onSurface, colors.surfaceContainer, Alignment.TopEnd, x = (-22).dp, y = 110.dp)
            FloatingChip("Dessert", colors.secondaryContainer, colors.onSurface, Alignment.BottomStart, x = 44.dp, y = (-64).dp)
            FloatingChip("Fruits de mer", colors.surfaceContainer, colors.onSurface, Alignment.BottomEnd, x = (-36).dp, y = (-92).dp)
        }

        // Feuille blanche du bas
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(colors.surfaceContainer)
                .navigationBarsPadding()
                .padding(start = Spacing.xxl, end = Spacing.xxl, top = Spacing.xxxl, bottom = Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.xl)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                PageDot(width = 24.dp, color = colors.primary)
                PageDot(width = 6.dp, color = colors.outlineVariant)
                PageDot(width = 6.dp, color = colors.outlineVariant)
            }
            Text(
                text = "Les plats du monde, livrés chez vous.",
                style = MaterialTheme.typography.displayMedium
            )
            Text(
                text = "Parcourez les recettes par catégorie, composez votre panier et commandez en quelques touches.",
                style = MaterialTheme.typography.bodyLarge,
                color = colors.onSurfaceVariant
            )
            Column {
                Spacer(Modifier.height(Spacing.xs))
                PrimaryButton(
                    text = "Commencer",
                    onClick = { onIntent(OnboardingIntent.StartClicked) },
                    modifier = Modifier.fillMaxWidth()
                )
                TextButton(
                    onClick = { onIntent(OnboardingIntent.AlreadyHaveAccountClicked) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        "J'ai déjà un compte",
                        style = MaterialTheme.typography.labelLarge,
                        color = colors.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun BoxScope.FloatingChip(
    label: String,
    background: Color,
    content: Color,
    alignment: Alignment,
    x: Dp,
    y: Dp
) {
    Surface(
        shape = CircleShape,
        color = background,
        contentColor = content,
        shadowElevation = if (background == MaterialTheme.colorScheme.surfaceContainer) 8.dp else 0.dp,
        modifier = Modifier
            .align(alignment)
            .padding(
                start = if (x > 0.dp) x else 0.dp,
                end = if (x < 0.dp) -x else 0.dp,
                top = if (y > 0.dp) y else 0.dp,
                bottom = if (y < 0.dp) -y else 0.dp
            )
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = MaterialTheme.typography.labelLarge.fontWeight),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        )
    }
}

@Composable
private fun PageDot(width: Dp, color: Color) {
    Box(
        Modifier
            .width(width)
            .height(6.dp)
            .clip(CircleShape)
            .background(color)
    )
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun OnboardingPreview() {
    FoodAppTheme { OnboardingScreen(state = OnboardingState(), onIntent = {}) }
}
