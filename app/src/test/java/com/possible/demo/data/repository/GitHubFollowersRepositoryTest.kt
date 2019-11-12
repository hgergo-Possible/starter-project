package com.possible.demo.data.repository

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.possible.demo.data.localsource.GitHubFollowersLocalSource
import com.possible.demo.data.localsource.GitHubRepoLocalSource
import com.possible.demo.data.models.FollowersAnswer
import com.possible.demo.data.models.Repo
import com.possible.demo.data.models.User
import com.possible.demo.data.repositories.GitHubFollowersRepository
import com.possible.demo.data.repositories.GitHubRepoRepository
import com.possible.demo.data.services.GitHubFollowersService
import com.possible.demo.data.services.GitHubRepoService
import com.possible.demo.data.shared.NetworkException
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@Suppress("TestFunctionName")
class GitHubFollowersRepositoryTest {

    private lateinit var mockGitHubFollowersLocalSource: GitHubFollowersLocalSource
    private lateinit var mockGitHubFollowersService: GitHubFollowersService
    private lateinit var gitHubFollowersRepository: GitHubFollowersRepository

    @Before
    fun setUp() {
        mockGitHubFollowersLocalSource = mock()
        mockGitHubFollowersService = mock()
        gitHubFollowersRepository = GitHubFollowersRepository(mockGitHubFollowersService, mockGitHubFollowersLocalSource)
    }

    @Test(timeout = 20000L)
    fun GIVEN_no_data_in_localSource_but_from_api_WHEN_a_repos_are_requested_THE_the_data_is_returned_from_api() = runBlocking {
        val username = "username"
        val expected = FollowersAnswer.Followers(listOf(User(0, "n", "i")))
        whenever(mockGitHubFollowersLocalSource.get(username)).doReturn(null)
        whenever(mockGitHubFollowersService.get(username)).doReturn(expected)

        val actual = gitHubFollowersRepository.searchForFollowers(username)

        Assert.assertEquals(expected, actual)
        verify(mockGitHubFollowersLocalSource, times(1)).get(username)
        verify(mockGitHubFollowersService, times(1)).get(username)
        verify(mockGitHubFollowersLocalSource, times(1)).save(username, expected)
        verifyNoMoreInteractions(mockGitHubFollowersLocalSource)
        verifyNoMoreInteractions(mockGitHubFollowersService)
    }

    @Test(timeout = 20000L, expected = NetworkException::class)
    fun GIVEN_no_data_in_localSource_AND_error_from_api_WHEN_a_repos_are_requested_THE_the_data_is_returned_from_api() = runBlocking<Unit> {
        val username = "username"
        whenever(mockGitHubFollowersLocalSource.get(username)).doReturn(null)
        whenever(mockGitHubFollowersService.get(username)).doThrow(NetworkException())

        try {
            gitHubFollowersRepository.searchForFollowers(username)
        } catch (networkException: NetworkException) {
            verify(mockGitHubFollowersLocalSource, times(1)).get(username)
            verify(mockGitHubFollowersService, times(1)).get(username)
            verifyNoMoreInteractions(mockGitHubFollowersLocalSource)
            verifyNoMoreInteractions(mockGitHubFollowersService)

            throw networkException
        }
    }

    @Test(timeout = 20000L)
    fun GIVEN_data_in_localSource_WHEN_a_repos_are_requested_THE_the_data_is_returned_from_the_cache() = runBlocking {
        val username = "username"
        val expected = FollowersAnswer.Followers(listOf(User(0, "n", "i")))
        whenever(mockGitHubFollowersLocalSource.get(username)).doReturn(expected)
        whenever(mockGitHubFollowersService.get(username)).doThrow(NetworkException())

        val actual = gitHubFollowersRepository.searchForFollowers(username)

        Assert.assertEquals(expected, actual)
        verify(mockGitHubFollowersLocalSource, times(1)).get(username)
        verifyNoMoreInteractions(mockGitHubFollowersLocalSource)
        verifyNoMoreInteractions(mockGitHubFollowersService)
    }

}