package com.example.sleepjournal.DataClasses
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SleepRecordDAO {
    //создать
    @Insert
    suspend fun  insert(record: SleepRecord)
    //ДЛЯ ТЕСТОВ, НЕ НУЖНО ДЛЯ РАБОТЫ ПРОГРАММЫ
    @Insert
    suspend fun insertAll(records: List<SleepRecord>)
    //изменить
    @Update
    suspend fun update(record: SleepRecord)
    //удалить
    @Delete
    suspend fun  delete(record: SleepRecord)
    //получить все
    @Query("SELECT * FROM sleep_records ORDER BY bedTime DESC")
    fun getAllRecords(): Flow<List<SleepRecord>>
    //получить одного по id
    @Query("SELECT * FROM sleep_records WHERE id = :id")
    suspend fun getRecordById(id: Long): SleepRecord?

    @Query("SELECT AVG(quality) FROM sleep_records WHERE bedTime>= :startDate")
    suspend fun getAvgQuality(startDate:Long): Double?

    @Query("SELECT AVG((wakeUpTime - bedTime)/60000.0 - fallAsleepMinutes) FROM sleep_records WHERE bedTime>= :startDate")
    suspend fun getAvgDuration(startDate:Long): Double?

}