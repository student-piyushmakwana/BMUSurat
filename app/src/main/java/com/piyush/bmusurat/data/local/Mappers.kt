package com.piyush.bmusurat.data.local

import com.piyush.bmusurat.data.local.entity.LatestNewsEntity
import com.piyush.bmusurat.data.local.entity.StudentTestimonialEntity
import com.piyush.bmusurat.data.local.entity.UpcomingEventEntity
import com.piyush.bmusurat.data.models.LatestNews
import com.piyush.bmusurat.data.models.StudentTestimonial
import com.piyush.bmusurat.data.models.UpcomingEvent

fun LatestNews.toEntity(): LatestNewsEntity {
    return LatestNewsEntity(
        description = this.description,
        date = this.date,
        link = this.link
    )
}

fun LatestNewsEntity.toDto(): LatestNews {
    return LatestNews(
        description = this.description,
        date = this.date,
        link = this.link
    )
}

fun UpcomingEvent.toEntity(): UpcomingEventEntity {
    return UpcomingEventEntity(
        description = this.description,
        date = this.date,
        link = this.link
    )
}

fun UpcomingEventEntity.toDto(): UpcomingEvent {
    return UpcomingEvent(
        description = this.description,
        date = this.date,
        link = this.link
    )
}

fun StudentTestimonial.toEntity(): StudentTestimonialEntity {
    return StudentTestimonialEntity(
        name = this.name,
        designation = this.designation,
        photo = this.photo,
        testimonial = this.testimonial
    )
}

fun StudentTestimonialEntity.toDto(): StudentTestimonial {
    return StudentTestimonial(
        name = this.name,
        designation = this.designation,
        photo = this.photo,
        testimonial = this.testimonial
    )
}