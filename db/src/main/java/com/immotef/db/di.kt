package com.immotef.db

import com.immotef.db.infected.InfectedDAO
import com.immotef.db.location.LocationDao
import com.immotef.db.meet.MeetDAO
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

/**
 *
 */


val dbModule = module {
    single<CoronaVirusDatabase> { CoronaVirusDatabase.buildDatabase(androidApplication()) }
    factory<ClearDB> { get<CoronaVirusDatabase>() }
    single<MeetDAO> { get<CoronaVirusDatabase>().getMeetDao() }
    single<LocationDao> { get<CoronaVirusDatabase>().getLocationDao() }
    single<InfectedDAO> { get<CoronaVirusDatabase>().getInfectedDao() }
}