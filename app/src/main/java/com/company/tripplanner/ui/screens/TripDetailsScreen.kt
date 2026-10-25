package com.company.tripplanner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.company.tripplanner.ui.components.AppButton
import com.company.tripplanner.ui.components.AppCard
import com.company.tripplanner.ui.components.SectionHeader
import com.company.tripplanner.ui.components.StatusBadge
import com.company.tripplanner.ui.theme.MyApplicationTheme
import com.company.tripplanner.utils.PdfExporter

import com.company.tripplanner.data.models.Trip
import com.company.tripplanner.data.models.getEffectiveStatus
import com.company.tripplanner.utils.Helpers

@Composable
fun TripDetailsScreen(
    trip: Trip, 
    onDelete: () -> Unit, 
    onViewWeather: (String) -> Unit,
    onUpdateStatus: (String) -> Unit = {},
    onOpenChecklist: () -> Unit = {}
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showCompleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val effectiveStatus = trip.getEffectiveStatus()

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Trip") },
            text = { Text("Are you sure you want to delete '${trip.name}'? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showCompleteDialog) {
        AlertDialog(
            onDismissRequest = { showCompleteDialog = false },
            title = { Text("Complete This Trip?") },
            text = { Text("Are you sure you want to move this trip to Past Trips?") },
            confirmButton = {
                Button(
                    onClick = {
                        showCompleteDialog = false
                        onUpdateStatus("Completed")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Mark Completed", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCompleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = trip.name, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                StatusBadge(
                    text = effectiveStatus,
                    color = when (effectiveStatus) {
                        "Completed" -> MaterialTheme.colorScheme.secondary
                        "Active" -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.primary
                    }
                )
            }
            Row {
                IconButton(onClick = { Helpers.openDestinationInGoogleMaps(context, trip.name) }) { 
                    Icon(Icons.Default.Place, contentDescription = "Open in Google Maps", tint = MaterialTheme.colorScheme.primary) 
                }
                IconButton(onClick = { PdfExporter.exportTripToPdf(context, trip) }) { 
                    Icon(Icons.Default.PictureAsPdf, contentDescription = "Export PDF", tint = MaterialTheme.colorScheme.primary) 
                }
                IconButton(onClick = { showDeleteDialog = true }) { 
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error) 
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = trip.dates, color = MaterialTheme.colorScheme.onSurfaceVariant)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                AppButton(
                    text = "Weather", 
                    onClick = { onViewWeather(trip.name) },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                AppButton(
                    text = "Maps", 
                    onClick = { Helpers.openDestinationInGoogleMaps(context, trip.name) },
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                AppButton(
                    text = "Export PDF", 
                    onClick = { PdfExporter.exportTripToPdf(context, trip) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = androidx.compose.ui.graphics.Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        

        
        SectionHeader(title = "Trip Options")
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ActionButtonRow("Packing Checklist", Icons.Default.Checklist) { 
                onOpenChecklist()
            }
            if (effectiveStatus != "Completed") {
                ActionButtonRow("Mark as Completed", Icons.Default.CheckCircle) { 
                    showCompleteDialog = true
                }
            } else {
                ActionButtonRow("Move Back to Upcoming", Icons.Default.Refresh) {
                    onUpdateStatus("Upcoming")
                }
            }
            ActionButtonRow("Open in Google Maps", Icons.Default.Place) { 
                Helpers.openDestinationInGoogleMaps(context, trip.name) 
            }
            ActionButtonRow("Export Itinerary as PDF", Icons.Default.PictureAsPdf) { 
                PdfExporter.exportTripToPdf(context, trip) 
            }
            ActionButtonRow("Expenses", Icons.Default.Receipt) { }
        }
    }
}

@Composable
fun ActionButtonRow(label: String, icon: ImageVector, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.Start) {
            Icon(icon, contentDescription = null)
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = label, fontSize = 16.sp)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TripDetailsScreenPreview() {
    MyApplicationTheme {
        TripDetailsScreen(
            trip = Trip(name = "Paris Trip", dates = "Jul 15, 2026", total_budget = 4000.0), 
            onDelete = {},
            onViewWeather = {}
        )
    }
}
