package com.example.snaptask.ui.cleanup

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.snaptask.R
import com.example.snaptask.data.local.LocalScreenshotEntity
import com.example.snaptask.ui.components.BottomTab
import com.example.snaptask.ui.components.DoodleBackground
import com.example.snaptask.ui.components.SnapTaskBottomNavigation
import com.example.snaptask.ui.components.SnapTaskPrimaryButton
import com.example.snaptask.ui.components.formatFileSize
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

private val Heading = inter(32.sp, lineHeight = 37.76.sp)
private val Subtitle = inter(14.sp, lineHeight = 21.sp)
private val SectionTitle = inter(14.sp, FontWeight.SemiBold)
private val SectionMeta = inter(13.sp)
private val ItemTitle = inter(14.sp, FontWeight.SemiBold)
private val ItemMeta = inter(11.sp, lineHeight = 16.5.sp)

@Composable
fun CleanupReviewScreen(
    screenshots: List<LocalScreenshotEntity> = emptyList(),
    onBack: () -> Unit,
    onDeleteConfirmed: (List<Long>) -> Unit = {},
    onTabSelected: (BottomTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedIds by remember(screenshots) {
        mutableStateOf(screenshots.map { it.id }.toSet())
    }

    val selectedScreenshots = remember(screenshots, selectedIds) {
        screenshots.filter { it.id in selectedIds }
    }

    val totalBytes = remember(selectedScreenshots) {
        selectedScreenshots.sumOf { it.size }
    }

    DoodleBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            CleanupAppBar(onBack)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Text(text = "Keep what matters.", style = Heading, color = SnapTaskColors.TextPrimary)
                Text(
                    text = if (screenshots.isNotEmpty())
                        "Review ${screenshots.size} screenshots before deleting."
                    else
                        "No screenshots eligible for cleanup yet.",
                    style = Subtitle,
                    color = SnapTaskColors.TextSecondary,
                )

                if (screenshots.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "${selectedIds.size} selected · ${formatFileSize(totalBytes)}",
                            style = SectionTitle,
                            color = SnapTaskColors.Burgundy,
                        )
                        Text(
                            text = if (selectedIds.size == screenshots.size) "Deselect all" else "Select all",
                            style = SectionMeta,
                            color = SnapTaskColors.Burgundy,
                            modifier = Modifier.clickable(role = Role.Button) {
                                selectedIds = if (selectedIds.size == screenshots.size) emptySet()
                                else screenshots.map { it.id }.toSet()
                            }
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(SnapTaskColors.Surface)
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        screenshots.forEach { item ->
                            val isChecked = item.id in selectedIds
                            CleanupItemRow(
                                title = item.displayName,
                                meta = "${item.category} · ${formatFileSize(item.size)}",
                                contentUri = item.contentUri,
                                checked = isChecked,
                                onToggle = {
                                    selectedIds = if (isChecked) selectedIds - item.id
                                    else selectedIds + item.id
                                }
                            )
                        }
                    }

                    if (selectedIds.isNotEmpty()) {
                        SnapTaskPrimaryButton(
                            label = "Delete ${selectedIds.size} screenshot${if (selectedIds.size > 1) "s" else ""}",
                            onClick = { onDeleteConfirmed(selectedIds.toList()) },
                            leadingIcon = painterResource(R.drawable.ic_trash),
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(SnapTaskColors.Surface)
                            .padding(32.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Your library is clean. Nothing to delete.",
                            style = Subtitle,
                            color = SnapTaskColors.TextSecondary,
                            textAlign = TextAlign.Center,
                        )
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
private fun CleanupAppBar(onBack: () -> Unit) {
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
            text = "Take Action",
            style = SnapTaskType.AppBarTitle,
            color = SnapTaskColors.Burgundy,
        )
    }
}

@Composable
private fun CleanupItemRow(
    title: String,
    meta: String,
    contentUri: String? = null,
    checked: Boolean,
    onToggle: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Checkbox, onClick = onToggle),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (contentUri != null) {
            AsyncImage(
                model = contentUri,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(width = 38.dp, height = 48.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFEDE7DE)),
            )
        } else {
            Image(
                painter = painterResource(R.drawable.img_screenshot_lamp),
                contentDescription = null,
                modifier = Modifier
                    .size(width = 38.dp, height = 48.dp)
                    .clip(RoundedCornerShape(6.dp)),
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(text = title, style = ItemTitle, color = SnapTaskColors.TextPrimary)
            Text(text = meta, style = ItemMeta, color = SnapTaskColors.TextSecondary)
        }
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(if (checked) SnapTaskColors.Burgundy else Color.Transparent)
                .border(1.dp, if (checked) SnapTaskColors.Burgundy else Color(0xFFC7B8BC), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (checked) {
                Image(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 892)
@Composable
private fun CleanupReviewScreenPreview() {
    CleanupReviewScreen(
        onBack = {},
        onDeleteConfirmed = {},
        onTabSelected = {},
    )
}
