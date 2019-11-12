package com.possible.demo.data.localsource

import com.possible.demo.data.models.FollowersAnswer
import com.possible.demo.data.models.User
import com.possible.demo.data.shared.Clock
import javax.inject.Inject

/**
 * Local cache for the [User] models.
 */
class GitHubFollowersLocalSource constructor(private val clock: Clock, private val expirationTime: Long) {

    @Inject
    constructor(clock: Clock) : this(clock, 60000L)

    private val followersByUserName = mutableMapOf<String, Pair<Long, FollowersAnswer>>()

    fun get(username: String): FollowersAnswer? {
        val (expirationTime, repos) = followersByUserName[username] ?: return null

        return if (clock.currentTimeInMillis > expirationTime) {
            followersByUserName.remove(username)
            null
        } else {
            repos
        }
    }

    fun save(username: String, repos: FollowersAnswer) {
        followersByUserName[username] = clock.currentTimeInMillis + expirationTime to repos
    }
}