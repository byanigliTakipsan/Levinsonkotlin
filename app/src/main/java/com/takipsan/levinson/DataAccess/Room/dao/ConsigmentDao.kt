package com.takipsan.levinson.DataAccess.Room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.takipsan.levinson.Entities.Room.Consignment

@Dao
interface ConsigmentDao {



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(consigment: Consignment)

    @Update
    fun updateLoginInfo(consigment: Consignment):Int
}