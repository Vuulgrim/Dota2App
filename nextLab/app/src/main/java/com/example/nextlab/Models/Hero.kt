package com.example.nextlab.Models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Hero.db")
data class Hero (
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    @ColumnInfo(name = "localized_name")
    val localName: String
)