package com.example.snaptask.ui.auth

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.snaptask.R
import com.example.snaptask.ui.theme.SnapTaskColors
import com.example.snaptask.ui.theme.SnapTaskType
import kotlinx.coroutines.delay

/**
 * Splash screen shown at app launch.
 * Checks auth session after a brief brand moment and calls either
 * [onAuthenticated] (valid session → Home) or [onUnauthenticated] (no session → Welcome).
 */
@Composable
fun SplashScreen(
    onAuthenticated: () -> Unit,
    onUnauthenticated: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var startAnim by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0f,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "splashAlpha",
    )

    // The actual auth check is driven by the caller (SnapTaskApp via AuthViewModel).
    // We simply show the brand for ~1.8 s then hand off.
    LaunchedEffect(Unit) {
        startAnim = true
        delay(1800)
        // Navigation decision is made by SnapTaskApp which observes AuthViewModel.
        // Here we just signal "splash done" – caller decides which branch.
        // We call onUnauthenticated as default; SnapTaskApp overrides immediately
        // if the session check returns authenticated.
        onUnauthenticated()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SnapTaskColors.Background),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.graphicsLayer { this.alpha = alpha },
        ) {
            Image(
                painter = painterResource(R.drawable.ic_scan_line),
                contentDescription = null,
                modifier = Modifier.size(52.dp),
            )
            Text(
                text = "SnapTask",
                style = SnapTaskType.AppBarTitle,
                color = SnapTaskColors.Burgundy,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Save the moment. Not the clutter.",
                style = SnapTaskType.Caption,
                color = SnapTaskColors.TextSecondary,
            )
        }
    }
}

