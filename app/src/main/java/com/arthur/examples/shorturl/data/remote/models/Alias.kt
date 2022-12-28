package com.arthur.examples.shorturl.data.remote.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Alias(
    val alias: String,
    @SerializedName("_links")
    val link: Link,
) : Parcelable
