package com.example.snaptask.ui.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snaptask.R
import com.example.snaptask.ui.components.BottomTab
import com.example.snaptask.ui.components.DoodleBackground
import com.example.snaptask.ui.components.SnapTaskBottomNavigation
import com.example.snaptask.ui.components.SnapTaskInfoNote
import com.example.snaptask.ui.components.SnapTaskSecondaryButton
import com.example.snaptask.ui.components.SnapTaskSwitch
import com.example.snaptask.ui.theme.InterFamily
import com.example.snaptask.ui.theme.SnapTaskColors
import com.example.snaptask.ui.theme.SnapTaskType

private fun inter(
    size: TextUnit,
    weight: FontWeight = FontWeight.Normal,
    lineHeight: TextUnit = TextUnit.Unspecified,
) = TextStyle(
    fontFamily = InterFamily,
    fontWeight = weight,
    fontSize = size,
    lineHeight = lineHeight,
    letterSpacing = 0.sp,
)

private val Heading = inter(32.sp, lineHeight = 37.76.sp)
private val SectionTitle = inter(14.sp, FontWeight.SemiBold)
private val RowTitle = inter(14.sp, FontWeight.SemiBold)
private val RowSubtitle = inter(12.sp, lineHeight = 18.sp)

@Composable
fun PermissionsPrivacyScreen(
    onBack: () -> Unit,
    onOpenPrivacyPolicy: () -> Unit,
    onTabSelected: (BottomTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    var aiOrgChecked by remember { mutableStateOf(true) }
    var shareAnalyticsChecked by remember { mutableStateOf(false) }

    DoodleBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            HeaderBar(title = "Permissions & privacy", onBack = onBack)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Text(text = "Your data, your rules.", style = Heading, color = SnapTaskColors.TextPrimary)

                Text(text = "Android permissions", style = SectionTitle, color = SnapTaskColors.TextPrimary)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(SnapTaskColors.Surface)
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    PermissionNavRow(
                        icon = R.drawable.ic_nav_library,
                        title = "Photos & videos",
                        subtitle = "Selected screenshots only · Manage access",
                    )
                    PermissionNavRow(
                        icon = R.drawable.ic_bell,
                        title = "Notifications",
                        subtitle = "Not enabled on this device · Open settings",
                    )
                }

                Text(text = "Privacy controls", style = SectionTitle, color = SnapTaskColors.TextPrimary)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(SnapTaskColors.Surface)
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    SwitchRow(
                        title = "AI organization",
                        subtitle = "Allow reading screenshot text to suggest categories and dates.",
                        checked = aiOrgChecked,
                        onCheckedChange = { aiOrgChecked = it },
                    )
                    SwitchRow(
                        title = "Share anonymous analytics",
                        subtitle = "Optional usage insights. Off by default.",
                        checked = shareAnalyticsChecked,
                        onCheckedChange = { shareAnalyticsChecked = it },
                    )
                    PermissionNavRow(
                        icon = R.drawable.ic_trash,
                        title = "Clear AI organization data",
                        subtitle = "Remove extracted text and categories, not photos.",
                    )
                }

                SnapTaskInfoNote(text = "Access, deletion and notification scheduling require the Android app. These screens are design previews only.")

                SnapTaskSecondaryButton(
                    label = "Privacy policy",
                    onClick = onOpenPrivacyPolicy,
                )

                Text(
                    text = "Revoke photo access anytime in Android Settings.",
                    style = SnapTaskType.Caption,
                    color = SnapTaskColors.TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            SnapTaskBottomNavigation(
                selectedTab = BottomTab.Settings,
                onTabSelected = onTabSelected,
            )
        }
    }
}

@Composable
private fun HeaderBar(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_arrow_left),
            contentDescription = "Back",
            modifier = Modifier
                .size(22.dp)
                .clickable(role = Role.Button, onClick = onBack),
        )
        Text(text = title, style = SnapTaskType.AppBarTitle, color = SnapTaskColors.Burgundy)
        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.End) {
            Image(painter = painterResource(R.drawable.ic_sparkles_appbar), contentDescription = null, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun SwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(text = title, style = RowTitle, color = SnapTaskColors.TextPrimary)
            Text(text = subtitle, style = RowSubtitle, color = SnapTaskColors.TextSecondary)
        }
        SnapTaskSwitch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun PermissionNavRow(
    icon: Int,
    title: String,
    subtitle: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF8EDF0)),
            contentAlignment = Alignment.Center,
        ) {
            Image(painter = painterResource(icon), contentDescription = null, modifier = Modifier.size(18.dp))
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(text = title, style = RowTitle, color = SnapTaskColors.TextPrimary)
            Text(text = subtitle, style = RowSubtitle, color = SnapTaskColors.TextSecondary)
        }
        Image(painter = painterResource(R.drawable.ic_chevron_right), contentDescription = null, modifier = Modifier.size(18.dp))
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 892)
@Composable
private fun PermissionsPrivacyScreenPreview() {
    PermissionsPrivacyScreen(onBack = {}, onOpenPrivacyPolicy = {}, onTabSelected = {})
}
