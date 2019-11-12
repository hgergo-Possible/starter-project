package com.possible.demo.data.localsource

import com.possible.demo.data.models.Repo
import com.possible.demo.data.shared.Clock
import javax.inject.Inject

/**
 * Local cache for the [Repo] models.
 */
class GitHubRepoLocalSource constructor(private val clock: Clock, private val expirationTime: Long) {

    @Inject
    constructor(clock: Clock) : this(clock, 5000L)

    private val reposByUserName = mutableMapOf<String, Pair<Long, List<Repo>>>()

    fun get(username: String): List<Repo>? {
        val (expirationTime, repos) = reposByUserName[username] ?: return null

        return if (clock.currentTimeInMillis > expirationTime) {
            reposByUserName.remove(username)
            null
        } else {
            repos
        }
    }

    fun save(username: String, repos: List<Repo>) {
        reposByUserName[username] = clock.currentTimeInMillis + expirationTime to repos
    }
}