package com.example.snaptask.ui.auth

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snaptask.R
import com.example.snaptask.ui.components.SnapTaskPrimaryButton
import com.example.snaptask.ui.theme.InterFamily
import com.example.snaptask.ui.theme.SnapTaskColors
import com.example.snaptask.ui.theme.SnapTaskType

@Composable
fun LoginScreen(
    onBack: () -> Unit,
    onLoginSuccess: () -> Unit,
    onNavigateToCreateAccount: () -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onLogin: (email: String, password: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SnapTaskColors.Background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        // App bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_scan_line),
                contentDescription = null,
                modifier = Modifier.size(22.dp),
            )
            Text("SnapTask", style = SnapTaskType.AppBarTitle, color = SnapTaskColors.Burgundy)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(role = Role.Button, onClick = onBack)
                    .padding(4.dp),
            ) {
                Text(
                    "Back",
                    style = TextStyle(
                        fontFamily = InterFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                    ),
                    color = SnapTaskColors.TextSecondary,
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Spacer(Modifier.height(8.dp))

            // Eyebrow
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_sparkles_eyebrow),
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                )
                Text(
                    "WELCOME BACK",
                    style = SnapTaskType.Eyebrow,
                    color = SnapTaskColors.Burgundy,
                )
            }

            Text(
                text = "Sign in to\nSnapTask.",
                style = TextStyle(
                    fontFamily = InterFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 36.sp,
                    lineHeight = 44.sp,
                    letterSpacing = 0.sp,
                ),
                color = SnapTaskColors.TextPrimary,
            )

            Text(
                text = "Your screenshots and tasks are waiting for you.",
                style = SnapTaskType.Body,
                color = SnapTaskColors.TextSecondary,
            )

            Spacer(Modifier.height(8.dp))

            // Email field
            AuthTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email address",
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                onImeAction = { focusManager.moveFocus(FocusDirection.Down) },
            )

            // Password field
            AuthTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                keyboardType = KeyboardType.Password,
                isPassword = true,
                imeAction = ImeAction.Done,
                onImeAction = {
                    focusManager.clearFocus()
                    if (email.isNotBlank() && password.isNotBlank()) onLogin(email, password)
                },
            )

            // Error message
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    style = SnapTaskType.Caption,
                    color = Color(0xFFB00020),
                )
            }

            Spacer(Modifier.height(4.dp))

            if (isLoading) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = SnapTaskColors.Burgundy)
                }
            } else {
                SnapTaskPrimaryButton(
                    label = "Sign in",
                    onClick = {
                        focusManager.clearFocus()
                        onLogin(email, password)
                    },
                )
            }

            Spacer(Modifier.height(8.dp))

            // Link to create account
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Don't have an account? ",
                    style = SnapTaskType.Caption,
                    color = SnapTaskColors.TextSecondary,
                )
                Text(
                    "Create one",
                    style = TextStyle(
                        fontFamily = InterFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                    ),
                    color = SnapTaskColors.Burgundy,
                    modifier = Modifier.clickable(role = Role.Button, onClick = onNavigateToCreateAccount),
                )
            }
        }
    }
}

@Composable
internal fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    isPassword: Boolean = false,
    onImeAction: () -> Unit = {},
) {
    val shape = RoundedCornerShape(14.dp)
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                label,
                style = TextStyle(fontFamily = InterFamily, fontSize = 14.sp),
                color = SnapTaskColors.TextSecondary,
            )
        },
        singleLine = true,
        shape = shape,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions = KeyboardActions(
            onNext = { onImeAction() },
            onDone = { onImeAction() },
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = SnapTaskColors.Burgundy,
            unfocusedBorderColor = Color(0xFFE9DFDF),
            focusedLabelColor = SnapTaskColors.Burgundy,
            cursorColor = SnapTaskColors.Burgundy,
            focusedContainerColor = SnapTaskColors.Surface,
            unfocusedContainerColor = SnapTaskColors.Surface,
        ),
        textStyle = TextStyle(
            fontFamily = InterFamily,
            fontSize = 15.sp,
            color = SnapTaskColors.TextPrimary,
        ),
        modifier = modifier.fillMaxWidth(),
    )
}

@Preview(showBackground = true, widthDp = 412, heightDp = 892)
@Composable
private fun LoginScreenPreview() {
    LoginScreen(
        onBack = {},
        onLoginSuccess = {},
        onNavigateToCreateAccount = {},
        onLogin = { _, _ -> },
    )
}

