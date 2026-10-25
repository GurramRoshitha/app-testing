package com.company.tripplanner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.company.tripplanner.ui.components.AppButton
import com.company.tripplanner.ui.components.AppInputField
import com.company.tripplanner.ui.theme.MyApplicationTheme

@Composable
fun ChangePasswordScreen(onUpdate: () -> Unit) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    var currentVisible by remember { mutableStateOf(false) }
    var newVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(text = "Change Password", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))
        
        AppInputField(
            value = currentPassword,
            onValueChange = { currentPassword = it },
            label = "Current Password",
            visualTransformation = if (currentVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { currentVisible = !currentVisible }) {
                    Icon(if (currentVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                }
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        AppInputField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = "New Password",
            visualTransformation = if (newVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { newVisible = !newVisible }) {
                    Icon(if (newVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                }
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        AppInputField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Confirm New Password",
            visualTransformation = PasswordVisualTransformation()
        )
        
        Spacer(modifier = Modifier.weight(1f))
        AppButton(text = "Update Password", onClick = onUpdate)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ChangePasswordScreenPreview() {
    MyApplicationTheme {
        ChangePasswordScreen(onUpdate = {})
    }
}
