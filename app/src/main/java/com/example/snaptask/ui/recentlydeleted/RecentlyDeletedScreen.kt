package com.example.snaptask.ui.recentlydeleted

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.snaptask.ui.components.SnapTaskInfoNote
import com.example.snaptask.ui.components.SnapTaskPrimaryButton
import com.example.snaptask.ui.components.SnapTaskSecondaryButton
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
fun RecentlyDeletedScreen(
    trashedItems: List<LocalScreenshotEntity> = emptyList(),
    onBack: () -> Unit,
    onRestoreAll: () -> Unit,
    onDeleteForever: () -> Unit,
    onTabSelected: (BottomTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val totalBytes = trashedItems.sumOf { it.size }

    DoodleBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            HeaderBar(title = "Recently deleted", onBack = onBack)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Text(text = "A little safety net.", style = Heading, color = SnapTaskColors.TextPrimary)
                Text(
                    text = "${trashedItems.size} screenshots · ${formatFileSize(totalBytes)} in recovery",
                    style = Subtitle,
                    color = SnapTaskColors.TextSecondary,
                )

                SnapTaskInfoNote(text = "Items moved to Android Gallery Trash can be restored within 30 days before permanent deletion.")

                if (trashedItems.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = "Deleted recently", style = SectionTitle, color = SnapTaskColors.TextPrimary)
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(SnapTaskColors.Surface)
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        trashedItems.forEach { item ->
                            DeletedItemRow(
                                title = item.displayName,
                                meta = "${item.category} · ${formatFileSize(item.size)}",
                                contentUri = item.contentUri,
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        SnapTaskPrimaryButton(
                            label = "Restore all",
                            onClick = onRestoreAll,
                            leadingIcon = painterResource(R.drawable.ic_restore),
                            modifier = Modifier.weight(1f),
                        )
                        SnapTaskSecondaryButton(
                            label = "Delete forever",
                            onClick = onDeleteForever,
                            modifier = Modifier.weight(1f),
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
                            text = "No recently deleted screenshots in recovery.",
                            style = Subtitle,
                            color = SnapTaskColors.TextSecondary,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
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
private fun DeletedItemRow(title: String, meta: String, contentUri: String? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
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
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 892)
@Composable
private fun RecentlyDeletedScreenPreview() {
    RecentlyDeletedScreen(
        onBack = {},
        onRestoreAll = {},
        onDeleteForever = {},
        onTabSelected = {},
    )
}
