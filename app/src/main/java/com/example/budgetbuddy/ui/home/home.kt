package com.example.budgetbuddy.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    username: String,
    monthly: Int,
    weekly: Int
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(
            text = "Welcome $username to BudgetBuddy",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp
        )

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Weekly Budget: ₹$weekly",
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 22.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Monthly Budget: ₹$monthly",
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 22.sp
        )
    }
}
