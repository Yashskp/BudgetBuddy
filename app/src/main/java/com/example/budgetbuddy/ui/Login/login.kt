package com.example.budgetbuddy.ui.login

import android.util.Log
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    auth: FirebaseAuth,
    navController: NavController
) {

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

            Text("Login", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(20.dp))

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

            // LOGIN BUTTON
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {

                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {

                                // Extract username from email
                                val username = email.substringBefore("@")
                                Log.d("Login", "Username = $username")

                                // Navigate to home/{username}
                                navController.navigate("home/$username") {
                                    popUpTo("login") { inclusive = true }
                                }

                            } else {
                                Toast.makeText(
                                    navController.context,
                                    task.exception?.message ?: "Login Failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            ) {
                Text("Login")
            }

            Spacer(Modifier.height(15.dp))

            TextButton(onClick = {
                navController.navigate("signup")
            }) {
                Text("Don't have an account? Sign up")
            }
        }
    }
}
