package com.example.energywizeapp.ui.composables.timeFrameType

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private fun selectedColor(index: Int, selectedTimeTypeIndex: Int): Color {
    if (index == selectedTimeTypeIndex) {
        return Color(0xff0000ff)
    } else {
        return Color(0xffffffff)
    }
}
@Preview(showBackground = true)
@Composable
fun TimeFrameTypeCard(
    modifier: Modifier = Modifier,
    timeFrameTypeItem: String = "day",
    selectedTimeTypeIndex: Int = 0,
    index: Int = 0,
    onClick: () -> Unit = { },
) {
    Column(
        modifier = Modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Surface(
            modifier = Modifier
                .height(32.dp)
                .width(32.dp)
                .border(shape = CircleShape, width = 1.dp, color = Color.Black),
            color = selectedColor(index = index, selectedTimeTypeIndex = selectedTimeTypeIndex),
            shape = CircleShape
        ) {

        }
        Text(
            text = timeFrameTypeItem,
            fontSize = 10.sp
        )
    }
}