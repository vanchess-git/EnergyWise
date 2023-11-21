package com.example.energywizeapp.ui.composables

import android.graphics.Typeface
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.energywizeapp.data.api.EntsoResponse
import com.example.energywizeapp.util.calculateTimeSpanInDays
import com.example.energywizeapp.util.isDateToday
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollState
import com.patrykandpatrick.vico.compose.component.shape.shader.fromBrush
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.compose.dimensions.dimensionsOf
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.DefaultAlpha
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun VicoChart(priceData: EntsoResponse, selectedTimeFrame: String) {
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

    val currentHourPriceTextState = remember { mutableStateOf("Hetki...") }
    val currentHourPriceCardText by currentHourPriceTextState

    LaunchedEffect(priceData) {
        datasetForModel.clear()
        datasetLineSpec.clear()

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

        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        var currentHourPrice: Double? = null

        val timeSpanInDays = calculateTimeSpanInDays(priceData)
        var timeSpan = "day"

        when {
            timeSpanInDays <= 1 -> timeSpan = "day"
            timeSpanInDays <= 7 -> timeSpan = "week"
            timeSpanInDays <= 31 -> timeSpan = "month"
            timeSpanInDays <= 365 -> timeSpan = "year"
        }

        when (timeSpan) {
            "day" -> {
                Log.d("LaunchedEffect Data", timeSpan)
                for (timeSeries in priceData.publicationMarketDocument.timeSeriesList ?: emptyList()) {
                    val timeInterval = timeSeries.period?.timeInterval
                    val startDateTime = timeInterval?.end
                    var xPos = 0f
                    for (point in timeSeries.period?.points ?: emptyList()) {
                        val position = point.position
                        val priceAmountBeforeVAT = (point.priceAmount * 0.1)
                        val priceAmountBeforeFormatting = priceAmountBeforeVAT * (1 + 0.24)
                        val priceAmount = BigDecimal(priceAmountBeforeFormatting)
                            .setScale(2, RoundingMode.HALF_UP)
                            .toDouble()

                        if(isDateToday(startDateTime) && selectedTimeFrame == "day") {
                            if (position == currentHour) {
                                currentHourPrice = priceAmount
                            }
                        }

                        dataPoints.add(
                            FloatEntry(x = xPos, y = priceAmount.toFloat())
                        )
                        xPos += 1f
                    }
                }
            }
            "week" -> {
                val dailyAverages = mutableMapOf<Int, Double>()
                val dailyCounts = mutableMapOf<Int, Int>()

                Log.d("LaunchedEffect Data", timeSpan)
                for (timeSeries in priceData.publicationMarketDocument.timeSeriesList ?: emptyList()) {
                    val timeInterval = timeSeries.period?.timeInterval
                    val startDateTime = timeInterval?.end

                    for (point in timeSeries.period?.points ?: emptyList()) {
                        val position = point.position
                        val priceAmountBeforeVAT = (point.priceAmount * 0.1)
                        val priceAmountBeforeFormatting = priceAmountBeforeVAT * (1 + 0.24)
                        val priceAmount = BigDecimal(priceAmountBeforeFormatting)
                            .setScale(2, RoundingMode.HALF_UP)
                            .toDouble()

                        if(isDateToday(startDateTime) && selectedTimeFrame == "day") {
                            if (position == currentHour) {
                                currentHourPrice = priceAmount
                            }
                        }

                        val calendar = Calendar.getInstance()
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'", Locale.US)
                        calendar.time = startDateTime?.let { dateFormat.parse(it) }!!
                        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

                        if (dailyAverages.containsKey(dayOfMonth)) {
                            dailyAverages[dayOfMonth] = dailyAverages[dayOfMonth]!! + priceAmount
                            dailyCounts[dayOfMonth] = dailyCounts[dayOfMonth]!! + 1
                        } else {
                            dailyAverages[dayOfMonth] = priceAmount
                            dailyCounts[dayOfMonth] = 1
                        }
                    }
                }

                for ((day, totalAmount) in dailyAverages) {
                    val average = totalAmount / dailyCounts[day]!!
                    dataPoints.add(FloatEntry(x = day.toFloat(), y = average.toFloat()))
                }
            }
            "month" -> {
                val dailyAverages = mutableMapOf<Int, Double>()
                val dailyCounts = mutableMapOf<Int, Int>()

                Log.d("LaunchedEffect Data", timeSpan)
                for (timeSeries in priceData.publicationMarketDocument.timeSeriesList ?: emptyList()) {
                    val timeInterval = timeSeries.period?.timeInterval
                    val startDateTime = timeInterval?.end

                    for (point in timeSeries.period?.points ?: emptyList()) {
                        val position = point.position
                        val priceAmountBeforeVAT = (point.priceAmount * 0.1)
                        val priceAmountBeforeFormatting = priceAmountBeforeVAT * (1 + 0.24)
                        val priceAmount = BigDecimal(priceAmountBeforeFormatting)
                            .setScale(2, RoundingMode.HALF_UP)
                            .toDouble()

                        if(isDateToday(startDateTime) && selectedTimeFrame == "day") {
                            if (position == currentHour) {
                                currentHourPrice = priceAmount
                            }
                        }

                        val calendar = Calendar.getInstance()
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'", Locale.US)
                        calendar.time = startDateTime?.let { dateFormat.parse(it) }!!
                        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

                        if (dailyAverages.containsKey(dayOfMonth)) {
                            dailyAverages[dayOfMonth] = dailyAverages[dayOfMonth]!! + priceAmount
                            dailyCounts[dayOfMonth] = dailyCounts[dayOfMonth]!! + 1
                        } else {
                            dailyAverages[dayOfMonth] = priceAmount
                            dailyCounts[dayOfMonth] = 1
                        }
                    }
                }

                for ((day, totalAmount) in dailyAverages) {
                    val average = totalAmount / dailyCounts[day]!!
                    dataPoints.add(FloatEntry(x = day.toFloat(), y = average.toFloat()))
                }

            }
            "year" -> {
                val monthlyAverages = mutableMapOf<Int, Double>()
                val monthlyCounts = mutableMapOf<Int, Int>()

                Log.d("LaunchedEffect Data", timeSpan)
                for (timeSeries in priceData.publicationMarketDocument.timeSeriesList ?: emptyList()) {
                    val timeInterval = timeSeries.period?.timeInterval
                    val startDateTime = timeInterval?.end

                    for (point in timeSeries.period?.points ?: emptyList()) {
                        val position = point.position
                        val priceAmountBeforeVAT = (point.priceAmount * 0.1)
                        val priceAmountBeforeFormatting = priceAmountBeforeVAT * (1 + 0.24)
                        val priceAmount = BigDecimal(priceAmountBeforeFormatting)
                            .setScale(2, RoundingMode.HALF_UP)
                            .toDouble()

                        if (isDateToday(startDateTime) && selectedTimeFrame == "day") {
                            if (position == currentHour) {
                                currentHourPrice = priceAmount
                            }
                        }

                        val calendar = Calendar.getInstance()
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'", Locale.US)
                        calendar.time = startDateTime?.let { dateFormat.parse(it) }!!
                        val monthOfYear = calendar.get(Calendar.MONTH)

                        if (monthlyAverages.containsKey(monthOfYear)) {
                            monthlyAverages[monthOfYear] = monthlyAverages[monthOfYear]!! + priceAmount
                            monthlyCounts[monthOfYear] = monthlyCounts[monthOfYear]!! + 1
                        } else {
                            monthlyAverages[monthOfYear] = priceAmount
                            monthlyCounts[monthOfYear] = 1
                        }
                    }
                }

                for ((month, totalAmount) in monthlyAverages) {
                    val average = totalAmount / monthlyCounts[month]!!
                    dataPoints.add(FloatEntry(x = month.toFloat(), y = average.toFloat()))
                }
            }
            else -> {
                // TODO
            }
        }

        if (currentHourPrice != null) {
            currentHourPriceTextState.value = "$currentHourPrice snt/kWh"
        }
        datasetForModel.add(dataPoints)
        modelProducer.setEntries(datasetForModel)
    }

    Column {
        ElevatedCard(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 12.dp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 15.dp)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Sähkön hinta nyt",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                Text(
                    text = currentHourPriceCardText,
                    color = Color.White,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        ElevatedCard(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
        ) {
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
                            title = "c/kWh",
                            tickLength = 0.dp,
                            itemPlacer = AxisItemPlacer.Vertical.default(
                                maxItemCount = 6
                            ),
                            titleComponent = textComponent(
                                color = Color.White,
                                padding = axisTitlePadding,
                                margins = startAxisTitleMargins,
                                typeface = Typeface.MONOSPACE,
                                ),
                        ),
                        bottomAxis = rememberBottomAxis(
                            tickLength = 0.dp,
                            guideline = null,
                            titleComponent = textComponent(
                                color = Color.White,
                                padding = axisTitlePadding,
                                margins = bottomAxisTitleMargins,
                                typeface = Typeface.MONOSPACE,
                                ),
                            ),
                    )
                }
            }
        }
    }

}

