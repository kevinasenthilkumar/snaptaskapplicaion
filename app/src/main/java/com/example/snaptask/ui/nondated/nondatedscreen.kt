package com.example.snaptask.ui.nondated

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
import com.example.snaptask.ui.components.SnapTaskPrimaryButton
import com.example.snaptask.ui.theme.InterFamily
import com.example.snaptask.ui.theme.SnapTaskColors
import com.example.snaptask.ui.theme.SnapTaskType

// Colors used only on this screen
private object NonDatedColors {
    val PhotoCard = Color(0xFFEDE7DE)
    val Tint = Color(0xFFF8EDF0)
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
private val Supporting14 = inter(14.sp, lineHeight = 21.sp)
private val Mini5 = inter(5.sp)
private val Mini8 = inter(8.sp)
private val Category = inter(10.sp, FontWeight.SemiBold)
private val RecordTitle = inter(14.sp, FontWeight.SemiBold)
private val RecordMeta = inter(11.sp, lineHeight = 16.5.sp)
private val DateText = inter(11.sp)
private val EventTitle = inter(14.sp, FontWeight.SemiBold, 21.sp)
private val EventBody = inter(12.sp, lineHeight = 18.sp)
private val NoteText = inter(12.sp, lineHeight = 18.sp)

@Composable
fun NonDatedScreen(
    onBack: () -> Unit,
    onChooseAccess: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SnapTaskColors.Background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        NonDatedAppBar(onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "HOW IT WORKS · 3 OF 3",
                style = SectionLabel,
                color = SnapTaskColors.Burgundy,
            )
            Text(
                text = "No date?\nGive it seven days.",
                style = Heading,
                color = SnapTaskColors.TextPrimary,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = "Ideas, products and recipes get a week from the day you save them.",
                style = Supporting14,
                color = SnapTaskColors.TextSecondary,
                modifier = Modifier.fillMaxWidth(),
            )

            ScreenshotRecord()
            RetentionTimeline()
            InformationNote()

            Spacer(Modifier.weight(1f))

            SnapTaskPrimaryButton(
                label = "Choose screenshot access",
                onClick = onChooseAccess,
                leadingIcon = painterResource(R.drawable.ic_arrow_right),
            )
        }

        MascotNook()
    }
}

@Composable
private fun NonDatedAppBar(onBack: () -> Unit) {
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
            text = "SnapTask",
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
private fun ScreenshotRecord() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SnapTaskColors.Surface)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LampThumbnail()
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "Shopping",
                style = Category,
                color = SnapTaskColors.Burgundy,
                softWrap = false,
            )
            Text(
                text = "Arc table lamp",
                style = RecordTitle,
                color = SnapTaskColors.TextPrimary,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = "Saved 10 Oct · deletes 17 Oct",
                style = RecordMeta,
                color = SnapTaskColors.TextSecondary,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Image(
            painter = painterResource(R.drawable.ic_chevron_right),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
        )
    }
}

@Composable
private fun LampThumbnail() {
    Column(
        modifier = Modifier
            .size(width = 64.dp, height = 78.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(NonDatedColors.PhotoCard)
            .padding(6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = "FORM & FIELD",
            style = Mini5,
            color = SnapTaskColors.TextSecondary,
            softWrap = false,
        )
        Image(
            painter = painterResource(R.drawable.img_screenshot_lamp),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
                .clip(RoundedCornerShape(4.dp)),
        )
        Text(
            text = "Arc table lamp",
            style = Mini8,
            color = SnapTaskColors.TextPrimary,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = "Brushed brass • £89.00",
            style = Mini5,
            color = SnapTaskColors.TextSecondary,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun RetentionTimeline() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SnapTaskColors.Surface)
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        TimelineEvent(
            date = "10 OCT",
            title = "Screenshot saved",
            body = "Your seven-day window begins.",
        )
        TimelineEvent(
            date = "16 OCT",
            title = "A gentle heads-up",
            body = "Day six: notified one day before deletion.",
        )
        TimelineEvent(
            date = "17 OCT",
            title = "Eligible for deletion",
            body = "Day seven after being saved.",
        )
    }
}

@Composable
private fun TimelineEvent(date: String, title: String, body: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .width(58.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(NonDatedColors.Tint)
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = date,
                style = DateText,
                color = SnapTaskColors.Burgundy,
                softWrap = false,
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Text(
                text = title,
                style = EventTitle,
                color = SnapTaskColors.TextPrimary,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = body,
                style = EventBody,
                color = SnapTaskColors.TextSecondary,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun InformationNote() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(NonDatedColors.Tint)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_info),
            contentDescription = null,
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = "Non-dated rule: notify on day six, delete on day seven. Kept items are excluded.",
            style = NoteText,
            color = SnapTaskColors.Burgundy,
            modifier = Modifier.weight(1f),
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
                .offset(x = 212.dp, y = 1.dp)
                .size(width = 18.dp, height = 22.5.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 892)
@Composable
private fun NonDatedScreenPreview() {
    NonDatedScreen(onBack = {}, onChooseAccess = {})
}