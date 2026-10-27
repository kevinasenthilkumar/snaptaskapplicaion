package com.example.snaptask.ui.library

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
private val FilterChipText = inter(13.sp)
private val SectionTitle = inter(14.sp, FontWeight.SemiBold)
private val SectionSort = inter(13.sp)
private val CategoryText = inter(10.sp, FontWeight.SemiBold)
private val RecordTitle = inter(14.sp, FontWeight.SemiBold)
private val RecordMeta = inter(11.sp, lineHeight = 16.5.sp)

@Composable
fun DatedScreenshotsScreen(
    onBack: () -> Unit,
    onOpenNonDatedTab: () -> Unit,
    onSelectRecord: () -> Unit,
    onTabSelected: (BottomTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    var query by remember { mutableStateOf("") }

    DoodleBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            LibraryAppBar(onBack = onBack)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Text(
                    text = "Dated screenshots",
                    style = Heading,
                    color = SnapTaskColors.TextPrimary,
                )
                Text(
                    text = "42 screenshots · ordered by event date",
                    style = Subtitle,
                    color = SnapTaskColors.TextSecondary,
                )

                SnapTaskSearchField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = "Search screenshots, dates, places",
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PillTab(label = "Dated · 42", selected = true, onClick = {})
                    PillTab(label = "Non-dated · 86", selected = false, onClick = onOpenNonDatedTab)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PillChip(label = "All", selected = true)
                    PillChip(label = "Tickets", selected = false)
                    PillChip(label = "Travel", selected = false)
                    PillChip(label = "Bookings", selected = false)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = "This week", style = SectionTitle, color = SnapTaskColors.TextPrimary)
                    Text(text = "Date ↑", style = SectionSort, color = SnapTaskColors.Burgundy)
                }

                LibraryRecordRow(
                    category = "Tickets",
                    title = "Jazz at the Garden",
                    meta = "Today · deletes 17 Oct",
                    onClick = onSelectRecord,
                )
            }

            SnapTaskBottomNavigation(
                selectedTab = BottomTab.Library,
                onTabSelected = onTabSelected,
            )
        }
    }
}

@Composable
private fun LibraryAppBar(onBack: () -> Unit) {
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
            text = "Library",
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
private fun PillTab(label: String, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(100.dp)
    Box(
        modifier = Modifier
            .height(36.dp)
            .clip(shape)
            .background(if (selected) SnapTaskColors.Burgundy else SnapTaskColors.Surface)
            .clickable(role = Role.Tab, onClick = onClick)
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = FilterChipText,
            color = if (selected) Color.White else SnapTaskColors.TextSecondary,
        )
    }
}

@Composable
private fun PillChip(label: String, selected: Boolean) {
    val shape = RoundedCornerShape(100.dp)
    Box(
        modifier = Modifier
            .height(32.dp)
            .clip(shape)
            .background(if (selected) SnapTaskColors.Burgundy else SnapTaskColors.Surface)
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = FilterChipText,
            color = if (selected) Color.White else SnapTaskColors.TextSecondary,
        )
    }
}

@Composable
private fun LibraryRecordRow(
    category: String,
    title: String,
    meta: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SnapTaskColors.Surface)
            .clickable(role = Role.Button, onClick = onClick)
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
            Text(text = category, style = CategoryText, color = SnapTaskColors.Burgundy)
            Text(text = title, style = RecordTitle, color = SnapTaskColors.TextPrimary)
            Text(text = meta, style = RecordMeta, color = SnapTaskColors.TextSecondary)
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
private fun DatedScreenshotsScreenPreview() {
    DatedScreenshotsScreen(
        onBack = {},
        onOpenNonDatedTab = {},
        onSelectRecord = {},
        onTabSelected = {},
    )
}
