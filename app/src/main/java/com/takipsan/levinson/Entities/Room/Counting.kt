package com.takipsan.levinson.Entities.Room

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp

@Entity(tableName = "countings")
data class Counting(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val mode: Int = 0,
    val status: Int = 1,
    val created_user_id: Long = 0,
    val updated_user_id: Long = 0,
    val created_at: Timestamp? = null,
    val updated_at: Timestamp? = null,
    val deleted_at: Timestamp? = null
)