package com.example.snaptask.ui.scanning

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snaptask.R
import com.example.snaptask.ui.components.DoodleBackground
import com.example.snaptask.ui.components.SnapTaskInfoNote
import com.example.snaptask.ui.components.SnapTaskSecondaryButton
import com.example.snaptask.ui.theme.InterFamily
import com.example.snaptask.ui.theme.SnapTaskColors
import com.example.snaptask.ui.theme.SnapTaskType

private object ScanningColors {
    val TicketBackground = Color(0xFF3C1524)
    val TicketMuted = Color(0xFFE5B6C3)
    val Barcode = Color(0xFFF3E1E5)
    val LampCard = Color(0xFFEDE7DE)
    val RecipeCard = Color(0xFFE9EBD9)
    val IconTile = Color(0xFFF8EDF0)
    val ProgressTrack = Color(0xFFF3E1E5)
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

private val SectionLabel = inter(11.sp)
private val Heading = inter(32.sp, lineHeight = 37.76.sp)
private val Supporting = inter(14.sp, lineHeight = 21.sp)
private val ProgressCount = inter(32.sp, FontWeight.Normal, 37.76.sp)
private val ProgressPercentage = inter(14.sp, FontWeight.SemiBold)
private val ProgressStatus = inter(12.sp, lineHeight = 18.sp)
private val RowTitle = inter(14.sp, FontWeight.SemiBold, 21.sp)
private val RowSubtitle = inter(12.sp, lineHeight = 18.sp)
private val Mini5 = inter(5.sp)
private val Mini8 = inter(8.sp)

private val BarcodeWidths = listOf(3, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1)

@Composable
fun ScreenshotScanningScreen(
    onBack: () -> Unit,
    onCancelScan: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DoodleBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            ScanningAppBar(onBack)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "YOUR FIRST SORT",
                    style = SectionLabel,
                    color = SnapTaskColors.Burgundy,
                )
                Text(
                    text = "Finding the useful\nin the everyday.",
                    style = Heading,
                    color = SnapTaskColors.TextPrimary,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = "Looking for dates, details and a place for everything.",
                    style = Supporting,
                    color = SnapTaskColors.TextSecondary,
                    modifier = Modifier.fillMaxWidth(),
                )

                ScreenshotCollection()
                ProgressCard(current = 84, total = 128, progress = 0.66f)
                ScanningDetailsCard()
                SnapTaskInfoNote(text = "This is a scanning preview. No device gallery has been accessed.")

                Spacer(Modifier.weight(1f))

                SnapTaskSecondaryButton(
                    label = "Cancel scan",
                    onClick = onCancelScan,
                )
            }

            MascotNook()
        }
    }
}

@Composable
private fun ScanningAppBar(onBack: () -> Unit) {
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
            text = "Organizing",
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
private fun ScreenshotCollection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
    ) {
        TicketPreview()
        PhotoPreview(
            background = ScanningColors.LampCard,
            brand = "FORM & FIELD",
            photo = R.drawable.img_screenshot_lamp,
            title = "Arc table lamp",
            meta = "Brushed brass • £89.00",
        )
        PhotoPreview(
            background = ScanningColors.RecipeCard,
            brand = "THE SUNDAY TABLE",
            photo = R.drawable.img_screenshot_pasta,
            title = "Lemon ricotta pasta",
            meta = "20 min • serves 2",
        )
    }
}

@Composable
private fun PreviewCardShell(
    background: Color,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .size(width = 64.dp, height = 78.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(background)
            .padding(6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        content()
    }
}

@Composable
private fun TicketPreview() {
    PreviewCardShell(ScanningColors.TicketBackground) {
        Text(
            text = "THE GARDEN • LIVE",
            style = Mini5,
            color = ScanningColors.TicketMuted,
            softWrap = false,
        )
        Text(
            text = "JAZZ AT\nTHE GARDEN",
            style = Mini8,
            color = Color.White,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = "FRI 16 OCT 2026 • 19:30",
            style = Mini5,
            color = ScanningColors.TicketMuted,
            modifier = Modifier.fillMaxWidth(),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp),
            horizontalArrangement = Arrangement.spacedBy(1.dp),
        ) {
            BarcodeWidths.forEach { barWidth ->
                Box(
                    modifier = Modifier
                        .width(barWidth.dp)
                        .fillMaxHeight()
                        .background(ScanningColors.Barcode),
                )
            }
        }
    }
}

@Composable
private fun PhotoPreview(
    background: Color,
    brand: String,
    @DrawableRes photo: Int,
    title: String,
    meta: String,
) {
    PreviewCardShell(background) {
        Text(
            text = brand,
            style = Mini5,
            color = SnapTaskColors.TextSecondary,
            softWrap = false,
        )
        Image(
            painter = painterResource(photo),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
                .clip(RoundedCornerShape(4.dp)),
        )
        Text(
            text = title,
            style = Mini8,
            color = SnapTaskColors.TextPrimary,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = meta,
            style = Mini5,
            color = SnapTaskColors.TextSecondary,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun ProgressCard(current: Int, total: Int, progress: Float) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SnapTaskColors.Surface)
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(
                text = "$current of $total",
                style = ProgressCount,
                color = SnapTaskColors.Burgundy,
            )
            Text(
                text = "${(progress * 100).toInt()}%",
                style = ProgressPercentage,
                color = SnapTaskColors.Burgundy,
            )
        }

        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(ScanningColors.ProgressTrack),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .clip(RoundedCornerShape(4.dp))
                    .background(SnapTaskColors.Burgundy),
            )
        }

        Text(
            text = "Categorizing screenshots...",
            style = ProgressStatus,
            color = SnapTaskColors.TextSecondary,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun ScanningDetailsCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SnapTaskColors.Surface)
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        DetailRow(
            icon = R.drawable.ic_check,
            title = "Screenshots found",
            subtitle = "128 items in the example library",
        )
        DetailRow(
            icon = R.drawable.ic_sparkles_accent,
            title = "Reading dates & categories",
            subtitle = "You’ll review the results next.",
        )
    }
}

@Composable
private fun DetailRow(
    @DrawableRes icon: Int,
    title: String,
    subtitle: String,
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
                .background(ScanningColors.IconTile),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(19.dp),
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(text = title, style = RowTitle, color = SnapTaskColors.TextPrimary)
            Text(text = subtitle, style = RowSubtitle, color = SnapTaskColors.TextSecondary)
        }
        Image(
            painter = painterResource(R.drawable.ic_chevron_right),
            contentDescription = null,
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
private fun MascotNook() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp),
    ) {
        Image(
            painter = painterResource(R.drawable.img_pocket_doll_mascot_small),
            contentDescription = null,
            modifier = Modifier
                .offset(x = 259.dp, y = 1.dp)
                .size(width = 18.dp, height = 22.5.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 892)
@Composable
private fun ScreenshotScanningScreenPreview() {
    ScreenshotScanningScreen(onBack = {}, onCancelScan = {})
}
