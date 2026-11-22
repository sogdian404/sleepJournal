package com.example.sleepjournal.DataClasses
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sleep_records")
data class SleepRecord (
    @PrimaryKey(autoGenerate = true)
    val id:Long=0,
    //Время когда человек лёг в кровать
    val bedTime: Long,
    //Время на засыпание в минутах
    val fallAsleepMinutes: Int,
    //Время пробуждения
    val wakeUpTime: Long,
    //Количество ночных пробуждений
    val awakeningsCount: Int,
    //Оценка качества сна
    val quality:Int,
    //Заметка до сна
    val preSleepNote:String = "",
    //Заметка после сна
    val postSleepNote: String = ""
    )
{
     //Общее время проведённое в кровати
    val timeInBedMinutes: Long
        get() = (wakeUpTime - bedTime) / 60000

     // Чистое время сна
    val actualSleepMinutes: Long
        get() = timeInBedMinutes - fallAsleepMinutes
}