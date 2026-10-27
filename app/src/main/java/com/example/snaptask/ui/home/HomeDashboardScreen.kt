package com.example.snaptask.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import com.example.snaptask.ui.components.SnapTaskSearchField
import com.example.snaptask.ui.theme.InterFamily
import com.example.snaptask.ui.theme.SnapTaskColors
import com.example.snaptask.ui.theme.SnapTaskType

private object HomeColors {
    val ActionCardBg = Color(0xFF721D38)
    val ActionCardSubtitle = Color(0xFFE5B6C3)
    val CategoryBurgundy = Color(0xFF721D38)
}

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

private val DateHeader = inter(11.sp, FontWeight.SemiBold)
private val Headline = inter(34.sp, lineHeight = 40.12.sp)
private val ActionTitle = inter(22.sp, FontWeight.SemiBold, 28.sp)
private val ActionMeta = inter(13.sp)
private val ActionBody = inter(14.sp, lineHeight = 21.sp)
private val StatNumber = inter(28.sp, FontWeight.Normal)
private val StatLabel = inter(14.sp, FontWeight.SemiBold)
private val SectionTitle = inter(16.sp, FontWeight.SemiBold)
private val SectionLink = inter(13.sp)
private val Category = inter(10.sp, FontWeight.SemiBold)
private val RecordTitle = inter(14.sp, FontWeight.SemiBold)
private val RecordMeta = inter(11.sp, lineHeight = 16.5.sp)

@Composable
fun HomeDashboardScreen(
    onTakeAction: () -> Unit,
    onOpenDated: () -> Unit,
    onOpenNonDated: () -> Unit,
    onOpenReminders: () -> Unit,
    onTabSelected: (BottomTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    var searchQuery by remember { mutableStateOf("") }

    DoodleBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            HomeAppBar()

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "FRIDAY, 16 OCTOBER 2026",
                        style = DateHeader,
                        color = SnapTaskColors.Burgundy,
                    )
                    Text(
                        text = "A little lighter today.",
                        style = Headline,
                        color = SnapTaskColors.TextPrimary,
                    )
                }

                SnapTaskSearchField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = "Search screenshots, dates, places",
                )

                TakeActionCard(onTakeAction = onTakeAction)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    StatCard(
                        number = "42",
                        label = "Dated →",
                        onClick = onOpenDated,
                        modifier = Modifier.weight(1f),
                    )
                    StatCard(
                        number = "86",
                        label = "Non-dated →",
                        onClick = onOpenNonDated,
                        modifier = Modifier.weight(1f),
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Coming up",
                            style = SectionTitle,
                            color = SnapTaskColors.TextPrimary,
                        )
                        Text(
                            text = "View reminders",
                            style = SectionLink,
                            color = SnapTaskColors.Burgundy,
                            modifier = Modifier.clickable(role = Role.Button, onClick = onOpenReminders),
                        )
                    }

                    ComingUpRecord(
                        category = "Tickets",
                        title = "Jazz at the Garden",
                        meta = "Today · deletes 17 Oct",
                        thumbnail = R.drawable.img_screenshot_lamp,
                    )
                    ComingUpRecord(
                        category = "Shopping",
                        title = "Arc table lamp",
                        meta = "Saved 10 Oct · deletes 17 Oct",
                        thumbnail = R.drawable.img_screenshot_lamp,
                    )
                }

                Text(
                    text = "Automatic cleanup is off · You’re in control",
                    style = SnapTaskType.Caption,
                    color = SnapTaskColors.TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                )
            }

            SnapTaskBottomNavigation(
                selectedTab = BottomTab.Home,
                onTabSelected = onTabSelected,
            )
        }
    }
}

@Composable
private fun HomeAppBar() {
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
                painter = painterResource(R.drawable.ic_bell),
                contentDescription = "Notifications",
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

@Composable
private fun TakeActionCard(onTakeAction: () -> Unit) {
    val shape = RoundedCornerShape(24.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(HomeColors.ActionCardBg)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                text = "6 ready to let go",
                style = ActionTitle,
                color = Color.White,
            )
            Text(
                text = "18.4 MB",
                style = ActionMeta,
                color = HomeColors.ActionCardSubtitle,
            )
        }
        Text(
            text = "Their time is up. Review before you remove.",
            style = ActionBody,
            color = HomeColors.ActionCardSubtitle,
        )

        val btnShape = RoundedCornerShape(16.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(btnShape)
                .background(Color.White)
                .clickable(role = Role.Button, onClick = onTakeAction),
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_row_list_checks),
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = "Take Action",
                style = SnapTaskType.ButtonLabel,
                color = SnapTaskColors.Burgundy,
            )
        }
    }
}

@Composable
private fun StatCard(
    number: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(20.dp)
    Column(
        modifier = modifier
            .clip(shape)
            .background(SnapTaskColors.Surface)
            .border(1.dp, Color(0xFFE9DFDF), shape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = number,
            style = StatNumber,
            color = SnapTaskColors.Burgundy,
        )
        Text(
            text = label,
            style = StatLabel,
            color = SnapTaskColors.TextPrimary,
        )
    }
}

@Composable
private fun ComingUpRecord(
    category: String,
    title: String,
    meta: String,
    thumbnail: Int,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SnapTaskColors.Surface)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(thumbnail),
            contentDescription = title,
            modifier = Modifier
                .size(width = 44.dp, height = 54.dp)
                .clip(RoundedCornerShape(8.dp)),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = category,
                style = Category,
                color = HomeColors.CategoryBurgundy,
            )
            Text(
                text = title,
                style = RecordTitle,
                color = SnapTaskColors.TextPrimary,
            )
            Text(
                text = meta,
                style = RecordMeta,
                color = SnapTaskColors.TextSecondary,
            )
        }
        Image(
            painter = painterResource(R.drawable.ic_chevron_right),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 892)
@Composable
private fun HomeDashboardScreenPreview() {
    HomeDashboardScreen(
        onTakeAction = {},
        onOpenDated = {},
        onOpenNonDated = {},
        onOpenReminders = {},
        onTabSelected = {},
    )
}
