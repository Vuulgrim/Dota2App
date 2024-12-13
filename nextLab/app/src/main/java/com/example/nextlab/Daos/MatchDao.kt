package com.example.nextlab.Daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nextlab.Models.Match
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMatch(item: Match)
    @Query("SELECT * FROM `matches.db`")
    fun getAllMatches(): Flow<List<Match>>
    @Query("DELETE FROM `matches.db`")
    fun deleteAll()
}