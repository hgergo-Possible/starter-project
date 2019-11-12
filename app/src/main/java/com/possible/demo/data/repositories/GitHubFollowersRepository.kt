package com.possible.demo.data.repositories

import com.possible.demo.data.localsource.GitHubFollowersLocalSource
import com.possible.demo.data.models.FollowersAnswer
import com.possible.demo.data.models.User
import com.possible.demo.data.services.GitHubFollowersService
import com.possible.demo.data.shared.NetworkException
import kotlinx.coroutines.CancellationException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single access point of the [followers][User].
 */
@Singleton
class GitHubFollowersRepository @Inject constructor(
        private val gitHubFollowersService: GitHubFollowersService,
        private val gitHubFollowersLocalSource: GitHubFollowersLocalSource
) {

    @Throws(NetworkException::class, CancellationException::class)
    suspend fun searchForFollowers(userName: String): FollowersAnswer =
            gitHubFollowersLocalSource.get(userName) ?: getRemoteFollowersAndCache(userName)

    private suspend fun getRemoteFollowersAndCache(userName: String) =
            gitHubFollowersService.get(userName).also { gitHubFollowersLocalSource.save(userName, it) }

}