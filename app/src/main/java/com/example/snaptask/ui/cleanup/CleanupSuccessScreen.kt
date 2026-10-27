package com.example.snaptask.ui.cleanup

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snaptask.R
import com.example.snaptask.ui.components.DoodleBackground
import com.example.snaptask.ui.components.SnapTaskPrimaryButton
import com.example.snaptask.ui.components.SnapTaskSecondaryButton
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
fun CleanupSuccessScreen(
    onBackToHome: () -> Unit,
    onViewRecentlyDeleted: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DoodleBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            HeaderBar(title = "All tidy", onBack = onBackToHome)

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
                            painter = painterResource(R.drawable.ic_check),
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                        )
                    }
                }

                Text(
                    text = "Less clutter.\nLovely.",
                    style = Heading,
                    color = SnapTaskColors.TextPrimary,
                )
                Text(
                    text = "6 screenshots moved to Recently deleted. The useful things are still right here.",
                    style = Body,
                    color = SnapTaskColors.TextSecondary,
                )

                // Stats Summary Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(SnapTaskColors.Surface)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column {
                            Text(text = "6", style = inter(32.sp), color = SnapTaskColors.Burgundy)
                            Text(text = "Screenshots cleared", style = inter(12.sp), color = SnapTaskColors.TextSecondary)
                        }
                        Column {
                            Text(text = "122", style = inter(32.sp), color = SnapTaskColors.Burgundy)
                            Text(text = "In your active library", style = inter(12.sp), color = SnapTaskColors.TextSecondary)
                        }
                    }
                    Text(
                        text = "18.4 MB in recovery · storage is released after permanent deletion.",
                        style = inter(12.sp, lineHeight = 18.sp),
                        color = SnapTaskColors.TextSecondary,
                    )
                }

                Text(
                    text = "Changed your mind? Restore these screenshots until 15 Nov 2026.",
                    style = Body,
                    color = SnapTaskColors.TextSecondary,
                )

                Spacer(Modifier.weight(1f))

                SnapTaskPrimaryButton(
                    label = "Back to Home",
                    onClick = onBackToHome,
                    leadingIcon = painterResource(R.drawable.ic_nav_home),
                )
                SnapTaskSecondaryButton(
                    label = "View Recently deleted",
                    onClick = onViewRecentlyDeleted,
                )

                Text(
                    text = "Success state preview · no device photos deleted",
                    style = SnapTaskType.Caption,
                    color = SnapTaskColors.TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
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

@Preview(showBackground = true, widthDp = 412, heightDp = 892)
@Composable
private fun CleanupSuccessScreenPreview() {
    CleanupSuccessScreen(onBackToHome = {}, onViewRecentlyDeleted = {})
}
