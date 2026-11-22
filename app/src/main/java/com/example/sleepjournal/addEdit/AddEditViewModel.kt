package com.example.sleepjournal.addEdit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sleepjournal.DataClasses.AppDatabase
import com.example.sleepjournal.DataClasses.SleepRecord
import kotlinx.coroutines.launch

class AddEditViewModel(application: Application): AndroidViewModel(application) {
    private val dao = AppDatabase.getInstance(application).sleepRecordDao()

    suspend fun  getRecordById(id: Long): SleepRecord?
    {
        return dao.getRecordById(id)
    }
    fun saveRecord(record: SleepRecord,onSuccess:()-> Unit)
    {
        viewModelScope.launch {
            if (record.id == 0L) {
                dao.insert(record)
            }else
            {
                dao.update(record)
            }
            onSuccess()
        }
    }


}