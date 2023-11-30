package com.example.energywizeapp

import android.content.Intent
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.energywizeapp.ui.navigation.mainNavigator.MainNavigator
import com.example.energywizeapp.ui.theme.EnergyWizeAppTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

// TODO service can be launched, but it is not aware of correct instancing
// TODO handle Context related stuff
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    fun startNotSer() {
        startService(Intent(this@MainActivity, NotificationService::class.java))
    }
    fun stopNotSer() {
        stopService(Intent(this@MainActivity, NotificationService::class.java))
    }
    private val auth: FirebaseAuth = Firebase.auth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EnergyWizeAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    MainNavigator(auth = auth)
                }
            }
        }

    }
}


