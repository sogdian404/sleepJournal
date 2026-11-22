package com.example.sleepjournal.home
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sleepjournal.DataClasses.AppDatabase
import com.example.sleepjournal.DataClasses.SleepRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class HomeViewModel(application: Application): AndroidViewModel(application) {

    //экземпляры бд т дао
    private val db = AppDatabase.getInstance(application)
    private  val dao = db.sleepRecordDao()

    val sleepRecords: Flow<List<SleepRecord>> = dao.getAllRecords()

    fun deleteRecord(record: SleepRecord)
    {
        viewModelScope.launch {
            dao.delete(record)
        }
    }

}