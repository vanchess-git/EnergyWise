package com.example.energywizeapp.util

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun calculateWeekStart(selectedYear: Int, selectedWeek: Int): String {
    val calendar = Calendar.getInstance()
    calendar.clear()
    calendar.set(Calendar.YEAR, selectedYear)
    calendar.set(Calendar.WEEK_OF_YEAR, selectedWeek)
    val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
    return dateFormat.format(calendar.time)
}

fun calculateWeekEnd(selectedYear: Int, selectedWeek: Int): String {
    val calendar = Calendar.getInstance()
    calendar.clear()
    calendar.set(Calendar.YEAR, selectedYear)
    calendar.set(Calendar.WEEK_OF_YEAR, selectedWeek)
    calendar.add(Calendar.DAY_OF_YEAR, 6) // End of the week
    val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
    return dateFormat.format(calendar.time)
}

fun calculateMonthStart(selectedYear: Int, selectedMonth: String): String {
    val month = when (selectedMonth.lowercase(Locale.ROOT)) {
        "january" -> Calendar.JANUARY
        "february" -> Calendar.FEBRUARY
        "march" -> Calendar.MARCH
        "april" -> Calendar.APRIL
        "may" -> Calendar.MAY
        "june" -> Calendar.JUNE
        "july" -> Calendar.JULY
        "august" -> Calendar.AUGUST
        "september" -> Calendar.SEPTEMBER
        "october" -> Calendar.OCTOBER
        "november" -> Calendar.NOVEMBER
        "december" -> Calendar.DECEMBER
        else -> Calendar.JANUARY // Default to January
    }

    val calendar = Calendar.getInstance()
    calendar.clear()
    calendar.set(Calendar.YEAR, selectedYear)
    calendar.set(Calendar.MONTH, month)
    val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
    return dateFormat.format(calendar.time)
}

fun calculateMonthEnd(selectedYear: Int, selectedMonth: String): String {
    val calendar = Calendar.getInstance()
    calendar.clear()
    calendar.set(Calendar.YEAR, selectedYear)
    val month = when (selectedMonth.lowercase(Locale.ROOT)) {
        "january" -> Calendar.JANUARY
        "february" -> Calendar.FEBRUARY
        "march" -> Calendar.MARCH
        "april" -> Calendar.APRIL
        "may" -> Calendar.MAY
        "june" -> Calendar.JUNE
        "july" -> Calendar.JULY
        "august" -> Calendar.AUGUST
        "september" -> Calendar.SEPTEMBER
        "october" -> Calendar.OCTOBER
        "november" -> Calendar.NOVEMBER
        "december" -> Calendar.DECEMBER
        else -> Calendar.JANUARY // Default to January
    }
    calendar.set(Calendar.MONTH, month)
    calendar.add(Calendar.MONTH, 1)
    calendar.add(Calendar.DAY_OF_MONTH, -1)
    val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
    return dateFormat.format(calendar.time)
}

fun calculateYearStart(selectedYear: Int): String {
    val calendar = Calendar.getInstance()
    calendar.clear()
    calendar.set(Calendar.YEAR, selectedYear)
    val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
    return dateFormat.format(calendar.time)
}

fun calculateYearEnd(selectedYear: Int): String {
    val calendar = Calendar.getInstance()
    calendar.clear()
    calendar.set(Calendar.YEAR, selectedYear)
    calendar.add(Calendar.YEAR, 1)
    calendar.add(Calendar.DAY_OF_YEAR, -1)
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