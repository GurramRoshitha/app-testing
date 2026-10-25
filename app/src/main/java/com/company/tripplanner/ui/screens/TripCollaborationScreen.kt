package com.company.tripplanner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.company.tripplanner.ui.components.AppCard
import com.company.tripplanner.ui.theme.MyApplicationTheme

data class Collaborator(val name: String, val role: String)

@Composable
fun TripCollaborationScreen() {
    val collaborators = remember { mutableStateListOf(
        Collaborator("John Doe", "Owner"),
        Collaborator("Jane Smith", "Editor"),
        Collaborator("Mike Ross", "Viewer")
    )}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Collaboration", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = { }) { Icon(Icons.Default.Add, contentDescription = "Add Member") }
        }
        
        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(collaborators) { member ->
                AppCard {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = member.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(text = member.role, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                        }
                        if (member.role != "Owner") {
                            IconButton(onClick = { }) { Icon(Icons.Default.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error) }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TripCollaborationScreenPreview() {
    MyApplicationTheme {
        TripCollaborationScreen()
    }
}
