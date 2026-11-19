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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.snaptask.R
import com.example.snaptask.data.local.LocalScreenshotEntity
import com.example.snaptask.ui.components.DoodleBackground
import com.example.snaptask.ui.components.SnapTaskInfoNote
import com.example.snaptask.ui.components.SnapTaskSecondaryButton
import com.example.snaptask.ui.theme.InterFamily
import com.example.snaptask.ui.theme.SnapTaskColors
import com.example.snaptask.ui.theme.SnapTaskType
import com.example.snaptask.viewmodel.ScanUiState

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

@Composable
fun ScreenshotScanningScreen(
    onBack: () -> Unit,
    onCancelScan: () -> Unit,
    scanUiState: ScanUiState,
    onStartScan: () -> Unit,
    onScanComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isPreview = LocalInspectionMode.current
    if (!isPreview) {
        LaunchedEffect(Unit) {
            onStartScan()
        }

        LaunchedEffect(scanUiState.isComplete) {
            if (scanUiState.isComplete) {
                onScanComplete()
            }
        }
    }

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

                RealScreenshotPreviews(screenshots = scanUiState.recentScreenshots)

                ProgressCard(
                    current = scanUiState.scannedCount,
                    total = scanUiState.totalCount,
                    progress = scanUiState.progress,
                    statusText = scanUiState.statusText,
                )

                ScanningDetailsCard(
                    totalFound = scanUiState.totalCount,
                    uploaded = scanUiState.uploadedCount,
                    failed = scanUiState.failedCount,
                    remaining = scanUiState.remainingCount,
                )

                SnapTaskInfoNote(
                    text = if (scanUiState.totalCount > 0)
                        "Processing & uploading ${scanUiState.totalCount} actual screenshots to your library."
                    else
                        "Scanning device storage for real screenshots..."
                )

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
private fun RealScreenshotPreviews(screenshots: List<LocalScreenshotEntity>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
    ) {
        if (screenshots.isEmpty()) {
            Box(
                modifier = Modifier
                    .size(width = 200.dp, height = 78.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ScanningColors.LampCard),
                contentAlignment = Alignment.Center
            ) {
                Text("Searching device storage...", style = RowSubtitle, color = SnapTaskColors.TextSecondary)
            }
        } else {
            screenshots.take(3).forEach { item ->
                AsyncImage(
                    model = item.contentUri,
                    contentDescription = item.displayName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(width = 64.dp, height = 78.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ScanningColors.LampCard)
                )
            }
        }
    }
}

@Composable
private fun ProgressCard(current: Int, total: Int, progress: Float, statusText: String) {
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
                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                    .clip(RoundedCornerShape(4.dp))
                    .background(SnapTaskColors.Burgundy),
            )
        }

        Text(
            text = if (statusText.isNotBlank()) statusText else "Processing & categorizing screenshots...",
            style = ProgressStatus,
            color = SnapTaskColors.TextSecondary,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun ScanningDetailsCard(
    totalFound: Int,
    uploaded: Int,
    failed: Int,
    remaining: Int,
) {
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
            title = "Screenshots detected: $totalFound",
            subtitle = "From your device gallery MediaStore",
        )
        DetailRow(
            icon = R.drawable.ic_scan_line,
            title = "Upload status: $uploaded uploaded · $failed failed",
            subtitle = "$remaining screenshots remaining to sync",
        )
        DetailRow(
            icon = R.drawable.ic_sparkles_accent,
            title = "AI on-device categorization",
            subtitle = "Classifying into 10 dedicated categories",
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
    ScreenshotScanningScreen(
        onBack = {},
        onCancelScan = {},
        scanUiState = ScanUiState(
            isScanning = true,
            totalCount = 12,
            scannedCount = 8,
            progress = 0.66f,
        ),
        onStartScan = {},
        onScanComplete = {},
    )
}
