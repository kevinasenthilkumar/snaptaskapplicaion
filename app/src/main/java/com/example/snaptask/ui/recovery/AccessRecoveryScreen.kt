package com.example.snaptask.ui.recovery

import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snaptask.R
import com.example.snaptask.ui.components.DoodleBackground
import com.example.snaptask.ui.components.SnapTaskInfoNote
import com.example.snaptask.ui.components.SnapTaskPrimaryButton
import com.example.snaptask.ui.components.SnapTaskSecondaryButton
import com.example.snaptask.ui.theme.InterFamily
import com.example.snaptask.ui.theme.SnapTaskColors
import com.example.snaptask.ui.theme.SnapTaskType

private object RecoveryColors {
    val BadgeBackground = Color(0xFFF8EDF0)
    val EmblemDisc = Color(0xFFF3E1E5)
    val IconTile = Color(0xFFF8EDF0)
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

private val BadgeLabel = inter(12.sp)
private val Heading = inter(34.sp, lineHeight = 40.12.sp)
private val Supporting = inter(14.sp, lineHeight = 21.sp)
private val RowTitle = inter(14.sp, FontWeight.SemiBold, 21.sp)
private val RowSubtitle = inter(12.sp, lineHeight = 18.sp)

@Composable
fun AccessRecoveryScreen(
    onBack: () -> Unit,
    onChooseScreenshots: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DoodleBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            RecoveryAppBar(onBack)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                StatusBadge(label = "Access not granted")

                Text(
                    text = "Let’s let your\nscreenshots in.",
                    style = Heading,
                    color = SnapTaskColors.TextPrimary,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = "No access? No problem. Choose a few screenshots or update permissions when you’re ready.",
                    style = Supporting,
                    color = SnapTaskColors.TextSecondary,
                    modifier = Modifier.fillMaxWidth(),
                )

                Emblem()
                PreferenceCard()
                SnapTaskInfoNote(text = "With limited access, only selected screenshots appear. You can add more later.")

                Spacer(Modifier.weight(1f))

                SnapTaskPrimaryButton(
                    label = "Choose screenshots",
                    onClick = onChooseScreenshots,
                )
                SnapTaskSecondaryButton(
                    label = "Open Android Settings",
                    onClick = onOpenSettings,
                )
            }

            MascotNook()
        }
    }
}

@Composable
private fun RecoveryAppBar(onBack: () -> Unit) {
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
            text = "Screenshot access",
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
private fun StatusBadge(label: String) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(RecoveryColors.BadgeBackground)
            .padding(horizontal = 14.dp, vertical = 6.dp),
    ) {
        Text(
            text = label,
            style = BadgeLabel,
            color = SnapTaskColors.TextSecondary,
        )
    }
}

@Composable
private fun Emblem() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(RecoveryColors.EmblemDisc),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_access_recovery),
                contentDescription = null,
                modifier = Modifier.size(40.dp),
            )
        }
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
            icon = R.drawable.ic_choose_screenshots,
            title = "Choose selected screenshots",
            subtitle = "Use limited access instead of your full library.",
        )
        PreferenceRow(
            icon = R.drawable.ic_update_permissions,
            title = "Update Android permissions",
            subtitle = "Settings → Apps → SnapTask → Photos and videos",
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
                .background(RecoveryColors.IconTile),
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
private fun AccessRecoveryScreenPreview() {
    AccessRecoveryScreen(
        onBack = {},
        onChooseScreenshots = {},
        onOpenSettings = {},
    )
}
