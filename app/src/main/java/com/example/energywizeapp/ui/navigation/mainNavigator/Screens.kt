package com.example.energywizeapp.ui.navigation.mainNavigator

sealed class Screens(val route: String) {
    object SignInScreen : Screens(route = "signIn")
    object SignUpScreen : Screens(route = "signUp")
    object ProfileDetailsScreen : Screens(route = "profile")

}