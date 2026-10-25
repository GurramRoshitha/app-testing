package com.company.tripplanner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.company.tripplanner.ui.components.AppButton
import com.company.tripplanner.ui.components.AppInputField
import com.company.tripplanner.ui.theme.MyApplicationTheme

@Composable
fun ForgotPasswordScreen(onBackToLogin: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var isSuccess by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (!isSuccess) {
            Text(text = "Forgot Password", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Enter your email address and we'll send you a link to reset your password.",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(32.dp))
            AppInputField(value = email, onValueChange = { email = it }, label = "Email Address")
            Spacer(modifier = Modifier.height(24.dp))
            AppButton(text = "Send Reset Link", onClick = { isSuccess = true })
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = onBackToLogin) {
                Text(text = "Back to Login", fontWeight = FontWeight.SemiBold)
            }
        } else {
            Text(text = "Check Your Email", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "We've sent a password reset link to $email. Please check your inbox.",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(32.dp))
            AppButton(text = "Back to Login", onClick = onBackToLogin)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ForgotPasswordScreenPreview() {
    MyApplicationTheme {
        ForgotPasswordScreen(onBackToLogin = {})
    }
}
