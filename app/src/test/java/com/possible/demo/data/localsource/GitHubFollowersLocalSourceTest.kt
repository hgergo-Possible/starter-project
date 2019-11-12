package com.possible.demo.data.localsource

import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.possible.demo.data.models.FollowersAnswer
import com.possible.demo.data.models.User
import com.possible.demo.data.shared.Clock
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@Suppress("TestFunctionName")
class GitHubFollowersLocalSourceTest {

    private val expirationTime: Long = 500L
    private lateinit var gitHubFollowersLocalSource: GitHubFollowersLocalSource
    private lateinit var mockClock: Clock

    @Before
    fun setUp() {
        mockClock = mock()
        gitHubFollowersLocalSource = GitHubFollowersLocalSource(mockClock, expirationTime)
    }

    @Test
    fun GIVEN_no_data_inserted_value_to_cache_WHEN_the_data_is_requested_THEN_null_is_returned() {
        Assert.assertEquals(null, gitHubFollowersLocalSource.get(""))
    }

    @Test
    fun GIVEN_inserted_followers_to_cache_and_no_time_elapsed_WHEN_the_data_is_requested_THEN_its_returned() {
        val username = "alma"
        val expected = FollowersAnswer.Followers(listOf(User(0, "n", "d")))
        gitHubFollowersLocalSource.save(username, expected)

        Assert.assertEquals(expected, gitHubFollowersLocalSource.get(username))
    }

    @Test
    fun GIVEN_inserted_followers_to_cache_and_just_the_expiration_time_elapsed_WHEN_the_data_is_requested_THEN_its_returned() {
        val username = "alma"
        val expected = FollowersAnswer.Followers(listOf(User(0, "n", "d")))
        var timeToReturn = 0L
        whenever(mockClock.currentTimeInMillis).doAnswer {
            timeToReturn
        }
        gitHubFollowersLocalSource.save(username, expected)
        timeToReturn += expirationTime

        Assert.assertEquals(expected, gitHubFollowersLocalSource.get(username))
    }

    @Test
    fun GIVEN_inserted_followers_to_cache_and_just_over_the_expiration_time_elapsed_WHEN_the_data_is_requested_THEN_null_is_returned() {
        val username = "alma"
        val input = FollowersAnswer.Followers(listOf(User(0, "n", "d")))
        var timeToReturn = 0L
        whenever(mockClock.currentTimeInMillis).doAnswer {
            timeToReturn
        }
        gitHubFollowersLocalSource.save(username, input)
        timeToReturn += expirationTime + 1

        Assert.assertEquals(null, gitHubFollowersLocalSource.get(username))
    }

    @Test
    fun GIVEN_inserted_no_such_user_to_cache_and_no_time_elapsed_WHEN_the_data_is_requested_THEN_its_returned() {
        val username = "alma"
        val expected = FollowersAnswer.NoSuchUser
        gitHubFollowersLocalSource.save(username, expected)

        Assert.assertEquals(expected, gitHubFollowersLocalSource.get(username))
    }

    @Test
    fun GIVEN_inserted_no_such_user_to_cache_and_just_the_expiration_time_elapsed_WHEN_the_data_is_requested_THEN_its_returned() {
        val username = "alma"
        val expected = FollowersAnswer.NoSuchUser
        var timeToReturn = 0L
        whenever(mockClock.currentTimeInMillis).doAnswer {
            timeToReturn
        }
        gitHubFollowersLocalSource.save(username, expected)
        timeToReturn += expirationTime

        Assert.assertEquals(expected, gitHubFollowersLocalSource.get(username))
    }

    @Test
    fun GIVEN_inserted_no_such_user_to_cache_and_just_over_the_expiration_time_elapsed_WHEN_the_data_is_requested_THEN_null_is_returned() {
        val username = "alma"
        var timeToReturn = 0L
        whenever(mockClock.currentTimeInMillis).doAnswer {
            timeToReturn
        }
        gitHubFollowersLocalSource.save(username, FollowersAnswer.NoSuchUser)
        timeToReturn += expirationTime + 1

        Assert.assertEquals(null, gitHubFollowersLocalSource.get(username))
    }
}