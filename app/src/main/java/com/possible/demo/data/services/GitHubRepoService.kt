package com.possible.demo.data.services

import com.possible.demo.data.models.Repo
import com.possible.demo.data.shared.GitHubApi
import com.possible.demo.data.shared.NetworkException
import com.possible.demo.data.shared.wrapExceptionIfNeeded
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

/**
 * Network access point for Repositories based on [gitHubApi]
 */
class GitHubRepoService @Inject constructor(private val gitHubApi: GitHubApi) {

    @Throws(NetworkException::class, CancellationException::class)
    suspend fun get(username: String): List<Repo> =
            try {
                gitHubApi.getRepositoriesOfUser(username).orEmpty()
            } catch (throwable: Throwable) {
                throw wrapExceptionIfNeeded(throwable)
            }
}