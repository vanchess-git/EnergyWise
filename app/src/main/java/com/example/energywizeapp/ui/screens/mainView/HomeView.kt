package com.example.energywizeapp.ui.screens.mainView

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.energywizeapp.ui.composables.TimeFramePlusMinus
import com.example.energywizeapp.ui.composables.timeFrameType.TimeFrameTypeSelector

@Preview(
    showBackground = true
)
@Composable
fun HomeView(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = viewModel(),
    selectedTopNavIndex: Int = 1,
) {
    val selectedTimeTypeIndex by homeViewModel.selectedTimeTypeIndex.collectAsState()
    val listOfTimeTypes by homeViewModel.listOfTimeTypes.collectAsState()
    val calendar by homeViewModel.calendar.collectAsState()
    val time by homeViewModel.time.collectAsState()
    val timeIncrement: () -> Unit = { homeViewModel.timeIncrement() }
    val timeDecrement: () -> Unit = { homeViewModel.timeDecrement() }


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
                Spacer(modifier = Modifier.height(8.dp))
                TimeFramePlusMinus(
                    timeFrameType = listOfTimeTypes[selectedTimeTypeIndex],
                    decrement = timeDecrement,
                    increment = timeIncrement,
                    time = time,
                    calendar = calendar,
                )
                Spacer(modifier = Modifier.height(16.dp))
                TimeFrameTypeSelector(
                    listOfTimeTypes = listOfTimeTypes,
                    selectedTimeTypeIndex = selectedTimeTypeIndex,
                    onClick = { index -> homeViewModel.setTimeTypeIndex(index)}
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}