package com.example.projet_android.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.projet_android.Flux
import com.example.projet_android.Info

@Database(entities = [ Flux::class, Info::class ] , version = 2)
abstract class MyDatabase : RoomDatabase() {
    abstract fun myDao(): Dao

    companion object {
        @Volatile
        private var INSTANCE: MyDatabase? = null

        fun getDatabase(
            context: Context
        ): MyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyDatabase::class.java,
                    "DBFluxRSS"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}