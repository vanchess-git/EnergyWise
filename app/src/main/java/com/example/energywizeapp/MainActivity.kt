package com.example.energywizeapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.energywizeapp.ui.authentification.GoogleAuthUiClient
import com.example.energywizeapp.ui.composables.TimeFramePlusMinus
import com.example.energywizeapp.ui.navigation.mainNavigator.MainNavigator
import com.example.energywizeapp.ui.screens.SignInView.SignInView
import com.example.energywizeapp.ui.screens.SignInView.SignInViewModel
import com.example.energywizeapp.ui.screens.SignInView.UserData
import com.example.energywizeapp.ui.theme.EnergyWizeAppTheme
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EnergyWizeAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "sign_in") {
                        composable("sign_in") {
                            val viewModel = viewModel<SignInViewModel>()
                            val state by viewModel.state.collectAsStateWithLifecycle()

                            LaunchedEffect(key1 = Unit) {
                                if (googleAuthUiClient.getSignedInUser() != null) {
                                    navController.navigate("profile")
                                }
                            }

                            val launcher = rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.StartIntentSenderForResult(),
                                onResult = { result ->
                                    if (result.resultCode == RESULT_OK) {
                                        lifecycleScope.launch {
                                            val signInResult = googleAuthUiClient.signInWithIntent(
                                                intent = result.data ?: return@launch
                                            )
                                            viewModel.onSignInResult(signInResult)
                                        }
                                    }
                                }
                            )

                            LaunchedEffect(key1 = state.isSignInSuccessful) {
                                if (state.isSignInSuccessful) {
                                    Toast.makeText(
                                        applicationContext,
                                        "Sign in successful",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    navController.navigate("profile")
                                    viewModel.resetState()
                                }
                            }

                            SignInView(
                                state = state,
                                onSignInClick = {
                                    lifecycleScope.launch {
                                        val signInIntentSender = googleAuthUiClient.signIn()
                                        launcher.launch(
                                            IntentSenderRequest.Builder(
                                                signInIntentSender ?: return@launch
                                            ).build()
                                        )
                                    }
                                }
                            )
                        }
                        composable("profile") {
                            ProfileDetails(
                            )
                        }
                    }
                }
            }
        }
    }
}


// I know it is messy, will be fixed later
@Composable
fun ProfileDetails(
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color = Color.LightGray)
                .padding(8.dp)

        ) {
            Row(
                modifier = Modifier
                    .padding(10.dp)
                    .align(Alignment.CenterVertically),
            ) {
                Text(text = "Hei, Jorma Sähköinen!", fontWeight = FontWeight.Bold)
            }
        }
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(color = Color.LightGray)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier, horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Icon(imageVector = Icons.Default.Home, contentDescription = "Home")
                    Spacer(modifier = Modifier.padding(6.dp))
                    Text(text = "Valkyriankatu 12 C 36")
                }
                Spacer(modifier = Modifier.padding(4.dp))
                Row(
                    modifier = Modifier, horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = "Info")
                    Spacer(modifier = Modifier.padding(6.dp))
                    Text(text = "Sopimukset")
                }
                Spacer(modifier = Modifier.padding(4.dp))
                Row(
                    modifier = Modifier, horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Icon(imageVector = Icons.Default.Email, contentDescription = "Emal")
                    Spacer(modifier = Modifier.padding(6.dp))
                    Text(text = "sahko.jorma@gmail.com")
                }
                Spacer(modifier = Modifier.padding(4.dp))
                Row(
                    modifier = Modifier, horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Icon(imageVector = Icons.Default.AccountBox, contentDescription = "Plan")
                    Spacer(modifier = Modifier.padding(6.dp))
                    Text(text = "Omavoima super halpa")
                }
            }
        }
    }
}
