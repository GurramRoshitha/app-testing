package com.company.tripplanner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.tripplanner.ui.components.AppButton
import com.company.tripplanner.ui.components.AppCard
import com.company.tripplanner.utils.AppTheme
import com.company.tripplanner.utils.ThemeManager
import kotlinx.coroutines.launch

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalContext
import com.company.tripplanner.ui.theme.MyApplicationTheme

@Composable
fun ThemePreviewScreen(themeManager: ThemeManager) {
    val themeState by themeManager.themeFlow.collectAsState(initial = AppTheme.SYSTEM)
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(text = "Theme Preview", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))

        AppCard {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Preview Card", fontWeight = FontWeight.Bold)
                Text(text = "This is how elements look in the current theme.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(16.dp))
                AppButton(text = "Sample Button", onClick = {})
            }
        }

        Spacer(modifier = Modifier.height(48.dp))
        
        ThemeSelectionRow("Light Mode", themeState == AppTheme.LIGHT) {
            scope.launch { themeManager.setTheme(AppTheme.LIGHT) }
        }
        ThemeSelectionRow("Dark Mode", themeState == AppTheme.DARK) {
            scope.launch { themeManager.setTheme(AppTheme.DARK) }
        }
        ThemeSelectionRow("System Default", themeState == AppTheme.SYSTEM) {
            scope.launch { themeManager.setTheme(AppTheme.SYSTEM) }
        }

        Spacer(modifier = Modifier.weight(1f))
        AppButton(text = "Save Preference", onClick = { })
    }
}

@Composable
fun ThemeSelectionRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 18.sp)
        RadioButton(selected = selected, onClick = onClick)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ThemePreviewScreenPreview() {
    MyApplicationTheme {
        ThemePreviewScreen(themeManager = ThemeManager(LocalContext.current))
    }
}
