package com.possible.demo.data.repository

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.possible.demo.data.localsource.GitHubRepoLocalSource
import com.possible.demo.data.models.Repo
import com.possible.demo.data.repositories.GitHubRepoRepository
import com.possible.demo.data.services.GitHubRepoService
import com.possible.demo.data.shared.NetworkException
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@Suppress("TestFunctionName")
class GitHubRepoRepositoryTest {

    private lateinit var mockGitHubRepoLocalSource: GitHubRepoLocalSource
    private lateinit var mockGitHubRepoService: GitHubRepoService
    private lateinit var gitHubRepoRepository: GitHubRepoRepository

    @Before
    fun setUp() {
        mockGitHubRepoLocalSource = mock()
        mockGitHubRepoService = mock()
        gitHubRepoRepository = GitHubRepoRepository(mockGitHubRepoService, mockGitHubRepoLocalSource)
    }

    @Test(timeout = 20000L)
    fun GIVEN_no_data_in_localSource_but_from_api_WHEN_a_repos_are_requested_THE_the_data_is_returned_from_api() = runBlocking {
        val username = "username"
        val expected = listOf(Repo(0, "n", "i", "a", "b", 4))
        whenever(mockGitHubRepoLocalSource.get(username)).doReturn(null)
        whenever(mockGitHubRepoService.get(username)).doReturn(expected)

        val actual = gitHubRepoRepository.getRepos(username)

        Assert.assertEquals(expected, actual)
        verify(mockGitHubRepoLocalSource, times(1)).get(username)
        verify(mockGitHubRepoService, times(1)).get(username)
        verify(mockGitHubRepoLocalSource, times(1)).save(username, expected)
        verifyNoMoreInteractions(mockGitHubRepoLocalSource)
        verifyNoMoreInteractions(mockGitHubRepoService)
    }

    @Test(timeout = 20000L, expected = NetworkException::class)
    fun GIVEN_no_data_in_localSource_AND_error_from_api_WHEN_a_repos_are_requested_THE_the_data_is_returned_from_api() = runBlocking<Unit> {
        val username = "username"
        whenever(mockGitHubRepoLocalSource.get(username)).doReturn(null)
        whenever(mockGitHubRepoService.get(username)).doThrow(NetworkException())

        try {
            gitHubRepoRepository.getRepos(username)
        } catch (networkException: NetworkException) {
            verify(mockGitHubRepoLocalSource, times(1)).get(username)
            verify(mockGitHubRepoService, times(1)).get(username)
            verifyNoMoreInteractions(mockGitHubRepoLocalSource)
            verifyNoMoreInteractions(mockGitHubRepoService)

            throw networkException
        }
    }

    @Test(timeout = 20000L)
    fun GIVEN_data_in_localSource_WHEN_a_repos_are_requested_THE_the_data_is_returned_from_the_cache() = runBlocking {
        val username = "username"
        val expected = listOf(Repo(0, "n", "i", "a", "b", 4))
        whenever(mockGitHubRepoLocalSource.get(username)).doReturn(expected)
        whenever(mockGitHubRepoService.get(username)).doThrow(NetworkException())

        val actual = gitHubRepoRepository.getRepos(username)

        Assert.assertEquals(expected, actual)
        verify(mockGitHubRepoLocalSource, times(1)).get(username)
        verifyNoMoreInteractions(mockGitHubRepoLocalSource)
        verifyNoMoreInteractions(mockGitHubRepoService)
    }

}