package com.example.snaptask

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.snaptask.ui.howdated.HowDatedScreen
import com.example.snaptask.ui.nondated.NonDatedScreen
import com.example.snaptask.ui.onboarding.OnboardingScreen
import com.example.snaptask.ui.permission.GalleryPermissionScreen
import com.example.snaptask.ui.welcome.WelcomeScreen

private enum class Screen { Welcome, Onboarding, HowDated, NonDated, GalleryPermission }

@Composable
fun SnapTaskApp() {
    var screen by rememberSaveable { mutableStateOf(Screen.Welcome) }

    when (screen) {
        Screen.Welcome -> WelcomeScreen(
            onGetStarted = { screen = Screen.Onboarding },
        )

        Screen.Onboarding -> {
            BackHandler { screen = Screen.Welcome }
            OnboardingScreen(
                onBack = { screen = Screen.Welcome },
                onHowItWorks = { screen = Screen.HowDated },
            )
        }

        Screen.HowDated -> {
            BackHandler { screen = Screen.Onboarding }
            HowDatedScreen(
                onBack = { screen = Screen.Onboarding },
                onNext = { screen = Screen.NonDated },
            )
        }

        Screen.NonDated -> {
            BackHandler { screen = Screen.HowDated }
            NonDatedScreen(
                onBack = { screen = Screen.HowDated },
                onChooseAccess = { screen = Screen.GalleryPermission },
            )
        }

        Screen.GalleryPermission -> {
            BackHandler { screen = Screen.NonDated }
            GalleryPermissionScreen(
                onBack = { screen = Screen.NonDated },
                onAllow = { /* real Android permission request goes here later */ },
                onDontAllow = { screen = Screen.NonDated },
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 892)
@Composable
private fun SnapTaskAppPreview() {
    SnapTaskApp()
}