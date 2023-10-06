package com.takipsan.levinson.Entities.Room

import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CountingKor")
data class CountingKor(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userID: Long,
    val consignmentID: Long,
    val epcs:String,
    val isTransffered:Int
)