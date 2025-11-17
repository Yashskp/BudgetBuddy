package com.example.budgetbuddy.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.budgetbuddy.ui.home.HomeScreen
import com.example.budgetbuddy.ui.login.LoginScreen
import com.example.budgetbuddy.ui.signup.SignupScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavGraph(auth: FirebaseAuth) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        // Login Screen
        composable("login") {
            LoginScreen(auth, navController)
        }

        // Signup Screen
        composable("signup") {
            SignupScreen(auth, navController)
        }

        // Home Screen → with username parameter
        composable("home/{username}") { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: "User"
            HomeScreen(username) // <-- This is line 35
        }

    }
}
