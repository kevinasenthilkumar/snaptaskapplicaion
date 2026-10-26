package com.example.snaptask.ui.welcome

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.snaptask.R
import com.example.snaptask.ui.components.SnapTaskPrimaryButton
import com.example.snaptask.ui.theme.SnapTaskColors
import com.example.snaptask.ui.theme.SnapTaskType

// CSS "ease-in-out" used by the Figma animation
private val FigmaEaseInOut = CubicBezierEasing(0.42f, 0f, 0.58f, 1f)

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SnapTaskColors.Background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        WelcomeAppBar()

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            BrandEyebrow()
            WelcomeEmblem()

            Text(
                text = "Save the moment.\nNot the clutter.",
                style = SnapTaskType.Headline,
                color = SnapTaskColors.TextPrimary,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = "Meet SnapTask. Your screenshots, neatly organized by AI and ready for what’s next.",
                style = SnapTaskType.Body,
                color = SnapTaskColors.TextSecondary,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.weight(1f))

            SnapTaskPrimaryButton(
                label = "Get started",
                onClick = onGetStarted,
                leadingIcon = painterResource(R.drawable.ic_arrow_right),
            )
            Text(
                text = "Your memories stay yours. You stay in control.",
                style = SnapTaskType.Caption,
                color = SnapTaskColors.TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        MascotNook()
    }
}

@Composable
private fun WelcomeAppBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_scan_line),
            contentDescription = null,
            modifier = Modifier.size(22.dp),
        )
        Text(
            text = "SnapTask",
            style = SnapTaskType.AppBarTitle,
            color = SnapTaskColors.Burgundy,
        )
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.End,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_sparkles_appbar),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun BrandEyebrow() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_sparkles_eyebrow),
            contentDescription = null,
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = "A LITTLE LESS DIGITAL CLUTTER",
            style = SnapTaskType.Eyebrow,
            color = SnapTaskColors.Burgundy,
        )
    }
}

@Composable
private fun WelcomeEmblem() {
    val transition = rememberInfiniteTransition(label = "welcome")

    val mascotLift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2000
                0f at 0 using FigmaEaseInOut
                -6f at 800 using FigmaEaseInOut
                0f at 1600
                0f at 2000
            },
        ),
        label = "mascotLift",
    )

    val sparkleAlpha by transition.animateFloat(
        initialValue = 0.55f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2000
                0.55f at 0 using FigmaEaseInOut
                1f at 650 using FigmaEaseInOut
                0.55f at 1600
                0.55f at 2000
            },
        ),
        label = "sparkleAlpha",
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clipToBounds(),
    ) {
        Image(
            painter = painterResource(R.drawable.img_emblem_halo),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 30.dp)
                .size(width = 278.dp, height = 252.dp),
        )
        Image(
            painter = painterResource(R.drawable.img_pocket_doll_mascot),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(x = 2.dp, y = 86.dp)
                .size(width = 100.dp, height = 125.dp)
                .graphicsLayer { translationY = mascotLift.dp.toPx() },
        )
        Image(
            painter = painterResource(R.drawable.ic_sparkles_accent),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(x = 109.dp, y = 76.dp)
                .size(26.dp)
                .graphicsLayer { alpha = sparkleAlpha },
        )
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 23.dp, y = 223.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(SnapTaskColors.Surface)
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_check),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = "Saved. Sorted. Handled.",
                style = SnapTaskType.BadgeLabel,
                color = SnapTaskColors.Burgundy,
            )
        }
    }
}

@Composable
private fun MascotNook() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp),
    ) {
        Image(
            painter = painterResource(R.drawable.img_pocket_doll_mascot_small),
            contentDescription = null,
            modifier = Modifier
                .offset(x = 71.dp, y = 1.dp)
                .size(width = 18.dp, height = 22.5.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 892)
@Composable
private fun WelcomeScreenPreview() {
    WelcomeScreen(onGetStarted = {})
}