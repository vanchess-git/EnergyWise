package com.example.energywizeapp.ui.navigation.mainNavigator

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.List
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * This data class is used by the Main Navigator
 * but technically it could be used by other navigators if needed
 * */
data class MainNavItem(
    val title: String = "",
    val selectedIcon: ImageVector = Icons.Default.List,
    val unselectedIcon: ImageVector = Icons.Outlined.List,
    val hasNews: Boolean = false,
    val badgeCount: Int? = null,
    val route: String,
)
