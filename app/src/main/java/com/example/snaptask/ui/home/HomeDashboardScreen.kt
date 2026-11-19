package com.example.snaptask.ui.home

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.snaptask.R
import com.example.snaptask.data.local.LocalScreenshotEntity
import com.example.snaptask.ui.components.BottomTab
import com.example.snaptask.ui.components.DoodleBackground
import com.example.snaptask.ui.components.SnapTaskBottomNavigation
import com.example.snaptask.ui.components.SnapTaskSearchField
import com.example.snaptask.ui.components.formatFileSize
import com.example.snaptask.ui.components.getCategoryIcon
import com.example.snaptask.ui.theme.AppFontFamily
import com.example.snaptask.ui.theme.SnapTaskColors
import com.example.snaptask.ui.theme.SnapTaskType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    fontFamily = AppFontFamily,
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
private val SectionTitle = inter(16.sp, FontWeight.SemiBold)
private val RecordTitle = inter(15.sp, FontWeight.SemiBold)
private val RecordMeta = inter(12.sp, lineHeight = 16.5.sp)

@Composable
fun HomeDashboardScreen(
    totalCount: Int = 0,
    screenshots: List<LocalScreenshotEntity> = emptyList(),
    onStartScan: () -> Unit,
    onOpenCategory: (String) -> Unit,
    onDeleteCategory: (String) -> Unit,
    onSelectScreenshot: (Long) -> Unit,
    onTabSelected: (BottomTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    var searchQuery by remember { mutableStateOf("") }
    val todayFormatted = remember {
        val sdf = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault())
        sdf.format(Date()).uppercase(Locale.getDefault())
    }

    // Group screenshots by category
    val categoryGroups = remember(screenshots, searchQuery) {
        val filtered = if (searchQuery.isBlank()) screenshots
        else screenshots.filter {
            it.displayName.contains(searchQuery, ignoreCase = true) ||
            it.category.contains(searchQuery, ignoreCase = true) ||
            (it.ocrText?.contains(searchQuery, ignoreCase = true) == true)
        }
        filtered.groupBy { it.category }
            .toList()
            .sortedByDescending { it.second.size }
    }

    val totalBytes = remember(screenshots) {
        screenshots.sumOf { it.size }
    }

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
                        text = todayFormatted,
                        style = DateHeader,
                        color = SnapTaskColors.Burgundy,
                    )
                    Text(
                        text = if (totalCount > 0) "$totalCount screenshots organized." else "A little lighter today.",
                        style = Headline,
                        color = SnapTaskColors.TextPrimary,
                    )
                }

                if (screenshots.isEmpty()) {
                    // Empty state: "No screenshots yet" with button "Scan my screenshots"
                    EmptyStateCard(onScan = onStartScan)
                } else {
                    // Search field
                    SnapTaskSearchField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = "Search categories or text",
                    )

                    // Top summary & Scan Again row
                    ScanAgainBar(
                        totalCount = totalCount,
                        totalSizeFormatted = formatFileSize(totalBytes),
                        onScanAgain = onStartScan,
                    )

                    // Categories section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Categories (${categoryGroups.size})",
                            style = SectionTitle,
                            color = SnapTaskColors.TextPrimary,
                        )
                    }

                    if (categoryGroups.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(SnapTaskColors.Surface)
                                .padding(24.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "No matching categories found.",
                                style = RecordTitle,
                                color = SnapTaskColors.TextSecondary,
                            )
                        }
                    } else {
                        categoryGroups.forEach { (categoryName, itemsInCategory) ->
                            val categoryBytes = itemsInCategory.sumOf { it.size }
                            CategoryCard(
                                category = categoryName,
                                count = itemsInCategory.size,
                                sizeFormatted = formatFileSize(categoryBytes),
                                previews = itemsInCategory.take(3),
                                onClick = { onOpenCategory(categoryName) },
                                onDelete = { onDeleteCategory(categoryName) },
                            )
                        }
                    }
                }
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
    }
}

@Composable
private fun EmptyStateCard(onScan: () -> Unit) {
    val shape = RoundedCornerShape(24.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(HomeColors.ActionCardBg)
            .padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(
            text = "No screenshots yet",
            style = ActionTitle,
            color = Color.White,
        )
        Text(
            text = "Your library is waiting to be organized. Scan your screenshots to classify them into clean categories.",
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
                .clickable(role = Role.Button, onClick = onScan),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Scan My Screenshots",
                style = ActionTitle.copy(fontSize = 15.sp),
                color = HomeColors.ActionCardBg,
            )
        }
    }
}

@Composable
private fun ScanAgainBar(
    totalCount: Int,
    totalSizeFormatted: String,
    onScanAgain: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SnapTaskColors.Surface)
            .border(1.dp, Color(0xFFF0EAE8), RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "$totalCount screenshots · $totalSizeFormatted",
                style = RecordTitle,
                color = SnapTaskColors.TextPrimary,
            )
            Text(
                text = "Classified by on-device AI",
                style = RecordMeta,
                color = SnapTaskColors.TextSecondary,
            )
        }

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(SnapTaskColors.Burgundy)
                .clickable(role = Role.Button, onClick = onScanAgain)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_scan_line),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = "Scan again",
                style = inter(13.sp, FontWeight.SemiBold),
                color = Color.White,
            )
        }
    }
}

@Composable
private fun CategoryCard(
    category: String,
    count: Int,
    sizeFormatted: String,
    previews: List<LocalScreenshotEntity>,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    val shape = RoundedCornerShape(20.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SnapTaskColors.Surface)
            .border(1.dp, Color(0xFFF0EAE8), shape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF8EDF0)),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(getCategoryIcon(category)),
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = category,
                        style = RecordTitle.copy(fontSize = 16.sp),
                        color = SnapTaskColors.TextPrimary,
                    )
                    Text(
                        text = "$count screenshot${if (count > 1) "s" else ""} · $sizeFormatted",
                        style = RecordMeta,
                        color = SnapTaskColors.TextSecondary,
                    )
                }
            }

            // Delete button for this category
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(role = Role.Button, onClick = onDelete),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_trash),
                    contentDescription = "Delete $category",
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        // Thumbnail previews
        if (previews.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                previews.forEach { item ->
                    AsyncImage(
                        model = item.contentUri,
                        contentDescription = item.displayName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(width = 58.dp, height = 68.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFEDE7DE)),
                    )
                }
            }
        }
    }
}
