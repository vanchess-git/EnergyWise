package com.example.energywizeapp.ui.screens.settingsView

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Preview(
    showBackground = true
)
@Composable
fun SettingsView(
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel = viewModel(),
) {
    val context = LocalContext.current
    val isServiceRunning by settingsViewModel.isServiceRunning.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = Color(0xffffffff),
            shape = RoundedCornerShape(size = 8.dp)
        ) {
            Column {
                Button(
                    onClick = {
                        settingsViewModel.startService(context)
                    },
                    enabled = !isServiceRunning
                ) {
                    Text(text = "Service ON")
                }
                Button(
                    onClick = {
                        settingsViewModel.stopService(context)
                    },
                    enabled = isServiceRunning
                ) {
                    Text(text = "Service OFF")
                }
            }
        }
    }
}