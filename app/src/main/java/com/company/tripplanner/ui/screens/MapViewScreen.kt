package com.company.tripplanner.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.company.tripplanner.ui.theme.MyApplicationTheme

@Composable
fun MapViewScreen() {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale *= zoomChange
        offset += offsetChange
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA)) // Light map background
            .transformable(state = state)
    ) {
        // Simulated Map Background with Grid
        Canvas(modifier = Modifier.fillMaxSize().graphicsLayer(
            scaleX = scale,
            scaleY = scale,
            translationX = offset.x,
            translationY = offset.y
        )) {
            val gridSize = 100.dp.toPx()
            for (x in 0..(size.width / gridSize).toInt() + 10) {
                drawLine(
                    color = Color.LightGray.copy(alpha = 0.3f),
                    start = Offset(x * gridSize - 500, -500f),
                    end = Offset(x * gridSize - 500, size.height + 500f),
                    strokeWidth = 1.dp.toPx()
                )
            }
            for (y in 0..(size.height / gridSize).toInt() + 10) {
                drawLine(
                    color = Color.LightGray.copy(alpha = 0.3f),
                    start = Offset(-500f, y * gridSize - 500),
                    end = Offset(size.width + 500f, y * gridSize - 500),
                    strokeWidth = 1.dp.toPx()
                )
            }
            
            // Draw some "Parks" or "Water" areas
            drawCircle(Color(0xFFE3F2FD), radius = 300f, center = Offset(200f, 400f))
            drawCircle(Color(0xFFE8F5E9), radius = 250f, center = Offset(800f, 1000f))
        }

        // Animated Markers
        Box(modifier = Modifier.fillMaxSize().graphicsLayer(
            scaleX = scale,
            scaleY = scale,
            translationX = offset.x,
            translationY = offset.y
        )) {
            MapMarker(Offset(300f, 500f), "Eiffel Tower", Icons.Default.LocationOn, MaterialTheme.colorScheme.primary)
            MapMarker(Offset(700f, 800f), "Louvre", Icons.Default.Museum, MaterialTheme.colorScheme.secondary)
            MapMarker(Offset(500f, 1200f), "My Hotel", Icons.Default.Hotel, Color(0xFF4CAF50))
        }

        // Top Search Bar UI
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Search destinations...", color = Color.Gray, modifier = Modifier.weight(1f))
                Icon(Icons.Default.Mic, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
        }

        // UI Overlay Buttons
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FloatingActionButton(
                onClick = { 
                    scale = 1f
                    offset = Offset.Zero
                },
                containerColor = Color.White,
                contentColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            ) { Icon(Icons.Default.MyLocation, contentDescription = "My Location") }
            
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
            ) {
                IconButton(onClick = { scale *= 1.2f }) { Icon(Icons.Default.Add, contentDescription = "Zoom In") }
                Divider(modifier = Modifier.width(40.dp), thickness = 0.5.dp, color = Color.LightGray)
                IconButton(onClick = { scale /= 1.2f }) { Icon(Icons.Default.Remove, contentDescription = "Zoom Out") }
            }
        }
        
        // Simulation Notice (Clean and subtle)
        Surface(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 100.dp),
            color = Color.Black.copy(alpha = 0.7f),
            shape = RoundedCornerShape(100.dp)
        ) {
            Text(
                "Offline Map Simulation", 
                color = Color.White, 
                fontSize = 12.sp, 
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
fun MapMarker(position: Offset, title: String, icon: ImageVector, color: Color) {
    val infiniteTransition = rememberInfiniteTransition()
    val bounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.offset(x = position.x.dp / 3, y = (position.y.dp / 3) + bounce.dp)
    ) {
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(8.dp),
            shadowElevation = 4.dp
        ) {
            Text(
                text = title,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MapViewScreenPreview() {
    MyApplicationTheme {
        MapViewScreen()
    }
}
