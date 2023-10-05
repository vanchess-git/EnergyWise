package com.example.energywizeapp.ui.composables

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
    onClick: () -> Unit? = {  },
    listOfTimeTypes: List<TimeFrameTypeItem>? = null,
    selectedTimeTypeIndex: Int = 1,
) {
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
            listOfTimeTypes?.forEachIndexed { index, item ->
                TimeFrameTypeCard(
                    modifier = Modifier,
                    timeFrameTypeItem = item,
                    selectedTimeTypeIndex = selectedTimeTypeIndex,
                    index = index,
                    )
            }
        }
    }
}