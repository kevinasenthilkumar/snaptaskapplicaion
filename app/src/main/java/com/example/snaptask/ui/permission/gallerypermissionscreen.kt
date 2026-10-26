package com.example.snaptask.ui.permission

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.example.snaptask.ui.components.SnapTaskPrimaryButton
import com.example.snaptask.ui.theme.InterFamily
import com.example.snaptask.ui.theme.SnapTaskColors
import com.example.snaptask.ui.theme.SnapTaskType

// Colors used only on this screen
private object PermissionColors {
    val Scrim = Color(0xFF26141B).copy(alpha = 0.46f)
    val DialogBackground = Color(0xFFFFF8F6)
    val EmblemDisc = Color(0xFFF3E1E5)
    val IconTile = Color(0xFFF8EDF0)
    val Border = Color(0xFFE9DFDF)
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

private val Heading = inter(28.sp, lineHeight = 33.04.sp)
private val Supporting14 = inter(14.sp, lineHeight = 21.sp)
private val RowTitle = inter(14.sp, FontWeight.SemiBold, 21.sp)
private val RowSubtitle = inter(12.sp, lineHeight = 18.sp)
private val DialogQuestion = inter(21.sp, lineHeight = 27.3.sp)
private val DialogBody = inter(13.sp, lineHeight = 19.5.sp)

@Composable
fun GalleryPermissionScreen(
    onBack: () -> Unit,
    onAllow: () -> Unit,
    onDontAllow: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SnapTaskColors.Background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        // Page behind the dialog
        Column(modifier = Modifier.fillMaxSize()) {
            PermissionAppBar(onBack)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                FeatureEmblem()
                Text(
                    text = "Your screenshots.\nYour permission.",
                    style = Heading,
                    color = SnapTaskColors.TextPrimary,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = "To sort screenshots, SnapTask needs access to the photos you choose.",
                    style = Supporting14,
                    color = SnapTaskColors.TextSecondary,
                    modifier = Modifier.fillMaxWidth(),
                )
                PermissionCard()

                Spacer(Modifier.weight(1f))

                Text(
                    text = "Permission request preview · no gallery accessed",
                    style = SnapTaskType.Caption,
                    color = SnapTaskColors.TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            MascotNook()
        }

        // Scrim (also blocks taps on the page behind)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PermissionColors.Scrim)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {},
                ),
        )

        // Dialog
        PermissionDialog(
            onAllow = onAllow,
            onDontAllow = onDontAllow,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp)
                .offset(y = 19.5.dp),
        )
    }
}

@Composable
private fun PermissionAppBar(onBack: () -> Unit) {
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
            text = "Add screenshots",
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
private fun FeatureEmblem() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(132.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(108.dp)
                .clip(RoundedCornerShape(38.dp))
                .background(PermissionColors.EmblemDisc),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_images_emblem),
                contentDescription = null,
                modifier = Modifier.size(44.dp),
            )
        }
    }
}

@Composable
private fun PermissionCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SnapTaskColors.Surface)
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        PreferenceRow(
            icon = R.drawable.ic_row_shield_check,
            title = "Only what you allow",
            subtitle = "Change access in Android Settings anytime.",
        )
        PreferenceRow(
            icon = R.drawable.ic_row_image,
            title = "Built for screenshots",
            subtitle = "Your other photos are not cleanup targets.",
        )
    }
}

@Composable
private fun PreferenceRow(
    icon: Int,
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
                .background(PermissionColors.IconTile),
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
private fun PermissionDialog(
    onAllow: () -> Unit,
    onDontAllow: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(PermissionColors.DialogBackground)
            .padding(26.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Image(
            painter = painterResource(R.drawable.ic_images_dialog),
            contentDescription = null,
            modifier = Modifier.size(32.dp),
        )
        Text(
            text = "Allow SnapTask to access photos and videos on this device?",
            style = DialogQuestion,
            color = SnapTaskColors.TextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = "Screenshot access helps organize your library.",
            style = DialogBody,
            color = SnapTaskColors.TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        SnapTaskPrimaryButton(label = "Allow", onClick = onAllow)
        SecondaryTextButton(label = "Don’t Allow", onClick = onDontAllow)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            Image(
                painter = painterResource(R.drawable.img_pocket_doll_mascot_small),
                contentDescription = null,
                modifier = Modifier.size(width = 16.dp, height = 20.dp),
            )
        }
    }
}

@Composable
private fun SecondaryTextButton(label: String, onClick: () -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(shape)
            .background(SnapTaskColors.Surface)
            .border(1.dp, PermissionColors.Border, shape)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = SnapTaskType.ButtonLabel,
            color = SnapTaskColors.Burgundy,
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
private fun GalleryPermissionScreenPreview() {
    GalleryPermissionScreen(onBack = {}, onAllow = {}, onDontAllow = {})
}