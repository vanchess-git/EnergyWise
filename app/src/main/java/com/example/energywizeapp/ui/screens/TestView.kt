package com.example.energywizeapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.energywizeapp.ui.composables.TimeFramePlusMinus
import com.example.energywizeapp.ui.composables.TimeFrameTypeItem
import com.example.energywizeapp.ui.composables.TimeFrameTypeSelector

@Preview(
    showBackground = true
)
@Composable
fun TestView(
    modifier: Modifier = Modifier
) {
    val listOfTimeTypes = listOf(
        TimeFrameTypeItem(
            timeType = "day",
            visibleName = "Day",
        ),
        TimeFrameTypeItem(
            timeType = "week",
            visibleName = "Week",
        ),
        TimeFrameTypeItem(
            timeType = "month",
            visibleName = "Month",
        ),
        TimeFrameTypeItem(
            timeType = "year",
            visibleName = "Year",
        ),
    )

    var selectedTimeTypeIndex by rememberSaveable {
        mutableStateOf(0)
    }

    Column {
        TimeFramePlusMinus()
        TimeFrameTypeSelector(listOfTimeTypes = listOfTimeTypes, selectedTimeTypeIndex = selectedTimeTypeIndex)
    }
}