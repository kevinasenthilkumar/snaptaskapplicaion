package com.example.snaptask.ui.settings

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
private val Subtitle = inter(14.sp, lineHeight = 21.sp)
private val SectionTitle = inter(14.sp, FontWeight.SemiBold)
private val RowTitle = inter(14.sp, FontWeight.SemiBold)
private val RowSubtitle = inter(12.sp, lineHeight = 18.sp)

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onOpenAutomation: () -> Unit,
    onOpenRules: () -> Unit,
    onOpenRecentlyDeleted: () -> Unit,
    onOpenPermissions: () -> Unit,
    onTabSelected: (BottomTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    var remindersChecked by remember { mutableStateOf(true) }
    var summariesChecked by remember { mutableStateOf(false) }

    DoodleBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            HeaderBar(title = "Settings", onBack = onBack)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Text(text = "Make room, your way.", style = Heading, color = SnapTaskColors.TextPrimary)
                Text(
                    text = "Small preferences. A calmer library.",
                    style = Subtitle,
                    color = SnapTaskColors.TextSecondary,
                )

                Text(text = "Notifications", style = SectionTitle, color = SnapTaskColors.TextPrimary)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(SnapTaskColors.Surface)
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    SwitchRow(
                        title = "Cleanup reminders",
                        subtitle = "One day before each deletion date",
                        checked = remindersChecked,
                        onCheckedChange = { remindersChecked = it },
                    )
                    SwitchRow(
                        title = "Cleanup summaries",
                        subtitle = "A recap after each cleanup",
                        checked = summariesChecked,
                        onCheckedChange = { summariesChecked = it },
                    )
                }

                Text(text = "Your cleanup preferences", style = SectionTitle, color = SnapTaskColors.TextPrimary)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(SnapTaskColors.Surface)
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    NavRow(
                        icon = R.drawable.ic_nav_home,
                        title = "Automation",
                        subtitle = "Never run automatically",
                        onClick = onOpenAutomation,
                    )
                    NavRow(
                        icon = R.drawable.ic_row_list_checks,
                        title = "Cleanup rules",
                        subtitle = "Dated +1 day · Non-dated +7 days",
                        onClick = onOpenRules,
                    )
                    NavRow(
                        icon = R.drawable.ic_trash,
                        title = "Recently deleted",
                        subtitle = "6 screenshots · 30-day recovery",
                        onClick = onOpenRecentlyDeleted,
                    )
                }

                Text(text = "Your data", style = SectionTitle, color = SnapTaskColors.TextPrimary)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(SnapTaskColors.Surface)
                        .padding(18.dp),
                ) {
                    NavRow(
                        icon = R.drawable.ic_row_shield_check,
                        title = "Permissions & privacy",
                        subtitle = "Access, AI processing and data controls",
                        onClick = onOpenPermissions,
                    )
                }

                Text(
                    text = "SnapTask · v1.0.0 · Designed for a lighter library",
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
private fun NavRow(
    @DrawableRes icon: Int,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onClick),
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
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(text = title, style = RowTitle, color = SnapTaskColors.TextPrimary)
            Text(text = subtitle, style = RowSubtitle, color = SnapTaskColors.TextSecondary)
        }
        Image(
            painter = painterResource(R.drawable.ic_chevron_right),
            contentDescription = null,
            modifier = Modifier.size(18.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 892)
@Composable
private fun SettingsScreenPreview() {
    SettingsScreen(
        onBack = {},
        onOpenAutomation = {},
        onOpenRules = {},
        onOpenRecentlyDeleted = {},
        onOpenPermissions = {},
        onTabSelected = {},
    )
}
