package com.example.energywizeapp.ui.composables

import android.graphics.Typeface
import android.util.Log
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.energywizeapp.data.api.EntsoResponse
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
import java.util.Calendar
import java.util.Locale

@Composable
fun VicoChart(priceData: EntsoResponse) {

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

        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        var currentHourPrice: Double? = null

        for (timeSeries in priceData.publicationMarketDocument.timeSeriesList ?: emptyList()) {


            val timeInterval = timeSeries.period?.timeInterval
            val startDateTime = timeInterval?.end
            Log.d("VicoChart timeInterval", timeInterval.toString())
            for (point in timeSeries.period?.points ?: emptyList()) {

                val position = point.position
                val priceAmountBeforeVAT = (point.priceAmount * 0.1)
                val priceAmountBeforeFormatting = priceAmountBeforeVAT * (1 + 0.24)
                val priceAmount = BigDecimal(priceAmountBeforeFormatting).setScale(2, RoundingMode.HALF_UP).toDouble()
                Log.d("VicoChart price and position", "${priceAmount}, pos $xPos $position")

                if(isDateToday(startDateTime)) {
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

        if (currentHourPrice != null) {
            currentHourPriceTextState.value = "$currentHourPrice snt/kWh"
        } 

        Log.d("VicoChart DataPoints", dataPoints.toString())
        datasetForModel.add(dataPoints)
        modelProducer.setEntries(datasetForModel)

    }

    Column {
        ElevatedCard(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 15.dp)
        ) {

            Text(
                text = "Sähkön hinta nyt \n $currentHourPriceCardText",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                textAlign = TextAlign.Center
            )
        }
        ElevatedCard(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
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
                            title = "Hinta",
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
                            title = "Aika",
                            tickLength = 0.dp,
                            guideline = null,
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

}


// Old version with https://api.porssisahko.net/
/*
@Composable
fun VicoChartOld() {
    var priceDataList: List<PriceData>

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

    val currentHourPriceTextState = remember { mutableStateOf("Hetki...") }
    val currentHourPriceCardText by currentHourPriceTextState

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

        val call = RetrofitClient.apiService.getLatestPrices()
        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    priceDataList = response.body()?.prices!!
                    val filteredPriceDataList = filterPricesForToday(priceDataList)
                    Log.d("data", filteredPriceDataList.toString())

                    val sortedPriceDataList = filteredPriceDataList.sortedBy { priceData ->
                        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                            .parse(priceData.startDate)
                    }

                    // Initialize variables to store the current hour's price
                    var currentHourPrice: Double? = null
                    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

                    for (priceData in sortedPriceDataList) {
                        val startDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                            .parse(priceData.startDate)
                        startDate?.let {
                            val hourOfPrice = Calendar.getInstance().apply { time = it }.get(Calendar.HOUR_OF_DAY)
                            val price = priceData.price

                            dataPoints.add(FloatEntry(x = xPos, y = price.toFloat()))
                            xPos += 1f

                            // Check if the current data point corresponds to the current hour
                            if (hourOfPrice == currentHour) {
                                currentHourPrice = price
                            }
                        }
                    }
                    Log.d("datapoints", dataPoints.toString())
                    datasetForModel.add(dataPoints)

                    modelProducer.setEntries(datasetForModel)

                    // Check if the current hour's price is available, and update the card
                    if (currentHourPrice != null) {
                        // Update the text in the card with the current hour's price
                        currentHourPriceTextState.value = "$currentHourPrice snt/kWh"
                    } else {
                        // Handle the case where the current hour's price is not available
                        currentHourPriceTextState.value = "Current Hour Price not available"
                    }
                } else {
                    // TODO: Handle the case where the response is not successful
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                // TODO: Handle the case where the API call fails
            }
        })





    }

    Column {
        ElevatedCard(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 15.dp)
        ) {

            Text(
                text = "Sähkön hinta nyt \n $currentHourPriceCardText",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                textAlign = TextAlign.Center
            )
        }
        ElevatedCard(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        ) {
            Text(text = "Sähkön hinta tänään", modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp), textAlign = TextAlign.Center)
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

}

fun filterPricesForToday(priceDataList: List<PriceData>): List<PriceData> {
    val currentDate = Date()
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val currentDay = sdf.format(currentDate)

    return priceDataList.filter { priceData ->
        val startDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            .parse(priceData.startDate)
        startDate?.let {
            val dayOfPrice = sdf.format(startDate)
            dayOfPrice == currentDay
        } ?: false
    }
}

fun filterPricesForCurrentHour(priceDataList: List<PriceData>): List<PriceData> {
    val currentDate = Date()
    val sdf = SimpleDateFormat("yyyy-MM-dd HH", Locale.getDefault())
    val currentHour = sdf.format(currentDate)

    return priceDataList.filter { priceData ->
        val startDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            .parse(priceData.startDate)
        startDate?.let {
            val hourOfPrice = sdf.format(startDate)
            hourOfPrice == currentHour
        } ?: false
    }
}

*/