package com.example.foodapp.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.foodapp.R
import com.example.foodapp.ui.theme.FoodAppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Écran de démarrage : le logo Restou au centre pendant quelques secondes, puis la suite. */
@Composable
fun SplashRoute(onFinished: () -> Unit) {
    val currentOnFinished by rememberUpdatedState(onFinished)
    LaunchedEffect(Unit) {
        delay(SPLASH_DURATION_MS)
        currentOnFinished()
    }
    SplashScreen()
}

@Composable
fun SplashScreen() {
    val scale = remember { Animatable(0.85f) }
    val alpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        launch { alpha.animateTo(1f, tween(600)) }
        scale.animateTo(1f, tween(800))
    }
    Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(R.drawable.ic_splash),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier
                    .size(280.dp)
                    .graphicsLayer {
                        scaleX = scale.value
                        scaleY = scale.value
                        this.alpha = alpha.value
                    }
            )
        }
    }
}

private const val SPLASH_DURATION_MS = 3000L

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun SplashPreview() {
    FoodAppTheme { SplashScreen() }
}
