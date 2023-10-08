package com.example.energywizeapp.ui.composables

import android.graphics.Typeface
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollState
import com.patrykandpatrick.vico.compose.component.shape.shader.fromBrush
import com.patrykandpatrick.vico.compose.component.shapeComponent
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.compose.dimensions.dimensionsOf
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.DefaultAlpha
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry

@Composable
fun VitoChart() {
    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val bottomAxisValueFormatterDay =
        AxisValueFormatter<AxisPosition.Horizontal.Bottom> { x, _ -> daysOfWeek[x.toInt() % daysOfWeek.size] }

    val timesOfDay = listOf(
        "00",
        "01",
        "02",
        "03",
        "04",
        "05",
        "06",
        "07",
        "08",
        "09",
        "10",
        "11",
        "12",
        "13",
        "14",
        "15",
        "16",
        "17",
        "18",
        "19",
        "20",
        "21",
        "22",
        "23"
    )
    val bottomAxisValueFormatterTime =
        AxisValueFormatter<AxisPosition.Horizontal.Bottom> { x, _ -> timesOfDay[x.toInt() % timesOfDay.size] }

    val axisTitleHorizontalPaddingValue = 8.dp
    val axisTitleVerticalPaddingValue = 2.dp
    val axisTitlePadding =
        dimensionsOf(axisTitleHorizontalPaddingValue, axisTitleVerticalPaddingValue)
    val axisTitleMarginValue = 4.dp
    val startAxisTitleMargins = dimensionsOf(end = axisTitleMarginValue)
    val bottomAxisTitleMargins = dimensionsOf(top = axisTitleMarginValue)

    val refreshDataset = remember { mutableIntStateOf(0) }
    val modelProducer = remember { ChartEntryModelProducer() }
    val datasetForModel = remember { mutableStateListOf(listOf<FloatEntry>()) }
    val datasetLineSpec = remember { arrayListOf<LineChart.LineSpec>() }
    val scrollState = rememberChartScrollState()

    LaunchedEffect(key1 = refreshDataset.intValue) {
        datasetForModel.clear()
        datasetLineSpec.clear()
        var xPos = 0f
        val dataPoints = arrayListOf<FloatEntry>()
        datasetLineSpec.add(
            LineChart.LineSpec(
                lineColor = Blue.toArgb(),
                lineBackgroundShader = DynamicShaders.fromBrush(
                    brush = Brush.verticalGradient(
                        listOf(
                            Blue.copy(DefaultAlpha.LINE_BACKGROUND_SHADER_START),
                            Blue.copy(DefaultAlpha.LINE_BACKGROUND_SHADER_END)
                        )
                    )
                )
            )
        )
        for (i in 1..24) {
            val randomYFloat = (1..15).random().toFloat()

            dataPoints.add(FloatEntry(x = xPos, y = randomYFloat))
            xPos += 1f
        }

        Log.d("datapoints", dataPoints.toString())
        datasetForModel.add(dataPoints)

        modelProducer.setEntries(datasetForModel)

    }

    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
        Text(text = "Sähkön hinta tänään", modifier = Modifier.fillMaxWidth().padding(10.dp), textAlign = TextAlign.Center)
        if (datasetForModel.isNotEmpty()) {
            ProvideChartStyle {
                Chart(
                    modifier = Modifier.padding(10.dp),
                    chart = lineChart(
                        lines = datasetLineSpec
                    ),
                    chartModelProducer = modelProducer,
                    isZoomEnabled = false,
                    chartScrollState = scrollState,
                    startAxis = rememberStartAxis(
                        title = "Hinta snt/kWh",
                        tickLength = 0.dp,
                        itemPlacer = AxisItemPlacer.Vertical.default(
                            maxItemCount = 6
                        ),
                        titleComponent = textComponent(
                            color = Color.Black,
                            padding = axisTitlePadding,
                            margins = startAxisTitleMargins,
                            typeface = Typeface.MONOSPACE,

                        ),
                    ),
                    bottomAxis = rememberBottomAxis(
                        title = "Aika päivästä",
                        tickLength = 0.dp,
                        guideline = null,
                        valueFormatter = bottomAxisValueFormatterTime,
                        titleComponent = textComponent(
                            color = Color.Black,
                            padding = axisTitlePadding,
                            margins = bottomAxisTitleMargins,
                            typeface = Typeface.MONOSPACE,
                        ),

                    )
                )
            }
        }
    }
}