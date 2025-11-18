package com.example.budgetbuddy.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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

        composable("login") {
            LoginScreen(auth, navController)
        }

        composable("signup") {
            SignupScreen(auth, navController)
        }

        // FIX: Update the home route to handle 'weekly' and 'monthly' parameters
        composable(
            // The route now includes optional 'weekly' and 'monthly' query parameters
            route = "home/{username}?weekly={weekly}&monthly={monthly}",
            arguments = listOf(
                navArgument("username") {
                    type = NavType.StringType
                    // You could add a defaultValue here if needed, e.g., defaultValue = "User"
                },
                navArgument("weekly") {
                    type = NavType.BoolType
                    defaultValue = true // Default 'weekly' to true if not provided
                },
                navArgument("monthly") {
                    type = NavType.BoolType
                    defaultValue = true // Default 'monthly' to true if not provided
                }
            )
        ) { backStackEntry ->
            // Extract all arguments from the backStackEntry, providing fallbacks
            val username = backStackEntry.arguments?.getString("username") ?: "User"
            val weekly = backStackEntry.arguments?.getBoolean("weekly") ?: true
            val monthly = backStackEntry.arguments?.getBoolean("monthly") ?: true

            // Call HomeScreen with all required parameters
            HomeScreen(
                username = username,
                weekly = weekly,
                monthly = monthly
            )
        }
    }
}
