package com.takipsan.levinson.Entities.Room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "consignments")
data class Consignment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: Int = 0,
    val name: String,
    val mode: Int = 0,
    val quantity: Int = 0,
    val status: Int = 0,

)