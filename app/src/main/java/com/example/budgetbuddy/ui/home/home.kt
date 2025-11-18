package com.example.budgetbuddy.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions // <-- Correct import
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType // <-- Add for cleanliness
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// FIX: Add the 'weekly' and 'monthly' parameters that your navigation graph expects
fun HomeScreen(username: String, weekly: Boolean, monthly: Boolean) {

    // User inputs
    var weeklyInput by remember { mutableStateOf("") }
    var monthlyInput by remember { mutableStateOf("") }

    // Saved values
    var savedWeekly by remember { mutableStateOf<Int?>(null) }
    var savedMonthly by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        // Welcome Text
        Text(
            text = "Welcome $username to BudgetBuddy",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp
        )

        Spacer(modifier = Modifier.height(25.dp))

        // Weekly Input
        OutlinedTextField(
            value = weeklyInput,
            onValueChange = { weeklyInput = it },
            label = { Text("Enter Weekly Budget") },
            // FIX: Use the imported KeyboardOptions
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(15.dp))

        // Monthly Input
        OutlinedTextField(
            value = monthlyInput,
            onValueChange = { monthlyInput = it },
            label = { Text("Enter Monthly Budget") },
            // FIX: Use the imported KeyboardOptions
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Save Button
        Button(
            onClick = {
                savedWeekly = weeklyInput.toIntOrNull()
                savedMonthly = monthlyInput.toIntOrNull()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Budget")
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Output
        if (savedWeekly != null && savedMonthly != null) {
            Text(
                text = "Weekly Budget: ₹$savedWeekly",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 22.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Monthly Budget: ₹$savedMonthly",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 22.sp
            )
        }
    }
}
