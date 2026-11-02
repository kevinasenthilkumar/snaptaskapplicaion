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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snaptask.R
import com.example.snaptask.ui.components.SnapTaskPrimaryButton
import com.example.snaptask.ui.components.SnapTaskSecondaryButton
import com.example.snaptask.ui.theme.InterFamily
import com.example.snaptask.ui.theme.SnapTaskColors
import com.example.snaptask.ui.theme.SnapTaskType

/**
 * My Profile screen — accessible from Settings for authenticated users.
 * Shows account details and a sign-out option.
 * NOT shown during registration flow (that is ProfileSetupScreen).
 */
@Composable
fun MyProfileScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    userName: String = "",
    userEmail: String = "",
    modifier: Modifier = Modifier,
) {
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
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(role = Role.Button, onClick = onBack)
                    .padding(4.dp),
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_chevron_right),
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(20.dp)
                        .then(Modifier), // mirrored via scale if needed
                )
            }
            Text("My Profile", style = SnapTaskType.AppBarTitle, color = SnapTaskColors.TextPrimary)
            Spacer(Modifier.size(28.dp))
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(SnapTaskColors.Burgundy)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = userName.take(1).uppercase().ifEmpty { "?" },
                    style = TextStyle(
                        fontFamily = InterFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 32.sp,
                        color = Color.White,
                    ),
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SnapTaskColors.Surface)
                    .border(1.dp, Color(0xFFE9DFDF), RoundedCornerShape(16.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                ProfileRow(label = "Name", value = userName.ifEmpty { "—" })
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFFF0E8E8)),
                )
                ProfileRow(label = "Email", value = userEmail.ifEmpty { "—" })
            }

            Spacer(Modifier.height(8.dp))

            SnapTaskSecondaryButton(
                label = "Sign out",
                onClick = onLogout,
            )

            Text(
                text = "Signing out will clear your local session. Your data stays safely on the server.",
                style = SnapTaskType.Caption,
                color = SnapTaskColors.TextSecondary,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun ProfileRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = InterFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
            ),
            color = SnapTaskColors.TextSecondary,
        )
        Text(
            text = value,
            style = TextStyle(
                fontFamily = InterFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
            ),
            color = SnapTaskColors.TextPrimary,
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 892)
@Composable
private fun MyProfileScreenPreview() {
    MyProfileScreen(
        onBack = {},
        onLogout = {},
        userName = "Kevin",
        userEmail = "kevin@snaptask.app",
    )
}

