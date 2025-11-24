package com.piyush.bmusurat.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "latest_news")
data class LatestNewsEntity(
    @PrimaryKey
    val description: String,
    val date: String,
    val link: String
)