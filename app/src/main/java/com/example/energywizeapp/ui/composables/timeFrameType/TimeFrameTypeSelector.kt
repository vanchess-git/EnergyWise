package com.example.energywizeapp.ui.composables.timeFrameType

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showBackground = true)
@Composable
fun TimeFrameTypeSelector(
    modifier: Modifier = Modifier,
    timeFrameType: String? = null,
    onClick: (Int) -> Unit? = {  },
    listOfTimeTypes: List<String> = listOf(
        "day",
        "week",
        "month",
        "year",
    ),
    selectedTimeTypeIndex: Int = 0,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth(),
        color = Color(0x00000000)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            listOfTimeTypes?.forEachIndexed { index, item ->
                TimeFrameTypeCard(
                    modifier = Modifier,
                    timeFrameTypeItem = item,
                    selectedTimeTypeIndex = selectedTimeTypeIndex,
                    index = index,
                    onClick = { onClick(index) }
                    )
            }
        }
    }
}