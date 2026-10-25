package com.company.tripplanner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.company.tripplanner.ui.components.AppButton
import com.company.tripplanner.ui.components.SectionHeader
import com.company.tripplanner.ui.theme.MyApplicationTheme

@Composable
fun FilterSortScreen(onApply: () -> Unit) {
    var budgetRange by remember { mutableStateOf(0f..1000f) }
    var selectedRating by remember { mutableIntStateOf(4) }
    var sortBy by remember { mutableStateOf("Price") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(text = "Filter & Sort", style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(32.dp))

        SectionHeader("Budget Range")
        RangeSlider(
            value = budgetRange,
            onValueChange = { budgetRange = it },
            valueRange = 0f..5000f,
            modifier = Modifier.fillMaxWidth()
        )
        Text(text = "$${budgetRange.start.toInt()} - $${budgetRange.endInclusive.toInt()}", color = MaterialTheme.colorScheme.primary)

        Spacer(modifier = Modifier.height(24.dp))
        SectionHeader("Minimum Rating")
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(5) { i ->
                FilterChip(
                    selected = selectedRating == i + 1,
                    onClick = { selectedRating = i + 1 },
                    label = { Text("${i + 1} Stars") }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        SectionHeader("Sort By")
        Column {
            listOf("Price", "Rating", "Distance", "Popularity").forEach { option ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Text(text = option, style = MaterialTheme.typography.bodyLarge)
                    RadioButton(selected = sortBy == option, onClick = { sortBy = option })
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        AppButton(text = "Apply Filters", onClick = onApply)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FilterSortScreenPreview() {
    MyApplicationTheme {
        FilterSortScreen(onApply = {})
    }
}
