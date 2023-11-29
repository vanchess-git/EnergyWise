package com.example.energywizeapp.ui.composables

import android.graphics.Typeface
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.energywizeapp.R
import com.example.energywizeapp.data.api.EntsoResponse
import com.example.energywizeapp.util.calculateTimeSpanInDays
import com.example.energywizeapp.util.getPositionTimeFrame
import com.example.energywizeapp.util.isDateToday
import com.example.energywizeapp.util.rememberMarker
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollState
import com.patrykandpatrick.vico.compose.component.marker.markerComponent
import com.patrykandpatrick.vico.compose.component.shape.shader.fromBrush
import com.patrykandpatrick.vico.compose.component.shapeComponent
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.compose.dimensions.dimensionsOf
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.DefaultAlpha
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.decoration.ThresholdLine
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import java.lang.Double.max
import java.lang.Double.min
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun VicoChart(priceData: EntsoResponse, selectedTimeFrame: String, selectedTopNavIndex: Int = 1) {
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

    val highestPriceTodayTextState = remember { mutableStateOf("Hetki...") }
    val highestPriceTodayCardText by highestPriceTodayTextState
    val highestPriceTodayTimeTextState = remember { mutableStateOf("") }
    val highestPriceTodayTimeCardText by highestPriceTodayTimeTextState

    val lowestPriceTodayTextState = remember { mutableStateOf("Hetki...") }
    val lowestPriceTodayCardText by lowestPriceTodayTextState
    val lowestPriceTodayTimeTextState = remember { mutableStateOf("") }
    val lowestPriceTodayTimeCardText by lowestPriceTodayTimeTextState

    val averagePriceTodayTextState = remember { mutableStateOf("Hetki...") }
    val averagePriceTodayCardText by averagePriceTodayTextState

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
        var highestToday: Double? = null
        var lowestToday: Double? = null
        var averageToday: Double? = null
        var lowestPriceTime: String? = null
        var highestPriceTime: String? = null

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

                    var lowestPrice: Double = Double.MAX_VALUE
                    var highestPrice: Double = Double.MIN_VALUE
                    var totalPrices: Double = 0.0
                    var dataPointCount: Int = 0

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
                            // Update lowest and highest prices
                            if (priceAmount < lowestPrice) {
                                lowestPrice = priceAmount
                                lowestPriceTime = getPositionTimeFrame(position)
                            }

                            if (priceAmount > highestPrice) {
                                highestPrice = priceAmount
                                highestPriceTime = getPositionTimeFrame(position)
                            }

                            totalPrices += priceAmount
                            dataPointCount++

                            if (position == currentHour) {
                                currentHourPrice = priceAmount
                            }
                        }

                        dataPoints.add(
                            FloatEntry(x = xPos, y = priceAmount.toFloat())
                        )
                        xPos += 1f
                    }

                    if(isDateToday(startDateTime) && selectedTimeFrame == "day") {
                        val averagePrice = if (dataPointCount > 0) totalPrices / dataPointCount else 0.0
                        val formattedAverage = BigDecimal(averagePrice)
                            .setScale(2, RoundingMode.HALF_UP)
                            .toDouble()
                        highestToday = highestPrice
                        lowestToday = lowestPrice
                        averageToday = formattedAverage

                        Log.d("Price Analysis", "Lowest Price: $lowestPrice at $lowestPriceTime")
                        Log.d("Price Analysis", "Highest Price: $highestPrice at $highestPriceTime")
                        Log.d("Price Analysis", "Average Price: $averagePrice")
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
            currentHourPriceTextState.value = "$currentHourPrice"
        }

        if(highestToday != null) {
            highestPriceTodayTextState.value = "$highestToday"
            highestPriceTodayTimeTextState.value = "$highestPriceTime"
        }

        if(lowestToday != null) {
            lowestPriceTodayTextState.value = "$lowestToday"
            lowestPriceTodayTimeTextState.value = "$lowestPriceTime"

        }

        if(averageToday != null) {
            averagePriceTodayTextState.value = "$averageToday"
        }

        datasetForModel.add(dataPoints)
        modelProducer.setEntries(datasetForModel)
    }

    Column() {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .border(1.dp, Color.Gray.copy(alpha = 0.5f), shape = RoundedCornerShape(8.dp))
                .shadow(2.dp, shape = RoundedCornerShape(8.dp)),
            color = Color(0xffffffff),
            shape = RoundedCornerShape(size = 8.dp)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Electricity price now",
                    color = Color.Black,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                )
                Text(
                    text = currentHourPriceCardText,
                    color = Color(0xFF000080),
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Divider(
                    modifier = Modifier
                        .width(150.dp)
                        .shadow(2.dp, shape = RoundedCornerShape(8.dp)),
                    color = Color.Gray.copy(alpha = 0.5f),
                    thickness = 1.dp
                )
                Text(
                    text = "c/kWh",
                    color = Color.Black,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                )
            }
        }
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .border(1.dp, Color.Gray.copy(alpha = 0.5f), shape = RoundedCornerShape(8.dp))
                .shadow(2.dp, shape = RoundedCornerShape(8.dp)),
            color = Color(0xffffffff),
            shape = RoundedCornerShape(size = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically,) {
                            Icon(
                                painter = painterResource(R.drawable.trending_up),
                                contentDescription = null,
                                tint = Color(0xFF800000),
                            )
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Highest\n$highestPriceTodayTimeCardText",
                                    textAlign = TextAlign.Center
                                )

                                Text(
                                    text = highestPriceTodayCardText,
                                    color = Color(0xFF800000),
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "c/kWh",
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically,) {
                            Icon(
                                painter = painterResource(R.drawable.trending_down),
                                contentDescription = null,
                                tint = Color(0xFF008000),
                            )
                            Column(modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Lowest\n$lowestPriceTodayTimeCardText",
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = lowestPriceTodayCardText,
                                    color = Color(0xFF008000),
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "c/kWh",
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically,) {
                            Icon(
                                painter = painterResource(R.drawable.trending_flat),
                                contentDescription = null,
                                tint = Color(0xFF000080)
                            )
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Average",
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = averagePriceTodayCardText,
                                    color = Color(0xFF000080),
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "c/kWh",
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
                .padding(8.dp)
                .border(1.dp, Color.Gray.copy(alpha = 0.5f), shape = RoundedCornerShape(8.dp))
                .shadow(2.dp, shape = RoundedCornerShape(8.dp)),
            color = Color(0xffffffff),
            shape = RoundedCornerShape(size = 8.dp)
        ) {
            if (datasetForModel.isNotEmpty()) {
                ProvideChartStyle {
                    val thresholdLine = rememberThresholdLine()
                    val marker = rememberMarker()
                    Chart(
                        modifier = Modifier.padding(8.dp),
                        chart = lineChart(
                            lines = datasetLineSpec,
                            decorations = remember(thresholdLine) { listOf(thresholdLine) },
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
                                color = Color.Black,
                                padding = axisTitlePadding,
                                margins = startAxisTitleMargins,
                                typeface = Typeface.MONOSPACE,
                                ),
                        ),
                        bottomAxis = rememberBottomAxis(
                            tickLength = 0.dp,
                            guideline = null,
                            titleComponent = textComponent(
                                color = Color.Black,
                                padding = axisTitlePadding,
                                margins = bottomAxisTitleMargins,
                                typeface = Typeface.MONOSPACE,
                                ),
                            ),
                        marker = marker
                    )
                }
            }
        }
    }
}


