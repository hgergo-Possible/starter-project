package com.possible.demo.data.models

import com.google.gson.annotations.SerializedName

/**
 * Model class representing a user that can be searched.
 */
data class User(
        @SerializedName("id") val id: Int,
        @SerializedName("login") val name: String,
        @SerializedName("avatar_url") val imageUrl: String
)