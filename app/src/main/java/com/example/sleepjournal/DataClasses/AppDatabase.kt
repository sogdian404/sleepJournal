package com.example.sleepjournal.DataClasses
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SleepRecord::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun sleepRecordDao(): SleepRecordDAO

    companion object{
        @Volatile
        private  var INSTANCE: AppDatabase? = null
        // Метод для получения экземпляра базы данных
        fun getInstance(context: Context): AppDatabase
        {
            //создаем еслт нет, иначе саму базу возвращаем
            return INSTANCE?:synchronized(this)
            {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sleep_journal_database"
                ).fallbackToDestructiveMigration() //УДАЛИТЬ ПОСЛЕ ПОЛНОЙ РЕАЛИЗАЦИИ, пересоздание бд при изменении сущности
                    .build()

                INSTANCE = instance
                instance
            }

        }

    }



}