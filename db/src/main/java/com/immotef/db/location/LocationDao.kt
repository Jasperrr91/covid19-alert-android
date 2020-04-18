package com.immotef.db.location

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.immotef.db.locations_table_name
import com.immotef.db.meet_table_name

/**
 *
 */
@Dao
interface LocationDao {

    @Query("SELECT * FROM $locations_table_name")
    suspend fun getAllLocations(): List<Location>

    @Query("SELECT * FROM $locations_table_name WHERE timeStamp BETWEEN :startTime and :endTime ORDER BY timeStamp DESC LIMIT 1")
    suspend fun provideLastKnownLocationIn(startTime:Long,endTime:Long):Location?

    @Insert(onConflict = REPLACE)
    suspend fun insertLocation(location: Location)

}