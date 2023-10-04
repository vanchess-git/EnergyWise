@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.energywizeapp.ui.navigation.mainNavigator

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.energywizeapp.ProfileDetails

@Composable
fun MainNavigator(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    /** this is the list for navigation items
     *  if some route needs to be changed
     *  remember to implement the required changes to this list.
     *  title: is used for both top and bottom bar titling.
     *  selectedIcon: is the one used when the navigation item is selected.
     *  unselectedIcon: is the opposite of the above icon.
     *  hasNews: should stay false, but if you need to indicate changes under a route
     *           make it show here too.
     *  route: is just for navigator to differentiate paths from each other.
     *  */
    val items = listOf(
        MainNavItem(
            title = "Profile",
            selectedIcon = Icons.Filled.AccountCircle,
            unselectedIcon = Icons.Outlined.AccountCircle,
            hasNews = false,
            route = "profile",
        ),
        MainNavItem(
            title = "Home",
            selectedIcon = Icons.Filled.Warning,
            unselectedIcon = Icons.Outlined.Warning,
            hasNews = false,
            route = "home",
        ),
        MainNavItem(
            title = "Settings",
            selectedIcon = Icons.Filled.List,
            unselectedIcon = Icons.Outlined.List,
            hasNews = false,
            route = "settings",
        ),
    )

    // this defines the starting point for the navigation 0 being the left most item.
    // DON'T forget to check the NavHost later in this file as the starting route is
    // actually selected there for now
    var selectedItemIndex by rememberSaveable {
        mutableStateOf(1)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = items[selectedItemIndex].title)
                },
                // if we end up going for a nav drawer, it should open up with the icon below.
                // or we could set this icon to show the selectedItem icon or just some generic
                // app logo.
                navigationIcon = { }
            )
        },

        /**
         * this is the full bottom bar.
         * label: defines the text below the Icon
         * icon: is the icon itself, it also contains the badge that can be used to indicate
         *       changes withing the nov route.
         * */
        bottomBar = {
            NavigationBar() {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItemIndex == index,
                        onClick = {
                            selectedItemIndex = index
                            navController.navigate(item.route)
                        },
                        label = {
                            Text(
                                text = item.title,
                                fontSize = 6.sp,
                            )
                        },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (item.badgeCount != null) {
                                        Badge {
                                            Text(text = item.badgeCount.toString())
                                        }
                                    } else if(item.hasNews) {
                                        Badge()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (index == selectedItemIndex) {
                                        item.selectedIcon
                                    } else item.unselectedIcon,
                                    contentDescription = item.title
                                )
                            }
                        }
                    )
                }
            }
        }
    ) {innerPadding ->
        NavHost(navController = navController, startDestination = "home", Modifier.padding(innerPadding)) {
            composable("profile") { ProfileDetails() }
            // Function added here only for demonstrating purpose, will be deleted later from here and will be called from ProfileView
            composable("home") { /*TODO: call the proper view composable */ }
            composable("settings") { /*TODO: call the proper view composable */ }
        }
    }
}