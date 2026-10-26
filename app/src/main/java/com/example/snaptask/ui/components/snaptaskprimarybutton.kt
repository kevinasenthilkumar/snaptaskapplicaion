package com.example.snaptask.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.snaptask.ui.theme.SnapTaskColors
import com.example.snaptask.ui.theme.SnaptaskTheme
import com.example.snaptask.ui.theme.SnapTaskType

@Composable
fun SnapTaskPrimaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: Painter? = null,
) {
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(shape)
            .background(SnapTaskColors.Burgundy)
            .border(1.dp, SnapTaskColors.Burgundy, shape)
            .clickable(role = Role.Button, onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingIcon != null) {
            Image(
                painter = leadingIcon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
        }
        Text(text = label, style = SnapTaskType.ButtonLabel, color = Color.White)
    }
}

@Preview(showBackground = true)
@Composable
fun SnapTaskPrimaryButtonPreview() {
    SnaptaskTheme {
        SnapTaskPrimaryButton(
            label = "Continue",
            onClick = {}
        )
    }
}