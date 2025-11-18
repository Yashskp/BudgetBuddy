package com.example.budgetbuddy.ui.expense

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseScreen() {

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid ?: ""

    var amount by remember { mutableStateOf("") }
    var purpose by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }

    val expenseList = remember { mutableStateListOf<Map<String, Any>>() }

    // ⭐ SCROLL STATE ADDED
    val scrollState = rememberScrollState()

    // LOAD EXPENSES
    LaunchedEffect(true) {
        try {
            val snapshot = db.collection("expenses")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            expenseList.clear()
            snapshot.documents.forEach { doc ->
                doc.data?.let { expenseList.add(it) }
            }

        } catch (e: Exception) {
            Toast.makeText(context, "Load error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)   // ⭐ SCROLL ADDED HERE
    ) {

        // HEADER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF9900FF), Color(0xFFCC66FF))
                    )
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            Column(modifier = Modifier.padding(start = 20.dp)) {
                Text(
                    "Expense Tracker",
                    fontSize = 26.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Track your daily spending",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        // SEARCH BAR
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            placeholder = { Text("Search expenses...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(15.dp))

        // INPUT CARD
        Card(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(10.dp)
        ) {

            Column(modifier = Modifier.padding(20.dp)) {

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Expense Amount (₹)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(Modifier.height(15.dp))

                OutlinedTextField(
                    value = purpose,
                    onValueChange = { purpose = it },
                    label = { Text("Expense Purpose") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (amount.isNotEmpty() && purpose.isNotEmpty()) {

                            val cal = Calendar.getInstance()

                            val data = mapOf<String, Any>(
                                "amount" to amount,
                                "purpose" to purpose,
                                "date" to SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(cal.time),
                                "time" to SimpleDateFormat("hh:mm a", Locale.getDefault()).format(cal.time),
                                "month" to SimpleDateFormat("MMMM", Locale.getDefault()).format(cal.time),
                                "week" to cal.get(Calendar.WEEK_OF_MONTH).toString(),
                                "userId" to userId,
                                "timestamp" to Timestamp.now()
                            )

                            db.collection("expenses").add(data)
                                .addOnSuccessListener {
                                    expenseList.add(data)
                                    amount = ""
                                    purpose = ""
                                    Toast.makeText(context, "Expense added", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                                }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFFAA55FF))
                ) {
                    Text("Add Expense", color = Color.White, fontSize = 18.sp)
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(
            "Your Expenses",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 20.dp)
        )

        Spacer(Modifier.height(10.dp))

        val filteredList = expenseList.filter {
            it["amount"].toString().contains(searchQuery, true) ||
                    it["purpose"].toString().contains(searchQuery, true)
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            filteredList.forEach { item ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {

                    Column(modifier = Modifier.padding(16.dp)) {

                        Text("₹${item["amount"]}", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text(item["purpose"].toString(), fontSize = 16.sp, color = Color.DarkGray)

                        Spacer(Modifier.height(10.dp))

                        Text("📅 Date: ${item["date"]}")
                        Text("⏰ Time: ${item["time"]}")
                        Text("🗓 Month: ${item["month"]}")
                        Text("📆 Week: ${item["week"]}")
                    }
                }
            }
        }

        Spacer(Modifier.height(100.dp))
    }
}
