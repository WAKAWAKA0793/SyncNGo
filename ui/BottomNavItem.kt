package com.example.tripshare.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String,
    val isPrimary: Boolean = false   // 👈 NEW
)

val bottomNavItems = listOf(
    BottomNavItem("home", Icons.Filled.Home, "Home"),
    BottomNavItem("community", Icons.Filled.Explore, "Discover"),
    BottomNavItem("createTrip", Icons.Filled.Add, "New Trip", isPrimary = true), // ⭐
    BottomNavItem("emergency", Icons.Filled.Notifications, "Emergency"),
    BottomNavItem("profile", Icons.Filled.Person, "Profile"),
)

