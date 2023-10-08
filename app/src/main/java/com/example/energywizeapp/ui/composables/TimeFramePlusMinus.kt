package com.example.energywizeapp.ui.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Preview(showBackground = true, showSystemUi = false)
@Composable
fun TimeFramePlusMinus(
    modifier: Modifier = Modifier,
    timeFrameType: String = "day",
    decrement: () -> Unit = {  },
    increment: () -> Unit = {  },
    time: Long = 0L,
    calendar: Calendar = Calendar.getInstance(),
) {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(shape = RectangleShape, width = 1.dp, color = Color.Black),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            IconButton(onClick = decrement) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "decrease"
                )
            }
            Text(text = dateFormat.format(time).toString())
            Text(text = timeFrameType)

            IconButton(onClick = increment) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "increase"
                )
            }
        }
    }
}