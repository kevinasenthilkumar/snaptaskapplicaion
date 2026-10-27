package com.example.snaptask.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snaptask.R
import com.example.snaptask.ui.theme.InterFamily
import com.example.snaptask.ui.theme.SnapTaskColors
import com.example.snaptask.ui.theme.SnaptaskTheme

enum class BottomTab {
    Home, Library, Reminders, Settings
}

private val TabLabelStyle = TextStyle(
    fontFamily = InterFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 11.sp,
)

@Composable
fun SnapTaskBottomNavigation(
    selectedTab: BottomTab,
    onTabSelected: (BottomTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(SnapTaskColors.Surface)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BottomTabItem(
            label = "Home",
            iconRes = R.drawable.ic_nav_home,
            isSelected = selectedTab == BottomTab.Home,
            onClick = { onTabSelected(BottomTab.Home) },
        )
        BottomTabItem(
            label = "Library",
            iconRes = R.drawable.ic_nav_library,
            isSelected = selectedTab == BottomTab.Library,
            onClick = { onTabSelected(BottomTab.Library) },
        )
        BottomTabItem(
            label = "Reminders",
            iconRes = R.drawable.ic_nav_reminders,
            isSelected = selectedTab == BottomTab.Reminders,
            onClick = { onTabSelected(BottomTab.Reminders) },
        )
        BottomTabItem(
            label = "Settings",
            iconRes = R.drawable.ic_nav_settings,
            isSelected = selectedTab == BottomTab.Settings,
            onClick = { onTabSelected(BottomTab.Settings) },
        )
    }
}

@Composable
private fun BottomTabItem(
    label: String,
    iconRes: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(role = Role.Tab, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Box(
            modifier = Modifier
                .size(width = 44.dp, height = 26.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(if (isSelected) Color(0xFFF8EDF0) else Color.Transparent),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = label,
                modifier = Modifier.size(20.dp),
            )
        }
        Text(
            text = label,
            style = TabLabelStyle,
            color = if (isSelected) SnapTaskColors.Burgundy else SnapTaskColors.TextSecondary,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SnapTaskBottomNavigationPreview() {
    SnaptaskTheme {
        SnapTaskBottomNavigation(
            selectedTab = BottomTab.Home,
            onTabSelected = {},
        )
    }
}
