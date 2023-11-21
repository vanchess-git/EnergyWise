package com.example.energywizeapp.ui.screens.home

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.energywizeapp.data.api.EntsoResponse
import com.example.energywizeapp.data.api.PriceRepository
import com.example.energywizeapp.util.calculateMonthEnd
import com.example.energywizeapp.util.calculateMonthStart
import com.example.energywizeapp.util.calculateWeekEnd
import com.example.energywizeapp.util.calculateWeekStart
import com.example.energywizeapp.util.calculateYearEnd
import com.example.energywizeapp.util.calculateYearStart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeViewModel: ViewModel() {
    private val repository = PriceRepository()

    val entsoResponseState = mutableStateOf<EntsoResponse?>(null)
    fun fetchPrices(
        timeFrame: String?,
        selectedDate: Long?,
        selectedWeek: Long?,
        selectedMonth: Long?,
        selectedYear: Long?
    ) {
        viewModelScope.launch {
            try {
                setLoadingState(true)
                val (periodStart, periodEnd) = when (timeFrame) {
                    "day" -> {
                        run {
                            val formattedDate = SimpleDateFormat("yyyyMMdd", Locale.US).format(selectedDate)
                            Pair(formattedDate + "0000", formattedDate + "2300")
                        }
                    }
                    "week" -> {
                        if (selectedWeek != null) {
                            val weekStart = calculateWeekStart(selectedWeek)
                            val weekEnd = calculateWeekEnd(selectedWeek)
                            Pair(weekStart + "0000", weekEnd + "2300")
                        } else {
                            Pair("", "")
                        }
                    }
                    "month" -> {
                        if (selectedMonth != null) {
                            val monthStart = calculateMonthStart(selectedMonth)
                            val monthEnd = calculateMonthEnd(selectedMonth)
                            Pair(monthStart + "0000", monthEnd + "2300")
                        } else {
                            Pair("", "")
                        }
                    }
                    "year" -> {
                        if (selectedYear != null) {
                            val yearStart = calculateYearStart(selectedYear)
                            val yearEnd = calculateYearEnd(selectedYear)
                            Pair(yearStart + "0000", yearEnd + "2300")
                        } else {
                            Pair("", "")
                        }
                    }
                    else -> {
                        Pair("", "")
                    }
                }

                val publicationMarketDocument = repository.getPrices(
                    securityToken = "4ede6fbc-1823-49bb-9be0-0801df9df8ef",
                    documentType = "A44",
                    inDomain = "10YFI-1--------U",
                    outDomain = "10YFI-1--------U",
                    periodStart = periodStart,
                    periodEnd = periodEnd
                )
                val entsoResponse = EntsoResponse(publicationMarketDocument)

                entsoResponseState.value = entsoResponse

                val maxLogSize = 1000
                val veryLongString = entsoResponse.toString()
                val tag = "VicoChart Response"

                for (i in 0..veryLongString.length / maxLogSize) {
                    val start = i * maxLogSize
                    var end = (i + 1) * maxLogSize
                    end = if (end > veryLongString.length) veryLongString.length else end
                    Log.v(tag, veryLongString.substring(start, end))
                }

            } catch (e: Exception) {
                Log.e("error", e.message.toString())
            } finally {
                setLoadingState(false)
            }
        }
    }

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> = _loadingState.asStateFlow()

    fun setLoadingState(loading: Boolean) {
        _loadingState.value = loading
    }

    // index for the current time type, used for highlighting the correct button in the UI
    private val _selectedTimeTypeIndex = MutableStateFlow(0)
    val selectedTimeTypeIndex: StateFlow<Int> = _selectedTimeTypeIndex.asStateFlow()

    // time frame types for the time frame selector
    private val _listOfTimeTypes = MutableStateFlow(
        listOf("day", "week", "month", "year",)
    )
    val listOfTimeTypes: StateFlow<List<String>> = _listOfTimeTypes.asStateFlow()

    // calendar is used for time formatting and getting the time
    private val _calendar = MutableStateFlow(CalendarState(Calendar.getInstance()))
    val calendar: StateFlow<Calendar>
        get() = _calendar.map { it.calendar }.stateIn(viewModelScope,
            SharingStarted.WhileSubscribed(),
            Calendar.getInstance()
        )
    // time
    private val _time = MutableStateFlow(calendar.value.timeInMillis)
    val time: StateFlow<Long> = _time.asStateFlow()

    fun setTimeTypeIndex(index: Int) {
        _selectedTimeTypeIndex.value = index
    }
    fun timeIncrement() {
        when (_listOfTimeTypes.value[_selectedTimeTypeIndex.value]) {
            "day" -> { incrementDay() }
            "week" -> { incrementWeek() }
            "month" -> { incrementMonth() }
            "year" -> { incrementYear() }
            else -> { incrementDay() }
        }
    }
    fun timeDecrement() {
        when (_listOfTimeTypes.value[_selectedTimeTypeIndex.value]) {
            "day" -> { decrementDay() }
            "week" -> { decrementWeek() }
            "month" -> { decrementMonth() }
            "year" -> { decrementYear() }
            else -> { decrementDay() }
        }
    }
    private fun incrementDay() {
        val currentTime = _time.value
        val comparisonCalendarInstance = Calendar.getInstance()
        val currentCalendar = Calendar.getInstance()
        currentCalendar.timeInMillis = currentTime
        currentCalendar.add(Calendar.DAY_OF_MONTH, 1)

        var timeIsPast2PM = false
        if(comparisonCalendarInstance[Calendar.HOUR_OF_DAY] > 14) {
            timeIsPast2PM = true
        }

        if(comparisonCalendarInstance.timeInMillis < currentCalendar.timeInMillis) {
            if (comparisonCalendarInstance[Calendar.DAY_OF_MONTH] == currentCalendar[Calendar.DAY_OF_MONTH] - 1) {
                if(timeIsPast2PM) {
                    val updatedTime = currentCalendar.timeInMillis
                    _time.value = updatedTime
                    Log.d("TIMEFRAME SELECTOR", _time.value.toString())
                    return
                }
            }
            Log.d("TIMEFRAME SELECTOR", "Cannot increment, data not available")
            return
        }

        val updatedTime = currentCalendar.timeInMillis
        _time.value = updatedTime
        Log.d("TIMEFRAME SELECTOR", _time.value.toString())
    }
    private fun incrementWeek() {
        val currentTime = _time.value
        val currentCalendar = Calendar.getInstance()
        val comparisonCalendarInstance = Calendar.getInstance()
        currentCalendar.timeInMillis = currentTime
        currentCalendar.add(Calendar.WEEK_OF_MONTH, 1)

        if(comparisonCalendarInstance[Calendar.WEEK_OF_YEAR] < currentCalendar[Calendar.WEEK_OF_YEAR]) {
            Log.d("TIMEFRAME SELECTOR", "Cannot increment, data not available")
            return
        }

        val updatedTime = currentCalendar.timeInMillis
        _time.value = updatedTime
        Log.d("TIMEFRAME SELECTOR", _time.value.toString())
    }
    private fun incrementMonth() {
        val currentTime = _time.value
        val currentCalendar = Calendar.getInstance()
        val comparisonCalendarInstance = Calendar.getInstance()
        currentCalendar.timeInMillis = currentTime
        currentCalendar.add(Calendar.MONTH, 1)

        if(comparisonCalendarInstance[Calendar.MONTH] < currentCalendar[Calendar.MONTH]) {
            Log.d("TIMEFRAME SELECTOR", "Cannot increment, data not available")
            return
        }

        val updatedTime = currentCalendar.timeInMillis
        _time.value = updatedTime
        Log.d("TIMEFRAME SELECTOR", _time.value.toString())
    }
    private fun incrementYear() {
        val currentTime = _time.value
        val currentCalendar = Calendar.getInstance()
        val comparisonCalendarInstance = Calendar.getInstance()
        currentCalendar.timeInMillis = currentTime
        currentCalendar.add(Calendar.YEAR, 1)

        if(comparisonCalendarInstance[Calendar.YEAR] < currentCalendar[Calendar.YEAR]) {
            Log.d("TIMEFRAME SELECTOR", "Cannot increment, data not available")
            return
        }

        val updatedTime = currentCalendar.timeInMillis
        _time.value = updatedTime
        Log.d("TIMEFRAME SELECTOR", _time.value.toString())
    }
    private fun decrementDay() {
        val currentTime = _time.value
        val currentCalendar = Calendar.getInstance()
        currentCalendar.timeInMillis = currentTime
        currentCalendar.add(Calendar.DAY_OF_MONTH, -1)
        val updatedTime = currentCalendar.timeInMillis
        _time.value = updatedTime
        Log.d("TIMEFRAME SELECTOR", _time.value.toString())
    }
    private fun decrementWeek() {
        val currentTime = _time.value
        val currentCalendar = Calendar.getInstance()
        currentCalendar.timeInMillis = currentTime
        currentCalendar.add(Calendar.WEEK_OF_MONTH, -1)
        val updatedTime = currentCalendar.timeInMillis
        _time.value = updatedTime
        Log.d("TIMEFRAME SELECTOR", _time.value.toString())
    }
    private fun decrementMonth() {
        val currentTime = _time.value
        val currentCalendar = Calendar.getInstance()
        currentCalendar.timeInMillis = currentTime
        currentCalendar.add(Calendar.MONTH, -1)
        val updatedTime = currentCalendar.timeInMillis
        _time.value = updatedTime
        Log.d("TIMEFRAME SELECTOR", _time.value.toString())
    }
    private fun decrementYear() {
        val currentTime = _time.value
        val currentCalendar = Calendar.getInstance()
        currentCalendar.timeInMillis = currentTime
        currentCalendar.add(Calendar.YEAR, -1)
        val updatedTime = currentCalendar.timeInMillis
        _time.value = updatedTime
        Log.d("TIMEFRAME SELECTOR", _time.value.toString())
    }
}

private data class CalendarState(val calendar: Calendar)