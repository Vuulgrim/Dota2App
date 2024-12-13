package com.example.nextlab.DataBases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.nextlab.Daos.MatchDetailsDao
import com.example.nextlab.Models.MatchDetails

@Database(entities = [MatchDetails::class], version = 1)
abstract class matchdb : RoomDatabase() {
    abstract fun getDao(): MatchDetailsDao

    companion object {
        fun getDB(context: Context): matchdb {
            return Room.databaseBuilder(
                context,
                matchdb::class.java,
                "match.db"
            ).build()
        }
    }
}