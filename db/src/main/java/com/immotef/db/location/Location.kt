package com.immotef.db.location

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.immotef.db.locations_table_name

/**
 *
 */

@Entity(
    tableName = locations_table_name, indices = [Index(
        value = ["timeStamp"],
        unique = true
    )]
)
data class Location(
    val latitude: Double,
    val longitude: Double,

    val timeStamp: Long,
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0) {


}