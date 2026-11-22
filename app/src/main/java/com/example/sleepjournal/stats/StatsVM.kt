package com.example.sleepjournal.stats

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sleepjournal.DataClasses.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class Stats(
    val avgDuration: Double = 0.0,
    val avgQuality: Double = 0.0,
    val isLoading: Boolean = false
    )
class StatsVM(application: Application): AndroidViewModel(application) {
    private val dao = AppDatabase.getInstance(application).sleepRecordDao()

    private val _statsVal = MutableStateFlow(Stats())
    val statsValue: StateFlow<Stats> = _statsVal

    fun loadStats(days:Int)
    {
        viewModelScope.launch {
            _statsVal.value = _statsVal.value.copy(isLoading = true)

            val startDate = System.currentTimeMillis()-(days*24*60*60*1000L)
            val avgQuality = dao.getAvgQuality(startDate)?:0.0
            val avgDuration = dao.getAvgDuration(startDate)?:0.0

            _statsVal.value = Stats(
                avgDuration = avgDuration,
                avgQuality = avgQuality,
                isLoading = false
            )
        }
    }



}