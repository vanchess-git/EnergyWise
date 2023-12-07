package com.example.energywizeapp.ui.screens.profileView

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

// I know it is messy, will be fixed later
@Composable
fun ProfileDetails(
    auth: FirebaseAuth = Firebase.auth,
) {
    val currentUser = auth.currentUser
    val displayName = currentUser?.displayName ?: "User"
    val userEmail = currentUser?.email ?: "userEmail"


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
                Text(text = "Hei, $displayName!", fontWeight = FontWeight.Bold)
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
                modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.SpaceBetween
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
                    Icon(imageVector = Icons.Default.Email, contentDescription = "Email")
                    Spacer(modifier = Modifier.padding(6.dp))
                    Text(text = userEmail)
                }
                Spacer(modifier = Modifier.padding(4.dp))
                Row(
                    modifier = Modifier, horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Icon(imageVector = Icons.Default.AccountBox, contentDescription = "Plan")
                    Spacer(modifier = Modifier.padding(6.dp))
                    Text(text = "Omavoima SuperHalpa")
                }
            }
        }
        Button(onClick = { auth.signOut() }) {
            Text(text = "Logout")
        }
    }
}