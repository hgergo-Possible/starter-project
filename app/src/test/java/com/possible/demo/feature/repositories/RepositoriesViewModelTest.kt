@file:Suppress("TestFunctionName")

package com.possible.demo.feature.repositories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.possible.demo.data.models.Repo
import com.possible.demo.data.repositories.GitHubRepoRepository
import com.possible.demo.data.shared.NetworkException
import com.possible.demo.feature.observeNextValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch

class RepositoriesViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var mockGitHubRepoRepository: GitHubRepoRepository
    private lateinit var viewModel: RepositoriesViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        mockGitHubRepoRepository = mock()
        viewModel = RepositoriesViewModel(mockGitHubRepoRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test(timeout = 20000L)
    fun WHEN_viewModel_is_non_initialized_THEN_no_request_is_run() {
        verifyZeroInteractions(mockGitHubRepoRepository)
    }

    @Test(timeout = 20000L)
    fun WHEN_viewModel_is_non_initialized_THEN_all_states_are_null() {
        assertEquals(null, viewModel.showEmptyState.observeNextValue())
        assertEquals(null, viewModel.showErrorState.observeNextValue())
        assertEquals(null, viewModel.showLoading.observeNextValue())
        assertEquals(null, viewModel.repositories.observeNextValue())
    }

    @Test(timeout = 20000L)
    fun GIVEN_initialized_viewModel_WHEN_username_is_reset_THEN_request_is_not_run() = runBlocking {
        whenever(mockGitHubRepoRepository.getRepos("a")).doReturn(emptyList())
        viewModel.setUserName("a")
        viewModel.setUserName("a")

        verify(mockGitHubRepoRepository, times(1)).getRepos("a")
        verifyNoMoreInteractions(mockGitHubRepoRepository)
    }

    @Test(timeout = 20000L)
    fun GIVEN_non_initialized_viewModel_via_username_AND_empty_response_WHEN_initialized_THEN_the_states_are_updated() = runBlocking {
        whenever(mockGitHubRepoRepository.getRepos("a")).doReturn(emptyList())
        viewModel.setUserName("a")

        assertEquals(true, viewModel.showEmptyState.observeNextValue())
        assertEquals(false, viewModel.showErrorState.observeNextValue())
        assertEquals(false, viewModel.showLoading.observeNextValue())
        assertEquals(emptyList<Repo>(), viewModel.repositories.observeNextValue())
        verify(mockGitHubRepoRepository, times(1)).getRepos("a")
        verifyNoMoreInteractions(mockGitHubRepoRepository)
    }

    @Test(timeout = 20000L)
    fun GIVEN_non_initialized_viewModel_AND_network_exception_thrown_WHEN_initialized_THEN_the_states_are_updated() = runBlocking {
        whenever(mockGitHubRepoRepository.getRepos("a")).doThrow(NetworkException())
        viewModel.setUserName("a")

        assertEquals(false, viewModel.showEmptyState.observeNextValue())
        assertEquals(true, viewModel.showErrorState.observeNextValue())
        assertEquals(false, viewModel.showLoading.observeNextValue())
        assertEquals(null, viewModel.repositories.observeNextValue())
        verify(mockGitHubRepoRepository, times(1)).getRepos("a")
        verifyNoMoreInteractions(mockGitHubRepoRepository)
    }

    @Test(timeout = 20000L)
    fun GIVEN_non_initialized_viewModel_AND_non_empty_response_WHEN_initialized_THEN_the_states_are_updated() = runBlocking {
        val expected = listOf(Repo(0, "name", "d", "", "", 5))
        whenever(mockGitHubRepoRepository.getRepos("a")).doReturn(expected)
        viewModel.setUserName("a")

        assertEquals(false, viewModel.showEmptyState.observeNextValue())
        assertEquals(false, viewModel.showErrorState.observeNextValue())
        assertEquals(false, viewModel.showLoading.observeNextValue())
        assertEquals(expected, viewModel.repositories.observeNextValue())
        verify(mockGitHubRepoRepository, times(1)).getRepos("a")
        verifyNoMoreInteractions(mockGitHubRepoRepository)
    }

    @Test(timeout = 20000L)
    fun GIVEN_non_initialized_viewModel_AND_pending_requests_WHEN_initialized_THEN_the_states_are_updated() = runBlocking {
        Dispatchers.setMain(Dispatchers.IO)
        val latch = CountDownLatch(1)
        whenever(mockGitHubRepoRepository.getRepos("a")).doAnswer {
            latch.await()
            emptyList()
        }
        viewModel.setUserName("a")

        assertEquals(false, viewModel.showEmptyState.observeNextValue())
        assertEquals(false, viewModel.showErrorState.observeNextValue())
        assertEquals(true, viewModel.showLoading.observeNextValue())
        assertEquals(null, viewModel.repositories.observeNextValue())
        verify(mockGitHubRepoRepository, times(1)).getRepos("a")
        verifyNoMoreInteractions(mockGitHubRepoRepository)

        latch.countDown()
    }

    @Test(timeout = 20000L)
    fun GIVEN_viewModel_in_error_state_AND_failing_request_WHEN_retried_THEN_the_request_is_retried() = runBlocking {
        whenever(mockGitHubRepoRepository.getRepos("a")).doThrow(NetworkException())
        viewModel.setUserName("a")

        viewModel.onRetry()

        assertEquals(false, viewModel.showEmptyState.observeNextValue())
        assertEquals(true, viewModel.showErrorState.observeNextValue())
        assertEquals(false, viewModel.showLoading.observeNextValue())
        assertEquals(null, viewModel.repositories.observeNextValue())
        verify(mockGitHubRepoRepository, times(2)).getRepos("a")
        verifyNoMoreInteractions(mockGitHubRepoRepository)
    }

    @Test(timeout = 20000L)
    fun GIVEN_viewModel_in_error_state_AND_succeeding_request_WHEN_retried_THEN_the_request_is_retried_AND_states_are_updated_properly() = runBlocking {
        var requestCounter = 0
        whenever(mockGitHubRepoRepository.getRepos("a")).doAnswer {
            if (requestCounter == 0){
                requestCounter++
                throw NetworkException()
            } else {
                emptyList()
            }
        }
        viewModel.setUserName("a")

        viewModel.onRetry()

        assertEquals(true, viewModel.showEmptyState.observeNextValue())
        assertEquals(false, viewModel.showErrorState.observeNextValue())
        assertEquals(false, viewModel.showLoading.observeNextValue())
        assertEquals(emptyList<Repo>(), viewModel.repositories.observeNextValue())
        verify(mockGitHubRepoRepository, times(2)).getRepos("a")
        verifyNoMoreInteractions(mockGitHubRepoRepository)
    }
}