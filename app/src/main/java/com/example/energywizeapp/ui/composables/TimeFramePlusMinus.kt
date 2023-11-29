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
    isLoading: Boolean = false
) {
    when (timeFrameType) {
        "day" -> { SelectedDay(
            modifier = modifier,
            timeFrameType = timeFrameType,
            decrement = decrement,
            increment = increment,
            time = time,
            calendar = calendar,
        ) }
        "week" -> { SelectedWeek(
            modifier = modifier,
            timeFrameType = timeFrameType,
            decrement = decrement,
            increment = increment,
            time = time,
            calendar = calendar,
        ) }
        "month" -> { SelectedMonth(
            modifier = modifier,
            timeFrameType = timeFrameType,
            decrement = decrement,
            increment = increment,
            time = time,
            calendar = calendar,
        ) }
        "year" -> { SelectedYear(
            modifier = modifier,
            timeFrameType = timeFrameType,
            decrement = decrement,
            increment = increment,
            time = time,
            calendar = calendar,
        ) }
        else -> { SelectedDay(
            modifier = modifier,
            timeFrameType = timeFrameType,
            decrement = decrement,
            increment = increment,
            time = time,
            calendar = calendar,
        ) }
    }
}
@Preview(showBackground = true)
@Composable
private fun SelectedDay(
    modifier: Modifier = Modifier,
    timeFrameType: String = "day",
    decrement: () -> Unit = {  },
    increment: () -> Unit = {  },
    time: Long = 0L,
    calendar: Calendar = Calendar.getInstance(),
    isLoading: Boolean = false
) {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    Surface(
        modifier = modifier
            .fillMaxWidth(),
        color = Color(0x00000000),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            IconButton(
                onClick = decrement,
                modifier = Modifier
                    .weight(1f),
                enabled = !isLoading // Disable if loading
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "decrease"
                )
            }
            Row(
                modifier = Modifier
                    .weight(3f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Text(text = dateFormat.format(time).toString())
            }
            IconButton(
                onClick = increment,
                modifier = Modifier
                    .weight(1f),
                enabled = !isLoading // Disable if loading
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "increase"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SelectedWeek(
    modifier: Modifier = Modifier,
    timeFrameType: String = "week",
    decrement: () -> Unit = {  },
    increment: () -> Unit = {  },
    time: Long = 0L,
    calendar: Calendar = Calendar.getInstance(),
) {
    val dateFormat = SimpleDateFormat("yyyy", Locale.getDefault())
    calendar.timeInMillis = time
    Surface(
        modifier = modifier
            .fillMaxWidth(),
        color = Color(0x00000000),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            IconButton(
                onClick = decrement,
                modifier = Modifier
                    .weight(1f),
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "decrease"
                )
            }
            Row(
                modifier = Modifier
                    .weight(3f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Text(text = calendar.get(Calendar.WEEK_OF_YEAR).toString())
                Text(text = dateFormat.format(time).toString())
            }
            IconButton(
                onClick = increment,
                modifier = Modifier
                    .weight(1f),
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "increase"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SelectedMonth(
    modifier: Modifier = Modifier,
    timeFrameType: String = "month",
    decrement: () -> Unit = {  },
    increment: () -> Unit = {  },
    time: Long = 0L,
    calendar: Calendar = Calendar.getInstance(),
) {
    val dateFormat = SimpleDateFormat("yyyy", Locale.getDefault())
    val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
    calendar.timeInMillis = time
    Surface(
        modifier = modifier
            .fillMaxWidth(),
        color = Color(0x00000000),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            IconButton(
                onClick = decrement,
                modifier = Modifier
                    .weight(1f),
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "decrease"
                )
            }
            Row(
                modifier = Modifier
                    .weight(3f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Text(text = monthFormat.format(calendar.time))
                Text(text = dateFormat.format(time).toString())
            }
            IconButton(
                onClick = increment,
                modifier = Modifier
                    .weight(1f),
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "increase"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SelectedYear(
    modifier: Modifier = Modifier,
    timeFrameType: String = "year",
    decrement: () -> Unit = {  },
    increment: () -> Unit = {  },
    time: Long = 0L,
    calendar: Calendar = Calendar.getInstance(),
) {
    val dateFormat = SimpleDateFormat("yyyy", Locale.getDefault())
    Surface(
        modifier = modifier
            .fillMaxWidth(),
        color = Color(0x00000000),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            IconButton(
                onClick = decrement,
                modifier = Modifier
                    .weight(1f),
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "decrease"
                )
            }
            Row(
                modifier = Modifier
                    .weight(3f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Text(text = dateFormat.format(time).toString())
            }
            IconButton(
                onClick = increment,
                modifier = Modifier
                        .weight(1f),
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "increase"
                )
            }
        }
    }
}