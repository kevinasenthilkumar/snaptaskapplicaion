package com.example.snaptask.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snaptask.R
import com.example.snaptask.ui.components.SnapTaskPrimaryButton
import com.example.snaptask.ui.theme.InterFamily
import com.example.snaptask.ui.theme.SnapTaskColors
import com.example.snaptask.ui.theme.SnapTaskType

/**
 * Profile Setup — shown only once, immediately after first-time account creation.
 * Allows the new user to pick a display name and optionally a colour avatar before
 * continuing to the existing onboarding flow.
 */
@Composable
fun ProfileSetupScreen(
    onBack: () -> Unit,
    onProfileSaved: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    var displayName by remember { mutableStateOf("") }

    val avatarColors = listOf(
        Color(0xFF721D38),
        Color(0xFFD4855A),
        Color(0xFF4A7B6F),
        Color(0xFF4A5E8A),
        Color(0xFF7A4A8A),
    )
    var selectedColor by remember { mutableStateOf(avatarColors[0]) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SnapTaskColors.Background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        // App bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_scan_line),
                contentDescription = null,
                modifier = Modifier.size(22.dp),
            )
            Text("SnapTask", style = SnapTaskType.AppBarTitle, color = SnapTaskColors.Burgundy)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onSkip)
                    .padding(4.dp),
            ) {
                Text(
                    "Skip",
                    style = TextStyle(
                        fontFamily = InterFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                    ),
                    color = SnapTaskColors.TextSecondary,
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Spacer(Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_sparkles_eyebrow),
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                )
                Text("ONE LAST THING", style = SnapTaskType.Eyebrow, color = SnapTaskColors.Burgundy)
            }

            Text(
                text = "Set up your\nprofile.",
                style = TextStyle(
                    fontFamily = InterFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 36.sp,
                    lineHeight = 44.sp,
                    letterSpacing = 0.sp,
                ),
                color = SnapTaskColors.TextPrimary,
            )

            Text(
                text = "How should SnapTask greet you?",
                style = SnapTaskType.Body,
                color = SnapTaskColors.TextSecondary,
            )

            // Avatar preview
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(selectedColor),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = displayName.take(1).uppercase().ifEmpty { "?" },
                    style = TextStyle(
                        fontFamily = InterFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 32.sp,
                        color = Color.White,
                    ),
                )
            }

            // Colour picker
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                avatarColors.forEach { color ->
                    val isSelected = color == selectedColor
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(color)
                            .then(
                                if (isSelected) Modifier.border(2.dp, SnapTaskColors.TextPrimary, CircleShape)
                                else Modifier
                            )
                            .clickable { selectedColor = color },
                    )
                }
            }

            AuthTextField(
                value = displayName,
                onValueChange = { displayName = it },
                label = "Display name",
                imeAction = ImeAction.Done,
                onImeAction = { focusManager.clearFocus() },
            )

            Spacer(Modifier.height(4.dp))

            SnapTaskPrimaryButton(
                label = "Save & continue",
                onClick = {
                    focusManager.clearFocus()
                    onProfileSaved()
                },
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 892)
@Composable
private fun ProfileSetupScreenPreview() {
    ProfileSetupScreen(onBack = {}, onProfileSaved = {}, onSkip = {})
}

