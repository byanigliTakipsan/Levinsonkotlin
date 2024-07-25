package com.takipsan.levinson.DataAccess.Room.dao

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.takipsan.levinson.Entities.Room.CountingKor
import com.takipsan.levinson.Entities.Room.cosignmentEpcs

@Dao
interface CountingBlinkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(kor: CountingKor)
    @Query("select * from CountingKor where consignmentID =:consignmentID and epcs=:epc limit 1")
    fun ExistsElement(consignmentID:Long,epc: String):CountingKor?
    @Query("update CountingKor set isSeen=1, isTransffered =1 where consignmentID =:consignmentID and epcs=:epc ")
    fun updateSeenFromServer(consignmentID:Long,epc: String)
    @Query("select * from CountingKor where consignmentID =:consignmentID")
    fun getAll(consignmentID:Long):List<CountingKor>
    @Query("update CountingKor set isSeen=1, isTransffered =1 where consignmentID =:consignmentID and epcs=:epc ")
    fun updateSeen(consignmentID:Long,epc: String)
}