package com.example.snaptask.ui.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.snaptask.ui.components.SnapTaskPrimaryButton
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

private val Heading = inter(28.sp, lineHeight = 33.04.sp)
private val BadgeText = inter(13.sp)
private val CardTitle = inter(14.sp, FontWeight.SemiBold)
private val CardMeta = inter(12.sp, lineHeight = 18.sp)

@Composable
fun NonDatedDetailScreen(
    onBack: () -> Unit,
    onKeepThis: () -> Unit,
    onTakeAction: () -> Unit,
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
                        .height(200.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFEDE7DE))
                        .padding(16.dp),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "FORM & FIELD", style = inter(11.sp), color = SnapTaskColors.TextSecondary)
                        Image(
                            painter = painterResource(R.drawable.img_screenshot_lamp),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .clip(RoundedCornerShape(12.dp)),
                        )
                        Text(text = "Arc table lamp", style = inter(14.sp, FontWeight.SemiBold), color = SnapTaskColors.TextPrimary)
                    }
                }

                Text(text = "Arc table lamp", style = Heading, color = SnapTaskColors.TextPrimary)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DetailChip(label = "Non-dated", filled = true)
                    DetailChip(label = "Shopping", filled = false)
                    DetailChip(label = "Edit category ✏️", filled = false)
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
                        iconRes = R.drawable.ic_nav_library,
                        title = "Saved · 10 Oct 2026",
                        subtitle = "No event date found · Add a date",
                        actionIcon = R.drawable.ic_edit,
                    )
                    DetailRow(
                        iconRes = R.drawable.ic_bell,
                        title = "Notify · 16 Oct 2026",
                        subtitle = "Day six, one day before deletion.",
                        actionIcon = R.drawable.ic_chevron_right,
                    )
                    DetailRow(
                        iconRes = R.drawable.ic_trash,
                        title = "Delete · 17 Oct 2026",
                        subtitle = "Seven days after being saved.",
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
                        label = "Take Action",
                        onClick = onTakeAction,
                        modifier = Modifier.weight(1f),
                    )
                }

                Text(
                    text = "2.8 MB · Automatic cleanup off · Not deleted",
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
