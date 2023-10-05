package com.example.energywizeapp.ui.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class TimeFrameTypeItem(
    val timeType: String = "",
    val visibleName: String = "",
    val selectedColor: Color = Color(0xff0000ff),
    val unselectedColor: Color = Color(0xfffffff)
)

@Preview(showBackground = true)
@Composable
fun TimeFrameTypeCard(
    modifier: Modifier = Modifier,
    timeFrameTypeItem: TimeFrameTypeItem = TimeFrameTypeItem(),
    selectedTimeTypeIndex: Int = 1,
    index: Int = 0,
) {
    Column(
        modifier = Modifier
    ) {
        Surface(
            modifier = Modifier
                .height(32.dp)
                .width(32.dp)
                .border(shape = CircleShape, width = 1.dp, color = Color.Black),
            color = if (index == selectedTimeTypeIndex) {
                timeFrameTypeItem.selectedColor
            } else {
                timeFrameTypeItem.unselectedColor
                   },
            shape = CircleShape
        ) {

        }
        Text(text = timeFrameTypeItem.visibleName)
    }
}