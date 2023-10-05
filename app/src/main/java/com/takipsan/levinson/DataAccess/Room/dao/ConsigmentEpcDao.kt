package com.takipsan.levinson.DataAccess.Room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.takipsan.levinson.Entities.Room.cosignmentEpcs

@Dao
interface ConsigmentEpcDao {

    @Query("SELECT * FROM cosignmentEpcs WHERE epc = :epc")
    fun getRecordByEpc(epc: String): cosignmentEpcs?

    @Query("SELECT * FROM cosignmentEpcs WHERE counting_id = :id")
    fun getRecordBySevkiyatid(id: Long): List<cosignmentEpcs>?

    @Query("Update cosignmentEpcs set found = 1  WHERE epc = :epc")
    fun updateRecordByEpc(epc: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(consigment: cosignmentEpcs)


}