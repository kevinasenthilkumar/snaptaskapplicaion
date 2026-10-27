package com.example.snaptask.ui.cleanup

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

private val Heading = inter(32.sp, lineHeight = 37.76.sp)
private val Body = inter(14.sp, lineHeight = 21.sp)

@Composable
fun CleanupInProgressScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DoodleBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            HeaderBar(title = "Cleaning up", onBack = onBack)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
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
                            .background(Color(0xFFF3E1E5)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_sparkles_accent),
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                        )
                    }
                }

                Text(
                    text = "Making a little\nroom for what’s next.",
                    style = Heading,
                    color = SnapTaskColors.TextPrimary,
                )
                Text(
                    text = "Moving the selected screenshots to Recently deleted. Your kept items stay safe.",
                    style = Body,
                    color = SnapTaskColors.TextSecondary,
                )

                ProgressCard()
                SnapTaskInfoNote(text = "Cleanup progress preview only. No photos have actually been moved or deleted.")

                Spacer(Modifier.weight(1f))
            }
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
private fun ProgressCard() {
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
            Text(text = "4 of 6", style = inter(32.sp), color = SnapTaskColors.Burgundy)
            Text(text = "67%", style = inter(14.sp, FontWeight.SemiBold), color = SnapTaskColors.Burgundy)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFF3E1E5)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.67f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(SnapTaskColors.Burgundy),
            )
        }
        Text(text = "Moving Canvas trainers...", style = inter(12.sp), color = SnapTaskColors.TextSecondary)
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 892)
@Composable
private fun CleanupInProgressScreenPreview() {
    CleanupInProgressScreen(onBack = {})
}
