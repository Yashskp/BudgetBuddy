package com.example.budgetbuddy.ui.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(username: String, navController: NavController) {

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    // NEW WEEK SELECTOR STATE
    var selectedWeek by rememberSaveable { mutableStateOf("Select Week") }

    var selectedMonth by rememberSaveable { mutableStateOf("Select Month") }
    var weeklyAmount by rememberSaveable { mutableStateOf("") }
    var monthlyAmount by rememberSaveable { mutableStateOf("") }

    var savedDate by rememberSaveable { mutableStateOf("") }
    var savedTime by rememberSaveable { mutableStateOf("") }
    var showSaved by rememberSaveable { mutableStateOf(false) }

    val userId = auth.currentUser?.uid ?: ""

    // Load saved budget
    LaunchedEffect(true) {
        try {
            val doc = db.collection("budgets").document(userId).get().await()
            if (doc.exists()) {
                selectedWeek = doc.getString("week") ?: "Select Week"
                selectedMonth = doc.getString("month") ?: "Select Month"
                weeklyAmount = doc.getString("weeklyAmount") ?: ""
                monthlyAmount = doc.getString("monthlyAmount") ?: ""
                savedDate = doc.getString("savedDate") ?: ""
                savedTime = doc.getString("savedTime") ?: ""
                showSaved = true
            }
        } catch (_: Exception) {}
    }

    val today = SimpleDateFormat("EEEE", Locale.getDefault()).format(Date())

    Column(modifier = Modifier.fillMaxSize()) {

        // HEADER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF0066FF), Color(0xFF00AAFF))
                    )
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            Column(modifier = Modifier.padding(start = 20.dp)) {
                Text("Hello, $username", fontSize = 27.sp, color = Color.White, fontWeight = FontWeight.Bold)
                Text("Welcome to BudgetBuddy", fontSize = 16.sp, color = Color.White.copy(alpha = 0.8f))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ------------------------ BUDGET CARD ------------------------
        Card(
            modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(10.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                Text("Set Your Budget", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF003366))
                Spacer(modifier = Modifier.height(20.dp))

                // ⭐ WEEK SELECTOR ADDED HERE ⭐
                DropdownField(
                    value = selectedWeek,
                    label = "Select Week",
                    options = listOf("Week 1", "Week 2", "Week 3", "Week 4"),
                    onSelect = { selectedWeek = it }
                )

                Spacer(modifier = Modifier.height(15.dp))

                // MONTH SELECTOR
                DropdownField(
                    value = selectedMonth,
                    label = "Select Month",
                    options = listOf(
                        "January", "February", "March", "April", "May", "June",
                        "July", "August", "September", "October", "November", "December"
                    ),
                    onSelect = { selectedMonth = it }
                )

                Spacer(modifier = Modifier.height(15.dp))

                OutlinedTextField(
                    value = weeklyAmount,
                    onValueChange = { weeklyAmount = it },
                    label = { Text("Weekly Budget (₹)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(15.dp))

                OutlinedTextField(
                    value = monthlyAmount,
                    onValueChange = { monthlyAmount = it },
                    label = { Text("Monthly Budget (₹)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {

                        val sdfDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                        val sdfTime = SimpleDateFormat("hh:mm a", Locale.getDefault())
                        val now = Date()
                        savedDate = sdfDate.format(now)
                        savedTime = sdfTime.format(now)

                        val data = hashMapOf(
                            "week" to selectedWeek,
                            "month" to selectedMonth,
                            "weeklyAmount" to weeklyAmount,
                            "monthlyAmount" to monthlyAmount,
                            "savedDate" to savedDate,
                            "savedTime" to savedTime
                        )

                        db.collection("budgets").document(userId)
                            .set(data)
                            .addOnSuccessListener {
                                showSaved = true
                                Toast.makeText(context, "Budget Saved", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Failed to save", Toast.LENGTH_SHORT).show()
                            }

                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
                ) {
                    Text("Save Budget", fontSize = 18.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SAVED BUDGET CARD
        if (showSaved) {
            Card(
                modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    Text("Your Saved Budget", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("📅 Today: $today", fontSize = 16.sp)
                    Text("📆 Week: $selectedWeek", fontSize = 16.sp)
                    Text("🗓 Month: $selectedMonth", fontSize = 16.sp)
                    Text("💰 Weekly: ₹$weeklyAmount", fontSize = 16.sp)
                    Text("💸 Monthly: ₹$monthlyAmount", fontSize = 16.sp)

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("📆 Saved On: $savedDate", fontSize = 16.sp)
                    Text("⏰ Saved At: $savedTime", fontSize = 16.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(25.dp))

        Button(
            onClick = { navController.navigate("expense") },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 20.dp).height(50.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0077FF))
        ) {
            Text("Add Expense", fontSize = 18.sp, color = Color.White)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    value: String,
    label: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = value,
            readOnly = true,
            onValueChange = {},
            label = { Text(label) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
