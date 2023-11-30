@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.energywizeapp.ui.navigation.mainNavigator

import android.view.Surface
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.energywizeapp.ui.screens.home.HomeView
import com.example.energywizeapp.ProfileDetails
import com.example.energywizeapp.ui.screens.settingsView.SettingsView
import com.example.energywizeapp.ui.authentification.AppModule
import com.example.energywizeapp.ui.screens.profileView.ProfileDetails
import com.example.energywizeapp.ui.screens.testView.TestView
import com.example.energywizeapp.ui.screens.signIn.SignInScreen
import com.example.energywizeapp.ui.screens.signUp.SignUpScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigator(
    auth: FirebaseAuth = Firebase.auth,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navControllerLogin = rememberNavController()

    var user by remember { mutableStateOf<FirebaseUser?>(null) }

    DisposableEffect(auth) {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            user = firebaseAuth.currentUser
        }
        auth.addAuthStateListener(listener)
        onDispose {
            auth.removeAuthStateListener(listener)
        }
    }

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
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            hasNews = false,
            route = "home",
        ),
        MainNavItem(
            title = "Settings",
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings,
            hasNews = false,
            route = "settings",
        ),
    )

    val topNavItems = listOf(
        MainNavItem(
            title = "Profile",
            selectedIcon = Icons.Filled.List,
            unselectedIcon = Icons.Outlined.List,
            hasNews = false,
            route = "profile",
        ),
        MainNavItem(
            title = "Home",
            selectedIcon = Icons.Filled.List,
            unselectedIcon = Icons.Outlined.List,
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

    var selectedTopNavItem by rememberSaveable {
        mutableStateOf(1)
    }

    Scaffold(
        topBar = {
            if (user != null) {
                TopAppBar(
                    title = {
                        Text(text = items[selectedItemIndex].title)
                    },
                    // if we end up going for a nav drawer, it should open up with the icon below.
                    // or we could set this icon to show the selectedItem icon or just some generic
                    // app logo.
                    navigationIcon = {},
                    actions = {
                        if (selectedItemIndex == 1) {
                            topNavItems.forEachIndexed { index, item ->
                                IconButton(
                                    onClick = { selectedTopNavItem = index }
                                ) {
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
                                            imageVector = if (index == selectedTopNavItem) {
                                                item.selectedIcon
                                            } else item.unselectedIcon,
                                            contentDescription = item.title,
                                            tint = if (index == selectedTopNavItem) {
                                                Color(0xFF00D1FF)
                                            } else Color(0xff000000),
                                        )
                                    }
                                }
                            }
                        }
                    }
                )
            } else {
                /** Login TopBar if needed */
            }
        },

        /**
         * this is the full bottom bar.
         * label: defines the text below the Icon
         * icon: is the icon itself, it also contains the badge that can be used to indicate
         *       changes withing the nov route.
         * */
        bottomBar = {
            if (user != null) {
                NavigationBar(
                    containerColor = Color(0xffffffff),

            ) {
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
                                fontSize = 10.sp,
                                color = if (index == selectedItemIndex) {
                                    Color(0xFF00D1FF)
                                } else Color(0xff000000),
                            )
                        },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (item.badgeCount != null) {
                                        Badge {
                                            Text(text = item.badgeCount.toString(),)
                                            }
                                        } else if (item.hasNews) {
                                            Badge()
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (index == selectedItemIndex) {
                                            item.selectedIcon
                                        } else item.unselectedIcon,
                                        contentDescription = item.title,
                                    tint = if (index == selectedItemIndex) {
                                        Color(0xFF00D1FF)
                                    } else Color(0xff000000),
                                    )
                                }
                            }
                        )
                    }
                }
            } else {
                /** login bottom bar if needed */
            }
        }
    ) { innerPadding ->
        if (user != null) {NavHost(
            navController = navController,
           /*TODO: starting destination SignIn*/ startDestination = "home",
            Modifier
                .padding(innerPadding)
                .background(color = Color(0xffF2F2F2))
                .fillMaxSize(),
        ) {
            composable("profile") { ProfileDetails() }
            // Function added here only for demonstrating purpose, will be deleted later from here and will be called from ProfileView
            composable("home") { HomeView(selectedTopNavIndex = selectedTopNavItem) }
                composable("settings") { SettingsView() }

            }
        } else {
            NavHost(
                navController = navControllerLogin,
                startDestination = "signIn",
                Modifier.padding(innerPadding)
            ) {
                composable("signIn") {
                    SignInScreen(navControllerLogin)

                }
                composable("signUp") {
                    SignUpScreen(navControllerLogin)

                }
            }
        }
    }
}