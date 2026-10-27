package com.example.snaptask.ui.settings

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.snaptask.ui.components.BottomTab
import com.example.snaptask.ui.components.DoodleBackground
import com.example.snaptask.ui.components.SnapTaskBottomNavigation
import com.example.snaptask.ui.components.SnapTaskPrimaryButton
import com.example.snaptask.ui.components.SnapTaskSwitch
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
private val CardTitle = inter(14.sp, FontWeight.SemiBold)
private val CardSubtitle = inter(12.sp, lineHeight = 18.sp)
private val RuleBody = inter(12.sp, lineHeight = 18.sp)
private val RuleExample = inter(11.sp)

@Composable
fun CleanupRulesScreen(
    onBack: () -> Unit,
    onDone: () -> Unit,
    onTabSelected: (BottomTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    var protectKeptChecked by remember { mutableStateOf(true) }

    DoodleBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            HeaderBar(title = "Cleanup rules", onBack = onBack)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Text(text = "Clear rules.\nNo surprises.", style = Heading, color = SnapTaskColors.TextPrimary)

                // Dated Rule Card
                RuleCard(
                    icon = R.drawable.ic_nav_home,
                    title = "Dated screenshots",
                    subtitle = "Tickets, events and reservations",
                    body = "Delete one day after the specified date. Notify on the specified date, one day before deletion.",
                    example = "Example: event 16 Oct → notify 16 Oct → delete 17 Oct",
                )

                // Non-dated Rule Card
                RuleCard(
                    icon = R.drawable.ic_nav_library,
                    title = "Non-dated screenshots",
                    subtitle = "Products, ideas, recipes and receipts",
                    body = "Delete seven days after being saved. Notify on day six, one day before deletion.",
                    example = "Example: saved 10 Oct → notify 16 Oct → delete 17 Oct",
                )

                // Protect Kept Card
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
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(text = "Protect kept screenshots", style = CardTitle, color = SnapTaskColors.TextPrimary)
                            Text(text = "Always exclude items marked Keep", style = CardSubtitle, color = SnapTaskColors.TextSecondary)
                        }
                        SnapTaskSwitch(checked = protectKeptChecked, onCheckedChange = { protectKeptChecked = it })
                    }

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
                            Image(painter = painterResource(R.drawable.ic_trash), contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "30-day recovery", style = CardTitle, color = SnapTaskColors.TextPrimary)
                            Text(text = "Restore items from Recently deleted", style = CardSubtitle, color = SnapTaskColors.TextSecondary)
                        }
                        Image(painter = painterResource(R.drawable.ic_chevron_right), contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }

                Text(
                    text = "Rules define eligibility. Automatic execution is a separate, explicit preference.",
                    style = SnapTaskType.Caption,
                    color = SnapTaskColors.TextSecondary,
                )

                Spacer(Modifier.weight(1f))

                SnapTaskPrimaryButton(
                    label = "Done",
                    onClick = onDone,
                )
            }

            SnapTaskBottomNavigation(
                selectedTab = BottomTab.Settings,
                onTabSelected = onTabSelected,
            )
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
private fun RuleCard(
    icon: Int,
    title: String,
    subtitle: String,
    body: String,
    example: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SnapTaskColors.Surface)
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
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
                Image(painter = painterResource(icon), contentDescription = null, modifier = Modifier.size(18.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = CardTitle, color = SnapTaskColors.TextPrimary)
                Text(text = subtitle, style = CardSubtitle, color = SnapTaskColors.TextSecondary)
            }
            Image(painter = painterResource(R.drawable.ic_chevron_right), contentDescription = null, modifier = Modifier.size(18.dp))
        }
        Text(text = body, style = RuleBody, color = SnapTaskColors.TextSecondary)
        Text(text = example, style = RuleExample, color = SnapTaskColors.TextSecondary)
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 892)
@Composable
private fun CleanupRulesScreenPreview() {
    CleanupRulesScreen(onBack = {}, onDone = {}, onTabSelected = {})
}
