package com.example.energywizeapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.energywizeapp.ui.composables.TimeFramePlusMinus
import com.example.energywizeapp.ui.composables.timeFrameType.TimeFrameTypeSelector

@Preview(
    showBackground = true
)
@Composable
fun TestView(
    modifier: Modifier = Modifier,
    testViewModel: TestViewModel = viewModel()
) {
    val selectedTimeTypeIndex by testViewModel.selectedTimeTypeIndex.collectAsState()
    val listOfTimeTypes by testViewModel.listOfTimeTypes.collectAsState()
    val calendar by testViewModel.calendar.collectAsState()
    val time by testViewModel.time.collectAsState()
    val timeIncrement: () -> Unit = { testViewModel.timeIncrement() }
    val timeDecrement: () -> Unit = { testViewModel.timeDecrement() }


    Column {
        Spacer(modifier = Modifier.height(32.dp))
        TimeFramePlusMinus(
            timeFrameType = listOfTimeTypes[selectedTimeTypeIndex],
            decrement = timeDecrement,
            increment = timeIncrement,
            time = time,
            calendar = calendar,
            )
        Spacer(modifier = Modifier.height(32.dp))
        TimeFrameTypeSelector(
            listOfTimeTypes = listOfTimeTypes,
            selectedTimeTypeIndex = selectedTimeTypeIndex,
            onClick = { index -> testViewModel.setTimeTypeIndex(index)}
        )
    }
}