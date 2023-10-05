package com.takipsan.levinson.Entities.Room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cosignmentEpcs")
data class cosignmentEpcs(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val counting_id: Long = 0,
    val epc: String,
    val found: Int = 0,
    val created_user_id: Long = 0,
    val updated_user_id: Long = 0,
    val created_at: String,
    val isTransfferd:Int = 0
)