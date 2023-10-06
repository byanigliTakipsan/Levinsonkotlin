package com.takipsan.levinson.DataAccess.Room.dao

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.takipsan.levinson.Entities.Room.cosignmentEpcs



@Dao
interface ConsigmentEpcDao {

    @Query("SELECT * FROM cosignmentEpcs WHERE epc = :epc and counting_id = :sid")
    fun getRecordByEpcWithID(epc: String,sid:Int): cosignmentEpcs?

    @Query("SELECT * FROM cosignmentEpcs WHERE counting_id = :id")
    fun getRecordBySevkiyatid(id: Long): List<cosignmentEpcs>?

    @Query("Update cosignmentEpcs set found = 1  WHERE epc = :epc")
    fun updateRecordByEpc(epc: String): Int

    @Query("select * from cosignmentEpcs   WHERE  counting_id =:sid and isTransfferd = 0")
    fun getUnrecoded(sid: Long): List<cosignmentEpcs>

    @Query("update cosignmentEpcs  set isTransfferd = 1  WHERE  counting_id =:sid and isTransfferd = 0")
    fun setTransfer(sid: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(consigment: cosignmentEpcs)


}