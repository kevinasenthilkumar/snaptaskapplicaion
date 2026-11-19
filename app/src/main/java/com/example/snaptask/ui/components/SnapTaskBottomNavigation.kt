package com.example.snaptask.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snaptask.R
import com.example.snaptask.ui.theme.AppFontFamily
import com.example.snaptask.ui.theme.SnapTaskColors

enum class BottomTab {
    Home, Library, Settings
}

private val TabLabelStyle = TextStyle(
    fontFamily = AppFontFamily,
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
            .padding(horizontal = 24.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = label,
            tint = if (isSelected) SnapTaskColors.Burgundy else SnapTaskColors.TextSecondary,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = label,
            style = TabLabelStyle,
            color = if (isSelected) SnapTaskColors.Burgundy else SnapTaskColors.TextSecondary,
        )
    }
}