package com.example.snaptask.ui.library

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.snaptask.ui.components.ALL_CATEGORIES
import com.example.snaptask.ui.components.BottomTab
import com.example.snaptask.ui.components.DoodleBackground
import com.example.snaptask.ui.components.SnapTaskBottomNavigation
import com.example.snaptask.ui.components.SnapTaskSearchField
import com.example.snaptask.ui.components.formatFileSize
import com.example.snaptask.ui.components.getCategoryIcon
import com.example.snaptask.ui.theme.AppFontFamily
import com.example.snaptask.ui.theme.SnapTaskColors
import com.example.snaptask.ui.theme.SnapTaskType

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

private val Heading = inter(28.sp, lineHeight = 34.sp)
private val Subtitle = inter(14.sp, lineHeight = 21.sp)
private val TabLabel = inter(13.sp, FontWeight.SemiBold)
private val CategoryTag = inter(10.sp, FontWeight.SemiBold)
private val RecordTitle = inter(15.sp, FontWeight.SemiBold)
private val RecordMeta = inter(12.sp, lineHeight = 16.5.sp)
private val SectionTitle = inter(16.sp, FontWeight.SemiBold)

@Composable
fun DatedScreenshotsScreen(
    items: List<LocalScreenshotEntity> = emptyList(),
    initialCategory: String = "All",
    onBack: () -> Unit,
    onOpenNonDatedTab: () -> Unit = {},
    onSelectRecord: (Long) -> Unit = {},
    onDeleteRecord: (Long) -> Unit = {},
    onDeleteCategory: (String) -> Unit = {},
    onTabSelected: (BottomTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    var query by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember(initialCategory) { mutableStateOf(initialCategory) }

    val filteredItems = remember(items, query, selectedCategoryFilter) {
        var list = if (query.isBlank()) items
        else items.filter {
            it.displayName.contains(query, ignoreCase = true) ||
            it.category.contains(query, ignoreCase = true) ||
            (it.ocrText?.contains(query, ignoreCase = true) == true)
        }
        if (selectedCategoryFilter != "All") {
            list = list.filter { it.category.equals(selectedCategoryFilter, ignoreCase = true) }
        }
        list
    }

    val totalSize = remember(filteredItems) {
        formatFileSize(filteredItems.sumOf { it.size })
    }

    val isFolderView = selectedCategoryFilter != "All"

    DoodleBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            HeaderBar(
                title = if (isFolderView) selectedCategoryFilter else "Screenshot Library",
                onBack = {
                    if (isFolderView) {
                        selectedCategoryFilter = "All"
                    } else {
                        onBack()
                    }
                },
                onDelete = if (isFolderView && filteredItems.isNotEmpty()) {
                    { onDeleteCategory(selectedCategoryFilter) }
                } else null
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                item {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = if (isFolderView) selectedCategoryFilter else "Your Screenshot Library",
                        style = Heading,
                        color = SnapTaskColors.TextPrimary,
                    )
                    Text(
                        text = if (isFolderView)
                            "${filteredItems.size} screenshots · $totalSize"
                        else
                            "${items.size} total screenshots classified across categories",
                        style = Subtitle,
                        color = SnapTaskColors.TextSecondary,
                        modifier = Modifier.padding(top = 4.dp, bottom = 10.dp),
                    )

                    // Delete category action button if inside a folder
                    if (isFolderView && filteredItems.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFFF8EDF0))
                                .border(1.dp, Color(0xFFF0DCE2), RoundedCornerShape(14.dp))
                                .clickable(role = Role.Button, onClick = { onDeleteCategory(selectedCategoryFilter) })
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.ic_trash),
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                )
                                Text(
                                    text = "Delete all in $selectedCategoryFilter",
                                    style = TabLabel,
                                    color = SnapTaskColors.Burgundy,
                                )
                            }
                            Text(
                                text = "${filteredItems.size} items",
                                style = RecordMeta,
                                color = SnapTaskColors.Burgundy,
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                    }

                    SnapTaskSearchField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = if (isFolderView) "Search in $selectedCategoryFilter" else "Search screenshots or text",
                    )
                    Spacer(Modifier.height(10.dp))

                    // Pill tabs for fast category navigation
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        PillTab(
                            label = "All · ${items.size}",
                            selected = selectedCategoryFilter == "All",
                            onClick = { selectedCategoryFilter = "All" },
                        )
                        ALL_CATEGORIES.forEach { cat ->
                            val catCount = items.count { it.category.equals(cat, ignoreCase = true) }
                            PillTab(
                                label = "$cat · $catCount",
                                selected = selectedCategoryFilter.equals(cat, ignoreCase = true),
                                onClick = { selectedCategoryFilter = cat },
                            )
                        }
                    }
                }

                if (isFolderView) {
                    // We are inside a specific category folder
                    if (filteredItems.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(SnapTaskColors.Surface)
                                    .border(1.dp, Color(0xFFF0EAE8), RoundedCornerShape(18.dp))
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(Color(0xFFF8EDF0)),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Image(
                                            painter = painterResource(getCategoryIcon(selectedCategoryFilter)),
                                            contentDescription = null,
                                            modifier = Modifier.size(28.dp),
                                        )
                                    }
                                    Text(
                                        text = "Empty folder",
                                        style = RecordTitle,
                                        color = SnapTaskColors.TextPrimary,
                                    )
                                    Text(
                                        text = "No screenshots categorized as $selectedCategoryFilter yet.",
                                        style = RecordMeta,
                                        color = SnapTaskColors.TextSecondary,
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            }
                        }
                    } else {
                        items(filteredItems, key = { it.id }) { item ->
                            LibraryRecordRow(
                                category = item.category,
                                title = item.displayName,
                                meta = "${formatFileSize(item.size)} · Confidence ${(item.confidence * 100).toInt()}%",
                                contentUri = item.contentUri,
                                onClick = { onSelectRecord(item.id) },
                                onDelete = { onDeleteRecord(item.id) },
                            )
                        }
                    }
                } else {
                    // Root view: Shows "No Screenshots Found" empty state IF library has 0 items
                    if (items.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(SnapTaskColors.Surface)
                                    .border(1.dp, Color(0xFFF0EAE8), RoundedCornerShape(18.dp))
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    Text(
                                        text = "No Screenshots Found",
                                        style = RecordTitle.copy(fontSize = 16.sp),
                                        color = SnapTaskColors.TextPrimary,
                                    )
                                    Text(
                                        text = "Scan your device screenshots from Home to populate your folders.",
                                        style = RecordMeta,
                                        color = SnapTaskColors.TextSecondary,
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            }
                        }
                    }

                    // Below the "No Screenshots Found" / overview:
                    // DISPLAY ALL CATEGORIES INDIVIDUALLY AS FOLDERS
                    item {
                        Text(
                            text = "Category Folders (${ALL_CATEGORIES.size})",
                            style = SectionTitle,
                            color = SnapTaskColors.TextPrimary,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                        Text(
                            text = "All defined categories appear as folders. Folders update dynamically when screenshots are categorized.",
                            style = RecordMeta,
                            color = SnapTaskColors.TextSecondary,
                            modifier = Modifier.padding(bottom = 6.dp),
                        )
                    }

                    items(ALL_CATEGORIES) { categoryName ->
                        val catItems = items.filter { it.category.equals(categoryName, ignoreCase = true) }
                        CategoryFolderCard(
                            category = categoryName,
                            count = catItems.size,
                            sizeFormatted = if (catItems.isEmpty()) "Empty folder" else formatFileSize(catItems.sumOf { it.size }),
                            previews = catItems.take(3),
                            onClick = { selectedCategoryFilter = categoryName },
                            onDelete = if (catItems.isNotEmpty()) {
                                { onDeleteCategory(categoryName) }
                            } else null,
                        )
                    }
                }

                item {
                    Spacer(Modifier.height(16.dp))
                }
            }

            SnapTaskBottomNavigation(
                selectedTab = BottomTab.Library,
                onTabSelected = onTabSelected,
            )
        }
    }
}

