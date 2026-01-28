package com.example.tripshare.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun TripBottomBar(
    currentRoute: String,
    onNavClick: (String) -> Unit,
    messageCount: Int = 0
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Black,
        shadowElevation = 12.dp
    ) {
        Row(
            modifier = Modifier
                .height(64.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            BottomItem("home", "Home", currentRoute, onNavClick, Modifier.weight(1f)) {
                Icon(bottomNavItems.first { it.route == "home" }.icon, null)
            }

            BottomItem("community", "Community", currentRoute, onNavClick, Modifier.weight(1f)) {
                Icon(bottomNavItems.first { it.route == "community" }.icon, null)
            }

            // 🟦 CENTER ADD BUTTON (ROUNDED SQUARE)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { onNavClick("createTrip") }
                        .shadow(8.dp, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "New Trip",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            BottomItem(
                route = "emergency",
                label = "Emergency",
                currentRoute = currentRoute,
                onNavClick = onNavClick,
                modifier = Modifier.weight(1f),
                badgeCount = messageCount
            ) {
                Icon(bottomNavItems.first { it.route == "emergency" }.icon, null)
            }

            BottomItem("profile", "Me", currentRoute, onNavClick, Modifier.weight(1f)) {
                Icon(bottomNavItems.first { it.route == "profile" }.icon, null)
            }
        }
    }
}

@Composable
private fun BottomItem(
    route: String,
    label: String,
    currentRoute: String,
    onNavClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    badgeCount: Int = 0,
    icon: @Composable () -> Unit
) {
    val selected = currentRoute == route
    val color =
        if (selected) MaterialTheme.colorScheme.primary
        else Color.White.copy(alpha = 0.7f)

    Column(
        modifier = modifier
            .fillMaxHeight()
            .clickable { onNavClick(route) },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BadgedBox(
            badge = {
                if (badgeCount > 0) {
                    Badge(containerColor = Color.Red) {
                        Text(badgeCount.toString())
                    }
                }
            }
        ) {
            CompositionLocalProvider(LocalContentColor provides color) {
                icon()
            }
        }

        Spacer(Modifier.height(4.dp))

        Text(
            text = label,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}
