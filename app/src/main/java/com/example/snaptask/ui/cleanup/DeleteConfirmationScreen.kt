package com.example.snaptask.ui.cleanup

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snaptask.R
import com.example.snaptask.ui.components.SnapTaskPrimaryButton
import com.example.snaptask.ui.components.SnapTaskSecondaryButton
import com.example.snaptask.ui.theme.InterFamily
import com.example.snaptask.ui.theme.SnapTaskColors

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

private val DialogQuestion = inter(21.sp, lineHeight = 27.3.sp)
private val DialogBody = inter(13.sp, lineHeight = 19.5.sp)

@Composable
fun DeleteConfirmationScreen(
    onConfirmDelete: () -> Unit,
    onCancel: () -> Unit,
    count: Int = 1,
    categoryName: String? = null,
    sizeFormatted: String = "",
    modifier: Modifier = Modifier,
) {
    val question = if (categoryName != null) {
        "Delete all $count screenshots in $categoryName?"
    } else {
        "Delete $count screenshot${if (count > 1) "s" else ""}?"
    }
    val buttonLabel = "Delete $count screenshot${if (count > 1) "s" else ""}"

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF26141B).copy(alpha = 0.46f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onCancel,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(Color(0xFFFFF8F6))
                .padding(26.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Image(
                painter = painterResource(R.drawable.ic_trash),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
            )
            Text(
                text = question,
                style = DialogQuestion,
                color = SnapTaskColors.TextPrimary,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "These screenshots will be removed from your phone gallery. Android will ask for your confirmation.",
                style = DialogBody,
                color = SnapTaskColors.TextSecondary,
                textAlign = TextAlign.Center,
            )

            if (sizeFormatted.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFF8EDF0))
                        .padding(12.dp),
                ) {
                    Text(
                        text = "$sizeFormatted selected · Trash with Android system confirmation",
                        style = inter(12.sp, lineHeight = 18.sp),
                        color = SnapTaskColors.Burgundy,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            SnapTaskPrimaryButton(
                label = buttonLabel,
                onClick = onConfirmDelete,
                leadingIcon = painterResource(R.drawable.ic_trash),
            )
            SnapTaskSecondaryButton(
                label = "Cancel",
                onClick = onCancel,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 892)
@Composable
private fun DeleteConfirmationScreenPreview() {
    DeleteConfirmationScreen(onConfirmDelete = {}, onCancel = {})
}
