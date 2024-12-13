package com.example.nextlab.Daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nextlab.Models.Hero
import com.example.nextlab.Models.Match
import kotlinx.coroutines.flow.Flow

@Dao
interface HeroDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun InsertHero(item: Hero)
    @Query("Delete from 'Hero.db'")
    fun deleteAll()
    @Query("SELECT * FROM 'Hero.db'")
    fun selectAll(): Flow<List<Hero>>
}