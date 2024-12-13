package com.example.nextlab.Daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nextlab.Models.MatchDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDetailsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertDetails(item: MatchDetails)
    @Query("SELECT * FROM `match.db` WHERE id = (SELECT MAX(id) FROM `match.db`)")
    fun getMaxId(): Flow<List<MatchDetails>>
    @Query("Delete from `match.db`")
    fun deleteAll()
    @Query("SELECT * FROM `match.db` WHERE match_id = (:matchId)")
    fun selectById(matchId: String): Flow<List<MatchDetails>>
    @Query("SELECT * FROM `match.db`")
    fun selectAll(): Flow<List<MatchDetails>>
}