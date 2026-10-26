package com.example.snaptask.ui.onboarding

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.ui.text.style.TextAlign
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
private object OnboardingColors {
    val TicketBackground = Color(0xFF3C1524)
    val TicketMuted = Color(0xFFE5B6C3)
    val Barcode = Color(0xFFF3E1E5)
    val LampCard = Color(0xFFEDE7DE)
    val RecipeCard = Color(0xFFE9EBD9)
    val IconTile = Color(0xFFF8EDF0)
    val PillBorder = Color(0xFFE9DFDF)
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
private val Heading = inter(34.sp, lineHeight = 40.12.sp)
private val Supporting = inter(14.sp, lineHeight = 21.sp)
private val Mini5 = inter(5.sp)
private val Mini8 = inter(8.sp)
private val RowTitle = inter(14.sp, FontWeight.SemiBold, 21.sp)
private val RowSubtitle = inter(12.sp, lineHeight = 18.sp)
private val PillLabel = inter(12.sp)

private val BarcodeWidths = listOf(3, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1)

@Composable
fun OnboardingScreen(
    onBack: () -> Unit,
    onHowItWorks: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SnapTaskColors.Background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        OnboardingAppBar(onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "THE IDEA · 1 OF 3",
                style = SectionLabel,
                color = SnapTaskColors.Burgundy,
            )
            Text(
                text = "A tidy home for every screenshot.",
                style = Heading,
                color = SnapTaskColors.TextPrimary,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = "Tickets, recipes, things you love. Find them without the endless scroll.",
                style = Supporting,
                color = SnapTaskColors.TextSecondary,
                modifier = Modifier.fillMaxWidth(),
            )

            ScreenshotCollection()
            PreferenceCard()
            StepPills()

            Spacer(Modifier.weight(1f))

            SnapTaskPrimaryButton(
                label = "How it works",
                onClick = onHowItWorks,
                leadingIcon = painterResource(R.drawable.ic_arrow_right),
            )
            Text(
                text = "Nothing is removed without your chosen rules.",
                style = SnapTaskType.Caption,
                color = SnapTaskColors.TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        MascotNook()
    }
}

@Composable
private fun OnboardingAppBar(onBack: () -> Unit) {
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
private fun ScreenshotCollection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
    ) {
        TicketPreview()
        PhotoPreview(
            background = OnboardingColors.LampCard,
            brand = "FORM & FIELD",
            photo = R.drawable.img_screenshot_lamp,
            title = "Arc table lamp",
            meta = "Brushed brass • £89.00",
        )
        PhotoPreview(
            background = OnboardingColors.RecipeCard,
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
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .size(width = 64.dp, height = 78.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(background)
            .padding(6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        content = content,
    )
}

@Composable
private fun TicketPreview() {
    PreviewCardShell(OnboardingColors.TicketBackground) {
        Text(
            text = "THE GARDEN • LIVE",
            style = Mini5,
            color = OnboardingColors.TicketMuted,
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
            color = OnboardingColors.TicketMuted,
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
                        .background(OnboardingColors.Barcode),
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
private fun PreferenceCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SnapTaskColors.Surface)
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        PreferenceRow(
            icon = R.drawable.ic_row_scan_line,
            title = "Bring your screenshots together",
            subtitle = "Choose what SnapTask can see.",
        )
        PreferenceRow(
            icon = R.drawable.ic_row_sparkles,
            title = "Let AI do the sorting",
            subtitle = "Review categories and detected dates.",
        )
        PreferenceRow(
            icon = R.drawable.ic_row_list_checks,
            title = "Take action, on your terms",
            subtitle = "Review cleanup or opt into automation.",
        )
    }
}

@Composable
private fun PreferenceRow(
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
                .background(OnboardingColors.IconTile),
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
private fun StepPills() {
    Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
        StepPill(label = "Overview", selected = true)
        StepPill(label = "Dated", selected = false)
        StepPill(label = "Non-dated", selected = false)
    }
}

@Composable
private fun StepPill(label: String, selected: Boolean) {
    val shape = RoundedCornerShape(100.dp)
    Box(
        modifier = Modifier
            .height(34.dp)
            .clip(shape)
            .background(if (selected) SnapTaskColors.Burgundy else SnapTaskColors.Surface)
            .border(
                width = 1.dp,
                color = if (selected) SnapTaskColors.Burgundy else OnboardingColors.PillBorder,
                shape = shape,
            )
            .padding(horizontal = 13.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = PillLabel,
            color = if (selected) Color.White else SnapTaskColors.TextSecondary,
            softWrap = false,
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
                .offset(x = 118.dp, y = 1.dp)
                .size(width = 18.dp, height = 22.5.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 892)
@Composable
private fun OnboardingScreenPreview() {
    OnboardingScreen(onBack = {}, onHowItWorks = {})
}
