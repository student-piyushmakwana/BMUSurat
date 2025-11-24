package com.piyush.bmusurat.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.piyush.bmusurat.data.local.entity.LatestNewsEntity
import com.piyush.bmusurat.data.local.entity.StudentTestimonialEntity
import com.piyush.bmusurat.data.local.entity.UpcomingEventEntity

@Dao
interface HomeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLatestNews(news: List<LatestNewsEntity>)

    @Query("SELECT * FROM latest_news")
    suspend fun getLatestNews(): List<LatestNewsEntity>

    @Query("DELETE FROM latest_news")
    suspend fun clearLatestNews()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUpcomingEvents(events: List<UpcomingEventEntity>)

    @Query("SELECT * FROM upcoming_events")
    suspend fun getUpcomingEvents(): List<UpcomingEventEntity>

    @Query("DELETE FROM upcoming_events")
    suspend fun clearUpcomingEvents()
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudentTestimonials(testimonials: List<StudentTestimonialEntity>)

    @Query("SELECT * FROM student_testimonials")
    suspend fun getStudentTestimonials(): List<StudentTestimonialEntity>

    @Query("DELETE FROM student_testimonials")
    suspend fun clearStudentTestimonials()
}