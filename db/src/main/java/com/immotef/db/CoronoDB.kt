package com.immotef.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.immotef.db.infected.Infected
import com.immotef.db.infected.InfectedDAO
import com.immotef.db.location.Location
import com.immotef.db.location.LocationDao
import com.immotef.db.meet.Meet
import com.immotef.db.meet.MeetDAO

/**
 *
 */


private const val DATABASE_NAME = "corona-db"

@Database(entities = [Meet::class, Location::class, Infected::class], version = 4, exportSchema = false)
internal abstract class CoronaVirusDatabase : RoomDatabase(), ClearDB {

    abstract fun getMeetDao(): MeetDAO

    abstract fun getLocationDao(): LocationDao

    abstract fun getInfectedDao(): InfectedDAO

    companion object {
        fun buildDatabase(context: Context): CoronaVirusDatabase {
            return Room.databaseBuilder(context, CoronaVirusDatabase::class.java, DATABASE_NAME).fallbackToDestructiveMigration().build()
        }

    }

    override fun dropAll() {
        this.clearAllTables()
    }
}

interface ClearDB {
    fun dropAll()
}