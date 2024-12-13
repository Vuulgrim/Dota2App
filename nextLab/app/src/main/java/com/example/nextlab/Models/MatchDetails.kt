package com.example.nextlab.Models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//модель класса, куда мы будем записывать данные
@Entity(tableName = "match.db")
data class MatchDetails(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "match_id")
    val matchID: String,
    @ColumnInfo(name = "Player_name")
    val player_name: String,
    @ColumnInfo(name = "hero")
    val hero: String,
    @ColumnInfo(name = "networth")
    val networth: String
)