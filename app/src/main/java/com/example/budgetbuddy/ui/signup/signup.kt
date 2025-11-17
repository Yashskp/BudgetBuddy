package com.example.budgetbuddy.ui.signup

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(
    auth: FirebaseAuth,
    navController: NavController
) {

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Create Account", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(20.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(15.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(15.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(25.dp))

            // SIGN UP BUTTON
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {

                    if (username.isBlank()) {
                        Toast.makeText(navController.context, "Enter username", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {

                                val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                                val db = FirebaseFirestore.getInstance()

                                val userData = mapOf(
                                    "username" to username,
                                    "email" to email
                                )

                                db.collection("users").document(userId).set(userData)

                                navController.navigate("login") {
                                    popUpTo("signup") { inclusive = true }
                                }

                                Toast.makeText(
                                    navController.context,
                                    "Account created! Please log in.",
                                    Toast.LENGTH_SHORT
                                ).show()

                            } else {
                                Toast.makeText(
                                    navController.context,
                                    task.exception?.message ?: "Signup Failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            ) {
                Text("Sign Up")
            }

            Spacer(Modifier.height(15.dp))

            TextButton(onClick = {
                navController.navigate("login")
            }) {
                Text("Already have an account? Login")
            }
        }
    }
}
