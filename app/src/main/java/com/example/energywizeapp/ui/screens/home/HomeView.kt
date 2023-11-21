package com.example.energywizeapp.ui.screens.home


import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.energywizeapp.ui.composables.TimeFramePlusMinus
import com.example.energywizeapp.ui.composables.VicoChart
import com.example.energywizeapp.ui.composables.timeFrameType.TimeFrameTypeSelector

@SuppressLint("RememberReturnType", "StateFlowValueCalledInComposition")
@Composable
fun HomeView(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = viewModel()
) {

    val selectedTimeTypeIndex by homeViewModel.selectedTimeTypeIndex.collectAsState()
    val listOfTimeTypes by homeViewModel.listOfTimeTypes.collectAsState()
    val calendar by homeViewModel.calendar.collectAsState()
    val time by homeViewModel.time.collectAsState()
    val timeIncrement: () -> Unit = { homeViewModel.timeIncrement() }
    val timeDecrement: () -> Unit = { homeViewModel.timeDecrement() }
    val entsoResponse = homeViewModel.entsoResponseState.value
    val loading = homeViewModel.loadingState.value

    val selectedTimeFrame = remember(selectedTimeTypeIndex) {
        when (selectedTimeTypeIndex) {
            0 -> "day"
            1 -> "week"
            2 -> "month"
            3 -> "year"
            else -> "day" // Default to "day" or handle it as needed
        }
    }
    val selectedDate = remember(time) { time }
    val selectedWeek = remember(time) { time }
    val selectedMonth = remember(time) { time }
    val selectedYear = remember(time) { time }


    LaunchedEffect(selectedTimeTypeIndex, calendar, time) {
        homeViewModel.fetchPrices(
            timeFrame = selectedTimeFrame,
            selectedDate = selectedDate,
            selectedWeek = selectedWeek,
            selectedMonth = selectedMonth,
            selectedYear = selectedYear
        )

    }

    Box(modifier = Modifier.fillMaxSize().padding(10.dp), contentAlignment = Alignment.TopCenter) {

        Column() {
            if (entsoResponse == null || loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(50.dp)
                            .align(Alignment.Center)
                    )
                }
            } else {
                VicoChart(entsoResponse, selectedTimeFrame)
            }
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
                onClick = { index -> homeViewModel.setTimeTypeIndex(index)}
            )
        }
    }
}