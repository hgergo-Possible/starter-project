package com.possible.demo.data.shared

import com.google.gson.annotations.SerializedName
import com.possible.demo.data.models.Repo
import com.possible.demo.data.models.User
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit service to access the GitHub api
 */
interface GitHubApi {

    @GET("users/{username}/followers")
    suspend fun searchForFollowers(@Path("username") username: String) : List<User>?

    @GET("users/{username}/repos")
    suspend fun getRepositoriesOfUser(@Path("username") username: String): List<Repo>?

}