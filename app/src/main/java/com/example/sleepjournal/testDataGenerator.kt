package com.example.sleepjournal

import com.example.sleepjournal.DataClasses.SleepRecord
import kotlin.random.Random

object TestDataGenerator {

    fun generateRecords(daysBack: Int = 45): List<SleepRecord> {
        val records = mutableListOf<SleepRecord>()
        val now = System.currentTimeMillis()
        val dayInMillis = 24 * 60 * 60 * 1000L

        for (i in 1..daysBack) {
            // Дата: i дней назад
            val dayStart = now - (i * dayInMillis)

            // Время засыпания: от 22:00 до 01:00 (случайно)
            val bedTime = dayStart - Random.nextLong(1, 4) * 60 * 60 * 1000L

            // Время на засыпание: 5-60 минут
            val fallAsleep = Random.nextInt(5, 61)

            // Длительность в кровати: 5-9 часов
            val timeInBedMinutes = Random.nextLong(5 * 60, 9 * 60)
            val wakeUpTime = bedTime + timeInBedMinutes * 60 * 1000L

            // Оценка: случайная от 1 до 5, но с перекосом в сторону 3-4
            val quality = when (Random.nextInt(1, 11)) {
                in 1..2 -> 1
                in 3..4 -> 2
                in 5..7 -> 3
                in 8..9 -> 4
                else -> 5
            }

            // Пробуждения: 0-3
            val awakenings = Random.nextInt(0, 4)

            // Заметки: добавляем случайно (30% случаев)
            val preNote = if (Random.nextBoolean() && Random.nextInt(100) < 30)
                "Плохо засыпал, пил чай перед сном" else ""
            val postNote = if (Random.nextBoolean() && Random.nextInt(100) < 30)
                "Чувствовал себя бодро до обеда" else ""

            records.add(
                SleepRecord(
                    bedTime = bedTime,
                    fallAsleepMinutes = fallAsleep,
                    wakeUpTime = wakeUpTime,
                    awakeningsCount = awakenings,
                    quality = quality,
                    preSleepNote = preNote,
                    postSleepNote = postNote
                )
            )
        }
        return records
    }
}