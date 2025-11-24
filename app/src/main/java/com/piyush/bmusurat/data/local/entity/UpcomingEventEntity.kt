package com.piyush.bmusurat.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "upcoming_events")
data class UpcomingEventEntity(
    @PrimaryKey
    val description: String,
    val date: String,
    val link: String
)