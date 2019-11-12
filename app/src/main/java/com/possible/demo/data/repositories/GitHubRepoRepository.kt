package com.possible.demo.data.repositories

import com.possible.demo.data.shared.NetworkException
import com.possible.demo.data.localsource.GitHubRepoLocalSource
import com.possible.demo.data.services.GitHubRepoService
import com.possible.demo.data.models.Repo
import kotlinx.coroutines.CancellationException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single access point of the [Repo] of the [users][com.possible.demo.data.models.User].
 */
@Singleton
class GitHubRepoRepository @Inject constructor(
        private val gitHubRepoService: GitHubRepoService,
        private val gitHubRepoLocalSource: GitHubRepoLocalSource
) {

    @Throws(NetworkException::class, CancellationException::class)
    suspend fun getRepos(userName: String): List<Repo> =
            gitHubRepoLocalSource.get(userName) ?: getRemoteReposAndCacheThem(userName)

    private suspend fun getRemoteReposAndCacheThem(userName: String): List<Repo> =
            gitHubRepoService.get(userName).also { gitHubRepoLocalSource.save(userName, it) }
}