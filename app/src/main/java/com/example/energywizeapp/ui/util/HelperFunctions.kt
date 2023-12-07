package com.example.energywizeapp.ui.util

import android.util.Log
import com.example.energywizeapp.data.api.EntsoResponse
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

fun calculateWeekStart(selectedWeek: Long): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = selectedWeek
    calendar.firstDayOfWeek = Calendar.MONDAY
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
    return dateFormat.format(calendar.time)
}

fun calculateWeekEnd(selectedWeek: Long): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = selectedWeek
    calendar.firstDayOfWeek = Calendar.MONDAY
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
    val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
    return dateFormat.format(calendar.time)
}

fun calculateMonthStart(selectedMonth: Long): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = selectedMonth
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
    return dateFormat.format(calendar.time)
}

fun calculateMonthEnd(selectedMonth: Long): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = selectedMonth
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
    val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
    return dateFormat.format(calendar.time)
}

fun calculateYearStart(selectedYear: Long): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = selectedYear
    calendar.set(Calendar.DAY_OF_YEAR, 1)
    val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
    return dateFormat.format(calendar.time)
}

fun calculateYearEnd(selectedYear: Long): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = selectedYear
    calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR))
    val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
    return dateFormat.format(calendar.time)
}
fun isDateToday(dateTimeString: String?): Boolean {
    dateTimeString ?: return false

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val date = Calendar.getInstance().time
    val currentDate = dateFormat.format(date)
    val dataDate = dateFormat.parse(dateTimeString)

    val dataDateString = dateFormat.format(dataDate)
    Log.d("HelperFunctions", "$currentDate $dateTimeString")

    return currentDate == dataDateString
}

// Function to calculate the time span covered by the data in days
fun calculateTimeSpanInDays(priceData: EntsoResponse): Long {
    val firstTimestamp = priceData.publicationMarketDocument.timeSeriesList?.firstOrNull()?.period?.timeInterval?.start
    val lastTimestamp = priceData.publicationMarketDocument.timeSeriesList?.lastOrNull()?.period?.timeInterval?.end
    Log.d("calculateTimeSpanInDays", "$firstTimestamp, $lastTimestamp")

    if (firstTimestamp != null && lastTimestamp != null) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'", Locale.US)
        try {
            val firstDate = dateFormat.parse(firstTimestamp)
            val lastDate = dateFormat.parse(lastTimestamp)

            if (firstDate != null && lastDate != null) {
                val differenceInMillis = lastDate.time - firstDate.time
                Log.d("calculateTimeSpanInDays", "${TimeUnit.MILLISECONDS.toDays(differenceInMillis)}")
                return TimeUnit.MILLISECONDS.toDays(differenceInMillis)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    return 0
}

fun getPositionTimeFrame(position: Int): String {
    require(position in 0..24) { "Position must be between 0 and 24 (inclusive)" }

    val endHour = (position + 1) % 24

    return String.format("%02d:00 - %02d:00", position, endHour)
}
