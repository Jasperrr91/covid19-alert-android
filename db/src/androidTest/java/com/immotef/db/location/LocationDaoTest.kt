package com.immotef.db.location

import android.content.Context
import androidx.room.Room
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.immotef.db.CoronaVirusDatabase
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 *
 */

@RunWith(AndroidJUnit4ClassRunner::class)
class LocationDaoTest {

    private lateinit var dao: LocationDao
    private lateinit var db: CoronaVirusDatabase

    @Before
    fun createDb() {
        val context: Context = InstrumentationRegistry.getInstrumentation().context

        db = Room.inMemoryDatabaseBuilder(
            context, CoronaVirusDatabase::class.java
        ).build()
        dao = db.getLocationDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    private val location = Location(1.0, 2.0, 3)

    @Test
    fun testInsertLocation() {
        runBlocking {
            //when
            dao.insertLocation(location)
            val locations = dao.getAllLocations()

            //then
            locations shouldHaveSize 1
            with(locations[0]) {
                latitude shouldBe location.latitude
                longitude shouldBe location.longitude
                timeStamp shouldBe location.timeStamp
            }
        }
    }

    @Test
    fun testInsertLocationWithTheSameTimeStamp() {
        runBlocking {
            //given
            val changedLocation = location.copy(latitude = 212.0, longitude = 0.0)

            //when
            dao.insertLocation(location)
            dao.insertLocation(changedLocation)
            val locations = dao.getAllLocations()

            //then
            locations shouldHaveSize 1
            with(locations[0]) {
                latitude shouldBe changedLocation.latitude
                longitude shouldBe changedLocation.longitude
                timeStamp shouldBe changedLocation.timeStamp

            }
        }
    }

    @Test
    fun testGetLocationInBetweenTimes() {
        runBlocking {
            //given
            val changedLocation = location.copy(latitude = 212.0, longitude = 0.0, timeStamp = location.timeStamp + 1)

            //when
            dao.insertLocation(location)
            dao.insertLocation(changedLocation)
            val locationFromDb = dao.provideLastKnownLocationIn(1, 6)

            //then
            locationFromDb shouldNotBe null

            locationFromDb?.latitude shouldBe changedLocation.latitude
            locationFromDb?.longitude shouldBe changedLocation.longitude
            locationFromDb?.timeStamp shouldBe changedLocation.timeStamp
        }
    }

}