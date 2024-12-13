package com.example.nextlab.Models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

@Entity (tableName = "matches.db")
//модель класса, куда мы будем записывать данные
data class Match(
    @PrimaryKey(autoGenerate = false)
    val matchId: String,
    @ColumnInfo(name = "date")
    val date: String,
    @ColumnInfo(name = "winner")
    val winner: String,
    @ColumnInfo(name = "durationOfMatch")
    val durationOfMatch: String,
    @ColumnInfo(name = "avarageMMR")
    val averageMMR: String)