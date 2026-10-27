package com.example.snaptask.ui.reminders

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snaptask.R
import com.example.snaptask.ui.components.BottomTab
import com.example.snaptask.ui.components.DoodleBackground
import com.example.snaptask.ui.components.SnapTaskBottomNavigation
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
private val FilterText = inter(13.sp)
private val SectionTitle = inter(14.sp, FontWeight.SemiBold)
private val SectionMeta = inter(13.sp)
private val CardTitle = inter(14.sp, FontWeight.SemiBold)
private val CardSubtitle = inter(12.sp, lineHeight = 18.sp)

@Composable
fun RemindersScreen(
    onBack: () -> Unit,
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
            RemindersAppBar(onBack)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Text(text = "A gentle heads-up.", style = Heading, color = SnapTaskColors.TextPrimary)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PillFilter(label = "Upcoming", selected = true)
                    PillFilter(label = "Kept", selected = false)
                    PillFilter(label = "All dates", selected = false)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = "Today · 16 Oct", style = SectionTitle, color = SnapTaskColors.TextPrimary)
                    Text(text = "2 reminders", style = SectionMeta, color = SnapTaskColors.Burgundy)
                }

                ReminderRecord(
                    category = "Tickets",
                    title = "Jazz at the Garden",
                    meta = "Today · deletes 17 Oct",
                )
                ReminderRecord(
                    category = "Shopping",
                    title = "Arc table lamp",
                    meta = "Saved 10 Oct · deletes 17 Oct",
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFF8EDF0))
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(painter = painterResource(R.drawable.ic_bell), contentDescription = null, modifier = Modifier.size(18.dp))
                    Text(
                        text = "Both are eligible tomorrow, 17 Oct. Keep anything you still need.",
                        style = inter(12.sp, lineHeight = 18.sp),
                        color = SnapTaskColors.Burgundy,
                        modifier = Modifier.weight(1f),
                    )
                }

                Text(
                    text = "Reminder preview · no notifications scheduled",
                    style = SnapTaskType.Caption,
                    color = SnapTaskColors.TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
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
private fun RemindersAppBar(onBack: () -> Unit) {
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
            text = "Reminders",
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
private fun PillFilter(label: String, selected: Boolean) {
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
            style = FilterText,
            color = if (selected) Color.White else SnapTaskColors.TextSecondary,
        )
    }
}

@Composable
private fun ReminderRecord(category: String, title: String, meta: String) {
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
            painter = painterResource(R.drawable.img_screenshot_lamp),
            contentDescription = null,
            modifier = Modifier
                .size(width = 44.dp, height = 54.dp)
                .clip(RoundedCornerShape(8.dp)),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(text = category, style = inter(10.sp, FontWeight.SemiBold), color = SnapTaskColors.Burgundy)
            Text(text = title, style = CardTitle, color = SnapTaskColors.TextPrimary)
            Text(text = meta, style = CardSubtitle, color = SnapTaskColors.TextSecondary)
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
private fun RemindersScreenPreview() {
    RemindersScreen(onBack = {}, onTabSelected = {})
}