@Composable
private fun HeaderBar(title: String, onBack: () -> Unit, onDelete: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
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
        }

        if (onDelete != null) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(role = Role.Button, onClick = onDelete),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_trash),
                    contentDescription = "Delete category",
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Composable
private fun PillTab(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val bg = if (selected) SnapTaskColors.Burgundy else SnapTaskColors.Surface
    val textColor = if (selected) Color.White else SnapTaskColors.TextSecondary
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .border(1.dp, if (selected) SnapTaskColors.Burgundy else Color(0xFFF0EAE8), RoundedCornerShape(999.dp))
            .clickable(role = Role.Tab, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = label, style = TabLabel, color = textColor)
    }
}

@Composable
private fun CategoryFolderCard(
    category: String,
    count: Int,
    sizeFormatted: String,
    previews: List<LocalScreenshotEntity>,
    onClick: () -> Unit,
    onDelete: (() -> Unit)? = null,
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
        verticalArrangement = Arrangement.spacedBy(10.dp),
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
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (count > 0) Color(0xFFF8EDF0) else Color(0xFFF5F3F0)),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(getCategoryIcon(category)),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = category,
                        style = RecordTitle,
                        color = SnapTaskColors.TextPrimary,
                    )
                    Text(
                        text = if (count == 0) "Empty folder · 0 screenshots" else "$count screenshots · $sizeFormatted",
                        style = RecordMeta,
                        color = if (count > 0) SnapTaskColors.TextSecondary else Color(0xFF9E9296),
                    )
                }
            }

            if (onDelete != null) {
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
            } else {
                Image(
                    painter = painterResource(R.drawable.ic_chevron_right),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
            }
        }

        // Preview images if available
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
                            .size(width = 56.dp, height = 66.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFEDE7DE)),
                    )
                }
            }
        }
    }
}

@Composable
private fun LibraryRecordRow(
    category: String,
    title: String,
    meta: String,
    contentUri: String,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SnapTaskColors.Surface)
            .border(1.dp, Color(0xFFF0EAE8), RoundedCornerShape(16.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = contentUri,
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(width = 44.dp, height = 54.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFEDE7DE)),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(text = category, style = CategoryTag, color = SnapTaskColors.Burgundy)
            Text(text = title, style = RecordTitle, color = SnapTaskColors.TextPrimary)
            Text(text = meta, style = RecordMeta, color = SnapTaskColors.TextSecondary)
        }
        Image(
            painter = painterResource(R.drawable.ic_trash),
            contentDescription = "Delete screenshot",
            modifier = Modifier
                .size(20.dp)
                .clickable(role = Role.Button, onClick = onDelete),
        )
    }
}
