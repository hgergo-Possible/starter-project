package com.possible.demo.data.models

import com.google.gson.annotations.SerializedName

/**
 * Model class representing a user's repository.
 */
data class Repo(
        @SerializedName("id") val id: Long,
        @SerializedName("name") val name: String,
        @SerializedName("description") val description: String,
        @SerializedName("html_url") val url: String,
        @SerializedName("language") val language: String,
        @SerializedName("watchers_count") val watchersCount: Int
)