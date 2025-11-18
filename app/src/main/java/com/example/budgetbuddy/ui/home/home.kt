package com.example.budgetbuddy.ui.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
    val scrollState = rememberScrollState()   // ⭐ SCROLL ENABLED

    var selectedWeek by rememberSaveable { mutableStateOf("Select Week") }
    var selectedMonth by rememberSaveable { mutableStateOf("Select Month") }
    var weeklyAmount by rememberSaveable { mutableStateOf("") }
    var monthlyAmount by rememberSaveable { mutableStateOf("") }

    var savedDate by rememberSaveable { mutableStateOf("") }
    var savedTime by rememberSaveable { mutableStateOf("") }
    var showSaved by rememberSaveable { mutableStateOf(false) }

    val userId = auth.currentUser?.uid ?: ""

    // Load Firebase data
    LaunchedEffect(true) {
        try {
            val doc = db.collection("budgets").document(userId).get().await()
            if (doc.exists()) {
                selectedWeek = doc.getString("weekType") ?: "Select Week"
                selectedMonth = doc.getString("month") ?: "Select Month"
                weeklyAmount = doc.getString("weeklyAmount") ?: ""
                monthlyAmount = doc.getString("monthlyAmount") ?: ""
                savedDate = doc.getString("savedDate") ?: ""
                savedTime = doc.getString("savedTime") ?: ""
                showSaved = true
            }
        } catch (_: Exception) {}
    }

    val today = SimpleDateFormat("EEEE", Locale.getDefault()).format(Date()) // Day name

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)   // ⭐ SCROLL HERE
    ) {

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
                Text(
                    text = "Hello, $username 👋",
                    fontSize = 27.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Welcome to BudgetBuddy",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // BUDGET CARD
        Card(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(10.dp)
        ) {

            Column(modifier = Modifier.padding(20.dp)) {

                Text(
                    "Set Your Budget",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF003366)
                )

                Spacer(Modifier.height(20.dp))

                // ⭐ SELECT WEEK DROPDOWN
                DropdownField(
                    label = "Select Week",
                    value = selectedWeek,
                    options = listOf("Week 1", "Week 2", "Week 3", "Week 4"),
                    onSelect = { selectedWeek = it }
                )

                Spacer(Modifier.height(15.dp))

                // SELECT MONTH DROPDOWN
                DropdownField(
                    label = "Select Month",
                    value = selectedMonth,
                    options = listOf(
                        "January", "February", "March", "April", "May", "June",
                        "July", "August", "September", "October", "November", "December"
                    ),
                    onSelect = { selectedMonth = it }
                )

                Spacer(Modifier.height(15.dp))

                OutlinedTextField(
                    value = weeklyAmount,
                    onValueChange = { weeklyAmount = it },
                    label = { Text("Weekly Budget (₹)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(Modifier.height(15.dp))

                OutlinedTextField(
                    value = monthlyAmount,
                    onValueChange = { monthlyAmount = it },
                    label = { Text("Monthly Budget (₹)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(Modifier.height(20.dp))

                // SAVE BUDGET BUTTON
                Button(
                    onClick = {

                        val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
                        val time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())

                        savedDate = date
                        savedTime = time

                        val data = hashMapOf(
                            "weekType" to selectedWeek,
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
                ) {
                    Text("Save Budget", fontSize = 18.sp)
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // SAVED DATA CARD
        if (showSaved) {
            Card(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    Text("Your Saved Budget", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))

                    Text("📅 Today: $today", fontSize = 16.sp)
                    Text("🧾 Week: $selectedWeek", fontSize = 16.sp)
                    Text("🗓 Month: $selectedMonth", fontSize = 16.sp)
                    Text("💰 Weekly Budget: ₹$weeklyAmount", fontSize = 16.sp)
                    Text("💸 Monthly Budget: ₹$monthlyAmount", fontSize = 16.sp)

                    Spacer(Modifier.height(10.dp))

                    Text("📆 Saved Date: $savedDate", fontSize = 16.sp)
                    Text("⏰ Saved Time: $savedTime", fontSize = 16.sp)
                }
            }
        }

        Spacer(Modifier.height(25.dp))

        // ADD EXPENSE BUTTON
        Button(
            onClick = { navController.navigate("expense") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 20.dp)
                .height(50.dp),
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
    label: String,
    value: String,
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
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
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
