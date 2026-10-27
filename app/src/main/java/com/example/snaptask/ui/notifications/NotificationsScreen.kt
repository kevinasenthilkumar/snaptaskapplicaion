package com.example.snaptask.ui.notifications

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snaptask.R
import com.example.snaptask.ui.components.BottomTab
import com.example.snaptask.ui.components.DoodleBackground
import com.example.snaptask.ui.components.SnapTaskBottomNavigation
import com.example.snaptask.ui.components.SnapTaskSecondaryButton
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
private val SectionMeta = inter(13.sp)
private val CardTitle = inter(14.sp, FontWeight.SemiBold)
private val CardMeta = inter(12.sp, lineHeight = 18.sp)
private val CardTime = inter(11.sp)

@Composable
fun NotificationsScreen(
    onBack: () -> Unit,
    onReviewScreenshots: () -> Unit,
    onManagePreferences: () -> Unit,
    onTabSelected: (BottomTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    DoodleBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            NotificationsAppBar(onBack)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Text(text = "Your gentle nudges.", style = Heading, color = SnapTaskColors.TextPrimary)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PillChip(label = "All", selected = true)
                    PillChip(label = "Unread · 2", selected = false)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = "Today · 16 Oct", style = SectionTitle, color = SnapTaskColors.TextPrimary)
                    Text(text = "Mark all read", style = SectionMeta, color = SnapTaskColors.Burgundy)
                }

                NotificationCard(
                    title = "Two screenshots expire tomorrow",
                    body = "Jazz at the Garden and Arc table lamp are eligible for deletion on 17 Oct. Keep them if you need more time.",
                    time = "09:00",
                    onAction = onReviewScreenshots,
                )

                NotificationCard(
                    title = "A little space is waiting",
                    body = "6 older screenshots are ready for review. You could free 18.4 MB. Automatic cleanup is off.",
                    time = "08:45",
                    onAction = onReviewScreenshots,
                )

                SnapTaskSecondaryButton(
                    label = "Manage notification preferences",
                    onClick = onManagePreferences,
                )
            }

            SnapTaskBottomNavigation(
                selectedTab = BottomTab.Reminders,
                onTabSelected = onTabSelected,
            )
        }
    }
}

@Composable
private fun NotificationsAppBar(onBack: () -> Unit) {
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
        Text(
            text = "Notifications",
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
private fun PillChip(label: String, selected: Boolean) {
    val shape = RoundedCornerShape(100.dp)
    Box(
        modifier = Modifier
            .height(34.dp)
            .clip(shape)
            .background(if (selected) SnapTaskColors.Burgundy else SnapTaskColors.Surface)
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = inter(13.sp),
            color = if (selected) Color.White else SnapTaskColors.TextSecondary,
        )
    }
}

@Composable
private fun NotificationCard(
    title: String,
    body: String,
    time: String,
    onAction: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SnapTaskColors.Surface)
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                Image(painter = painterResource(R.drawable.ic_bell), contentDescription = null, modifier = Modifier.size(18.dp))
                Text(text = title, style = CardTitle, color = SnapTaskColors.TextPrimary)
            }
            Text(text = time, style = CardTime, color = SnapTaskColors.TextSecondary)
        }
        Text(text = body, style = CardMeta, color = SnapTaskColors.TextSecondary)
        Text(
            text = "Review screenshots →",
            style = inter(13.sp, FontWeight.SemiBold),
            color = SnapTaskColors.Burgundy,
            modifier = Modifier.clickable(role = Role.Button, onClick = onAction),
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 892)
@Composable
private fun NotificationsScreenPreview() {
    NotificationsScreen(
        onBack = {},
        onReviewScreenshots = {},
        onManagePreferences = {},
        onTabSelected = {},
    )
}
