package com.arthur.examples.shorturl.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alias")
data class AliasLocal(
    @PrimaryKey
    val alias: String,
    val self: String,
    val shorted: String,
)
