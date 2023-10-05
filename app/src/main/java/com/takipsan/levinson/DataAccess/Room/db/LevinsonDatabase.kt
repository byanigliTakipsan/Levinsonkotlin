package com.takipsan.levinson.DataAccess.Room.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.takipsan.levinson.DataAccess.Room.dao.ConsigmentDao
import com.takipsan.levinson.DataAccess.Room.dao.ConsigmentEpcDao
import com.takipsan.levinson.Entities.Room.Consignment
import com.takipsan.levinson.Entities.Room.cosignmentEpcs

@Database(entities = [Consignment::class, cosignmentEpcs::class], version = 5, exportSchema = false)
abstract  class LevinsonDatabase:RoomDatabase() {
    abstract fun ConsigmentDao():ConsigmentDao
    abstract fun ConsigmentEpcDao():ConsigmentEpcDao

    companion object {
        var INSTANCE: LevinsonDatabase? = null
        fun databaseConnection(context: Context): LevinsonDatabase? {
            if (INSTANCE == null) {
                synchronized(LevinsonDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        LevinsonDatabase::class.java,
                        "levinson.db"
                    ).createFromAsset("levinson.db")

                        .allowMainThreadQueries().build()
                }
            }
            return INSTANCE
        }
    }
}