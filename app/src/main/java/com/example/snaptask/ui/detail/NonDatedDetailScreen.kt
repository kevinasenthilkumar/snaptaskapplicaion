package com.example.snaptask.ui.detail

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
import androidx.compose.runtime.remember
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
import com.example.snaptask.ui.components.getCategoryIcon
import com.example.snaptask.ui.theme.AppFontFamily
import com.example.snaptask.ui.theme.SnapTaskColors
import com.example.snaptask.ui.theme.SnapTaskType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

private val Heading = inter(28.sp, lineHeight = 33.04.sp)
private val BadgeText = inter(13.sp)
private val CardTitle = inter(14.sp, FontWeight.SemiBold)
private val CardMeta = inter(12.sp, lineHeight = 18.sp)

@Composable
fun NonDatedDetailScreen(
    screenshot: LocalScreenshotEntity? = null,
    onBack: () -> Unit,
    onKeepThis: () -> Unit = {},
    onTakeAction: () -> Unit = {},
    onDelete: (Long) -> Unit = {},
    onTabSelected: (BottomTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val capturedDateStr = remember(screenshot?.capturedAt) {
        val captured = screenshot?.capturedAt
        if (captured != null && captured > 0) {
            SimpleDateFormat("d MMM yyyy, HH:mm", Locale.getDefault()).format(Date(captured * 1000L))
        } else {
            "Recent"
        }
    }

    DoodleBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            DetailAppBar(onBack)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                // Photo Card Preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFEDE7DE)),
                    contentAlignment = Alignment.Center,
                ) {
                    if (screenshot?.contentUri != null) {
                        AsyncImage(
                            model = screenshot.contentUri,
                            contentDescription = screenshot.displayName,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                        )
                    } else {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(text = "PREVIEW", style = inter(11.sp), color = SnapTaskColors.TextSecondary)
                            Image(
                                painter = painterResource(R.drawable.img_screenshot_lamp),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                            )
                        }
                    }
                }

                Text(
                    text = screenshot?.displayName ?: "Screenshot details",
                    style = Heading,
                    color = SnapTaskColors.TextPrimary
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DetailChip(label = "Categorized", filled = true)
                    DetailChip(label = screenshot?.category ?: "Shopping", filled = false)
                    if (screenshot != null && screenshot.confidence > 0f) {
                        DetailChip(
                            label = "${(screenshot.confidence * 100).toInt()}% match",
                            filled = false
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(SnapTaskColors.Surface)
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    DetailRow(
                        iconRes = getCategoryIcon(screenshot?.category ?: "Shopping"),
                        title = "Category · ${screenshot?.category ?: "Shopping"}",
                        subtitle = if (!screenshot?.ocrText.isNullOrBlank())
                            screenshot!!.ocrText!!.take(80).replace("\n", " ")
                        else "No date found in screenshot",
                        actionIcon = R.drawable.ic_chevron_right,
                    )
                    DetailRow(
                        iconRes = R.drawable.ic_nav_library,
                        title = "Captured · $capturedDateStr",
                        subtitle = "Size: ${formatFileSize(screenshot?.size ?: 0L)}",
                        actionIcon = R.drawable.ic_chevron_right,
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(SnapTaskColors.Surface)
                            .border(1.dp, Color(0xFFE9DFDF), RoundedCornerShape(16.dp))
                            .clickable(role = Role.Button, onClick = onKeepThis),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ic_keep),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                            Text(
                                text = "Keep this",
                                style = SnapTaskType.ButtonLabel,
                                color = SnapTaskColors.Burgundy,
                            )
                        }
                    }

                    SnapTaskPrimaryButton(
                        label = "Delete",
                        leadingIcon = painterResource(R.drawable.ic_trash),
                        onClick = {
                            if (screenshot != null) {
                                onDelete(screenshot.id)
                            } else {
                                onTakeAction()
                            }
                        },
                        modifier = Modifier.weight(1f),
                    )
                }

                Text(
                    text = "Saved · Tap Delete to request approval from gallery",
                    style = SnapTaskType.Caption,
                    color = SnapTaskColors.TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
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
private fun DetailAppBar(onBack: () -> Unit) {
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
            text = "Screenshot details",
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
private fun DetailChip(label: String, filled: Boolean) {
    val shape = RoundedCornerShape(100.dp)
    Box(
        modifier = Modifier
            .height(34.dp)
            .clip(shape)
            .background(if (filled) SnapTaskColors.Burgundy else SnapTaskColors.Surface)
            .border(1.dp, if (filled) SnapTaskColors.Burgundy else Color(0xFFE9DFDF), shape)
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = BadgeText,
            color = if (filled) Color.White else SnapTaskColors.TextSecondary,
        )
    }
}

@Composable
private fun DetailRow(
    iconRes: Int,
    title: String,
    subtitle: String,
    actionIcon: Int,
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
            Image(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(text = title, style = CardTitle, color = SnapTaskColors.TextPrimary)
            Text(text = subtitle, style = CardMeta, color = SnapTaskColors.TextSecondary)
        }
        Image(
            painter = painterResource(actionIcon),
            contentDescription = null,
            modifier = Modifier.size(18.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 892)
@Composable
private fun NonDatedDetailScreenPreview() {
    NonDatedDetailScreen(
        onBack = {},
        onKeepThis = {},
        onTakeAction = {},
        onTabSelected = {},
    )
}
