package com.example.budgetbuddy.ui.analytics

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.coroutines.tasks.await
import me.bytebeats.views.charts.pie.PieChart
import me.bytebeats.views.charts.pie.PieChartData
import me.bytebeats.views.charts.pie.render.SimpleSliceDrawer

@Composable
fun AnalyticsScreen() {

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid ?: ""

    var expenseData by remember { mutableStateOf<Map<String, Float>>(emptyMap()) }

    // Load expense data
    LaunchedEffect(true) {
        val result = db.collection("expenses")
            .whereEqualTo("userId", userId)
            .get()
            .await()

        val map = mutableMapOf<String, Float>()

        result.documents.forEach { doc ->
            val purpose = doc.getString("purpose") ?: "Other"
            val amount = doc.getString("amount")?.toFloatOrNull() ?: 0f

            map[purpose] = (map[purpose] ?: 0f) + amount
        }

        expenseData = map
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            "Expense Analytics",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(20.dp))

        if (expenseData.isNotEmpty()) {

            PieChart(
                pieChartData = PieChartData(
                    slices = expenseData.map { (label, value) ->
                        PieChartData.Slice(
                            value = value,
                            color = randomColor()
                        )
                    }
                ),
                modifier = Modifier.size(270.dp),
                sliceDrawer = SimpleSliceDrawer()
            )

            Spacer(Modifier.height(20.dp))

            expenseData.forEach { (p, a) ->
                Text("$p: ₹$a", fontSize = 16.sp)
            }

        } else {
            Text("No expenses yet")
        }
    }
}

@Composable
fun randomColor(): Color {
    val colors = listOf(
        Color(0xFFE57373),
        Color(0xFF64B5F6),
        Color(0xFF81C784),
        Color(0xFFFFD54F),
        Color(0xFFBA68C8),
        Color(0xFFFF8A65)
    )
    return colors.random()
}
