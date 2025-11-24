package com.piyush.bmusurat.data.models

import com.google.gson.annotations.SerializedName

data class HomeResponse(
    @SerializedName("data")
    val data: DataModel,
    @SerializedName("image_url")
    val imageUrl: String,
    @SerializedName("success")
    val success: Boolean
)

data class DataModel(
    @SerializedName("latest_news")
    val latestNews: List<LatestNews>,
    @SerializedName("student_testimonials")
    val studentTestimonials: List<StudentTestimonial>,
    @SerializedName("upcoming_events")
    val upcomingEvents: List<UpcomingEvent>
)

data class LatestNews(
    @SerializedName("date")
    val date: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("link")
    val link: String
)

data class StudentTestimonial(
    @SerializedName("designation")
    val designation: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("photo")
    val photo: String,
    @SerializedName("testimonial")
    val testimonial: String
)

data class UpcomingEvent(
    @SerializedName("date")
    val date: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("link")
    val link: String
)