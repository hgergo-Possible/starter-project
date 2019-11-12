package com.possible.demo.data.services

import com.possible.demo.data.models.FollowersAnswer
import com.possible.demo.data.shared.GitHubApi
import com.possible.demo.data.shared.NetworkException
import com.possible.demo.data.shared.wrapExceptionIfNeeded
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.net.HttpURLConnection
import javax.inject.Inject

/**
 * Network access point for Followers based on [gitHubApi]
 */
class GitHubFollowersService @Inject constructor(private val gitHubApi: GitHubApi) {

    @Throws(NetworkException::class, CancellationException::class)
    suspend fun get(username: String): FollowersAnswer =
            try {
                FollowersAnswer.Followers(gitHubApi.searchForFollowers(username).orEmpty())
            } catch (httpException: HttpException) {
                if (httpException.code() == HttpURLConnection.HTTP_NOT_FOUND) {
                    FollowersAnswer.NoSuchUser
                } else {
                    throw wrapExceptionIfNeeded(httpException)
                }
            } catch (throwable: Throwable) {
                throw wrapExceptionIfNeeded(throwable)
            }
}