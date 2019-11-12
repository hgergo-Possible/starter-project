package com.possible.demo.data.localsource

import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.possible.demo.data.localsource.GitHubRepoLocalSource
import com.possible.demo.data.models.Repo
import com.possible.demo.data.shared.Clock
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@Suppress("TestFunctionName")
class GitHubRepoLocalSourceTest {

    private val expirationTime: Long = 500L
    private lateinit var gitHubRepoLocalSource: GitHubRepoLocalSource
    private lateinit var mockClock: Clock

    @Before
    fun setUp() {
        mockClock = mock()
        gitHubRepoLocalSource = GitHubRepoLocalSource(mockClock, expirationTime)
    }

    @Test
    fun GIVEN_no_data_inserted_value_to_cache_WHEN_the_data_is_requested_THEN_null_is_returned() {
        Assert.assertEquals(null, gitHubRepoLocalSource.get(""))
    }

    @Test
    fun GIVEN_inserted_value_to_cache_and_no_time_elapsed_WHEN_the_data_is_requested_THEN_its_returned() {
        val username = "alma"
        val expected = listOf(Repo(0, "n", "d", "u", "l", 5))
        gitHubRepoLocalSource.save(username, expected)

        Assert.assertEquals(expected, gitHubRepoLocalSource.get(username))
    }

    @Test
    fun GIVEN_inserted_value_to_cache_and_just_the_expiration_time_elapsed_WHEN_the_data_is_requested_THEN_its_returned() {
        val username = "alma"
        val expected = listOf(Repo(0, "n", "d", "u", "l", 5))
        var timeToReturn = 0L
        whenever(mockClock.currentTimeInMillis).doAnswer {
            timeToReturn
        }
        gitHubRepoLocalSource.save(username, expected)
        timeToReturn += expirationTime

        Assert.assertEquals(expected, gitHubRepoLocalSource.get(username))
    }

    @Test
    fun GIVEN_inserted_value_to_cache_and_just_over_the_expiration_time_elapsed_WHEN_the_data_is_requested_THEN_null_is_returned() {
        val username = "alma"
        val input = listOf(Repo(0, "n", "d", "u", "l", 5))
        var timeToReturn = 0L
        whenever(mockClock.currentTimeInMillis).doAnswer {
            timeToReturn
        }
        gitHubRepoLocalSource.save(username, input)
        timeToReturn += expirationTime + 1

        Assert.assertEquals(null, gitHubRepoLocalSource.get(username))
    }
}