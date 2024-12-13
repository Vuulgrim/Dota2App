package com.example.nextlab.DataBases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.nextlab.Daos.MatchDao
import com.example.nextlab.Models.Match

@Database (entities = [Match::class], version = 1)
abstract class MainDB : RoomDatabase() {
    abstract fun getDao(): MatchDao

    companion object {
        fun getDB(context: Context): MainDB {
            return Room.databaseBuilder(
                context.applicationContext,
                MainDB::class.java,
                "matches.db"
            ).build()
        }
    }
}