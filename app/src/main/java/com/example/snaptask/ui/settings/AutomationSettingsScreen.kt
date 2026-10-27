package com.example.snaptask.ui.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.snaptask.ui.components.SnapTaskInfoNote
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

private val Heading = inter(32.sp, lineHeight = 37.76.sp)
private val Subtitle = inter(14.sp, lineHeight = 21.sp)
private val CardTitle = inter(14.sp, FontWeight.SemiBold)
private val CardBody = inter(12.sp, lineHeight = 18.sp)

@Composable
fun AutomationSettingsScreen(
    onBack: () -> Unit,
    onSavePreference: () -> Unit,
    onTabSelected: (BottomTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedOption by remember { mutableStateOf(1) } // 0 = Daily, 1 = Never

    DoodleBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            HeaderBar(title = "Automation", onBack = onBack)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Text(text = "A little help,\nonly if you want it.", style = Heading, color = SnapTaskColors.TextPrimary)
                Text(
                    text = "Choose one cleanup mode. Your retention rules stay the same in either mode.",
                    style = Subtitle,
                    color = SnapTaskColors.TextSecondary,
                )

                // Daily Option Card
                RadioCard(
                    title = "Daily automation",
                    body = "Run daily at 09:00 for eligible screenshots. Requires Android setup and your explicit consent.",
                    selected = selectedOption == 0,
                    onClick = { selectedOption = 0 },
                )

                // Never Option Card
                RadioCard(
                    title = "Never run automatically",
                    body = "Recommended to start. Nothing is deleted until you review and confirm with Take Action.",
                    selected = selectedOption == 1,
                    onClick = { selectedOption = 1 },
                )

                // Inactive Time Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(SnapTaskColors.Surface)
                        .padding(18.dp),
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
                        Image(painter = painterResource(R.drawable.ic_nav_home), contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Text(text = "Daily time · 09:00", style = CardTitle, color = SnapTaskColors.TextPrimary)
                        Text(text = "Inactive while manual mode is selected", style = CardBody, color = SnapTaskColors.TextSecondary)
                    }
                    Image(painter = painterResource(R.drawable.ic_lock), contentDescription = null, modifier = Modifier.size(18.dp))
                }

                SnapTaskInfoNote(text = "Automatic cleanup is OFF. Saving this preference does not schedule a background task in this design.")

                Spacer(Modifier.weight(1f))

                SnapTaskPrimaryButton(
                    label = "Save preference",
                    onClick = onSavePreference,
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
private fun RadioCard(
    title: String,
    body: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(20.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (selected) Color(0xFFF8EDF0) else SnapTaskColors.Surface)
            .border(1.dp, if (selected) SnapTaskColors.Burgundy else Color(0xFFE9DFDF), shape)
            .clickable(role = Role.RadioButton, onClick = onClick)
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .border(2.dp, SnapTaskColors.Burgundy, CircleShape)
                    .padding(4.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (selected) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(SnapTaskColors.Burgundy),
                    )
                }
            }
            Text(text = title, style = CardTitle, color = SnapTaskColors.TextPrimary)
        }
        Text(
            text = body,
            style = CardBody,
            color = SnapTaskColors.TextSecondary,
            modifier = Modifier.padding(start = 34.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 892)
@Composable
private fun AutomationSettingsScreenPreview() {
    AutomationSettingsScreen(onBack = {}, onSavePreference = {}, onTabSelected = {})
}
