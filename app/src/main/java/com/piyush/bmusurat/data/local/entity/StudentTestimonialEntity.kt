package com.piyush.bmusurat.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "student_testimonials")
data class StudentTestimonialEntity(
    @PrimaryKey
    val name: String,
    val designation: String,
    val photo: String,
    val testimonial: String
)