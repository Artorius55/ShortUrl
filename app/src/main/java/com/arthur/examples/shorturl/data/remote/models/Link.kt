package com.arthur.examples.shorturl.data.remote.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Link(
    val self:String,
    @SerializedName("short")
    val shorted:String,
) : Parcelable
