package com.piyush.bmusurat.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.piyush.bmusurat.data.local.entity.LatestNewsEntity
import com.piyush.bmusurat.data.local.entity.StudentTestimonialEntity
import com.piyush.bmusurat.data.local.entity.UpcomingEventEntity

@Database(
    entities = [
        LatestNewsEntity::class,
        UpcomingEventEntity::class,
        StudentTestimonialEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun homeDao(): HomeDao
}