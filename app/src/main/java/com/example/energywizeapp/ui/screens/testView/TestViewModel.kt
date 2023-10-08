package com.example.energywizeapp.ui.screens.testView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

/** this viewModel was made for creating the timeframe navigation UI elements and their logic.
 * for the timeframe navigators to work you need to copy everything from here and TestView.kt
 * to your view and viewModel.
 *
 * most important stuff to use for filtering are:
 *      val time
 *      selectedTimeTypeIndex
 *      val listOfTimeTypes
 *
 * technically we wouldn't need the listOfTimeTypes, they are just for giving the types a better name.
 *
 * time is in timestamp format, so when using it to filtering keep that in mind.*/
class TestViewModel: ViewModel() {
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
        val currentCalendar = Calendar.getInstance()
        currentCalendar.timeInMillis = currentTime
        currentCalendar.add(Calendar.DAY_OF_MONTH, 1)
        val updatedTime = currentCalendar.timeInMillis
        _time.value = updatedTime
    }
    private fun incrementWeek() {
        val currentTime = _time.value
        val currentCalendar = Calendar.getInstance()
        currentCalendar.timeInMillis = currentTime
        currentCalendar.add(Calendar.WEEK_OF_MONTH, 1)
        val updatedTime = currentCalendar.timeInMillis
        _time.value = updatedTime
    }
    private fun incrementMonth() {
        val currentTime = _time.value
        val currentCalendar = Calendar.getInstance()
        currentCalendar.timeInMillis = currentTime
        currentCalendar.add(Calendar.MONTH, 1)
        val updatedTime = currentCalendar.timeInMillis
        _time.value = updatedTime
    }
    private fun incrementYear() {
        val currentTime = _time.value
        val currentCalendar = Calendar.getInstance()
        currentCalendar.timeInMillis = currentTime
        currentCalendar.add(Calendar.YEAR, 1)
        val updatedTime = currentCalendar.timeInMillis
        _time.value = updatedTime
    }
    private fun decrementDay() {
        val currentTime = _time.value
        val currentCalendar = Calendar.getInstance()
        currentCalendar.timeInMillis = currentTime
        currentCalendar.add(Calendar.DAY_OF_MONTH, -1)
        val updatedTime = currentCalendar.timeInMillis
        _time.value = updatedTime
    }
    private fun decrementWeek() {
        val currentTime = _time.value
        val currentCalendar = Calendar.getInstance()
        currentCalendar.timeInMillis = currentTime
        currentCalendar.add(Calendar.WEEK_OF_MONTH, -1)
        val updatedTime = currentCalendar.timeInMillis
        _time.value = updatedTime
    }
    private fun decrementMonth() {
        val currentTime = _time.value
        val currentCalendar = Calendar.getInstance()
        currentCalendar.timeInMillis = currentTime
        currentCalendar.add(Calendar.MONTH, -1)
        val updatedTime = currentCalendar.timeInMillis
        _time.value = updatedTime
    }
    private fun decrementYear() {
        val currentTime = _time.value
        val currentCalendar = Calendar.getInstance()
        currentCalendar.timeInMillis = currentTime
        currentCalendar.add(Calendar.YEAR, -1)
        val updatedTime = currentCalendar.timeInMillis
        _time.value = updatedTime
    }
}

private data class CalendarState(val calendar: Calendar)
