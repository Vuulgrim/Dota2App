package com.example.nextlab.DataBases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.nextlab.Daos.HeroDao
import com.example.nextlab.Models.Hero

@Database (entities = [Hero::class], version = 1)
abstract class HeroDB: RoomDatabase() {
    abstract fun getDao(): HeroDao

    companion object {
        fun getDB(context: Context): HeroDB {
            return Room.databaseBuilder(
                context.applicationContext,
                HeroDB::class.java,
                "Hero.db"
            ).build()
        }
    }
}