@Composable
private fun rememberThresholdLine(): ThresholdLine {
    val line = shapeComponent(color = color2)
    val label = textComponent(
        color = Color.Black,
        background = shapeComponent(Shapes.pillShape, color2),
        padding = thresholdLineLabelPadding,
        margins = thresholdLineLabelMargins,
        typeface = Typeface.MONOSPACE,
    )
    return remember(line, label) {
        ThresholdLine(thresholdValue = THRESHOLD_LINE_VALUE, lineComponent = line, labelComponent = label)
    }
}


/*
@Composable
private fun rememberThresholdLine2(): ThresholdLine {
    val label = textComponent(
        color = Color.Black,
        background = shapeComponent(Shapes.rectShape, Color(0xffe9e5af)),
        padding = thresholdLineLabelPadding,
        margins = thresholdLineLabelMargins,
        typeface = Typeface.MONOSPACE,
    )
    val line = shapeComponent(color = thresholdLineColor)
    return remember(label, line) {
        ThresholdLine(thresholdRange = thresholdLineValueRange, labelComponent = label, lineComponent = line)
    }
}
*/

// THRESHOLD 1
private val thresholdLineLabelMarginValue = 4.dp
private val thresholdLineLabelHorizontalPaddingValue = 8.dp
private val thresholdLineLabelVerticalPaddingValue = 2.dp
private val thresholdLineLabelPadding =
    dimensionsOf(thresholdLineLabelHorizontalPaddingValue, thresholdLineLabelVerticalPaddingValue)
private val thresholdLineLabelMargins = dimensionsOf(thresholdLineLabelMarginValue)
private const val THRESHOLD_LINE_VALUE = 20f
private val color1 = Color(0xffff5500)
private val color2 = Color(0xffd3d826)

// THRESHOLD 2
/*
private const val THRESHOLD_LINE_VALUE_RANGE_START = 0f
private const val THRESHOLD_LINE_VALUE_RANGE_END = 20f
private const val THRESHOLD_LINE_ALPHA = .36f
private val thresholdLineValueRange = THRESHOLD_LINE_VALUE_RANGE_START..THRESHOLD_LINE_VALUE_RANGE_END
private val thresholdLineLabelHorizontalPaddingValue = 8.dp
private val thresholdLineLabelVerticalPaddingValue = 2.dp
private val thresholdLineLabelMarginValue = 4.dp
private val thresholdLineLabelPadding =
    dimensionsOf(thresholdLineLabelHorizontalPaddingValue, thresholdLineLabelVerticalPaddingValue)
private val thresholdLineLabelMargins = dimensionsOf(thresholdLineLabelMarginValue)
private val thresholdLineColor = Color(0xffe9e5af).copy(THRESHOLD_LINE_ALPHA)
*/