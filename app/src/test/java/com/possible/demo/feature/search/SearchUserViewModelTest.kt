@file:Suppress("TestFunctionName")

package com.possible.demo.feature.search

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
import com.possible.demo.data.models.FollowersAnswer
import com.possible.demo.data.models.User
import com.possible.demo.data.repositories.GitHubFollowersRepository
import com.possible.demo.data.shared.NetworkException
import com.possible.demo.feature.observeNextValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.internal.invocation.InterceptedInvocation
import java.util.concurrent.CountDownLatch
import kotlin.coroutines.Continuation

class SearchUserViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var mockGitHubFollowersRepository: GitHubFollowersRepository
    private lateinit var viewModel: SearchUserViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        mockGitHubFollowersRepository = mock()
        viewModel = SearchUserViewModel(mockGitHubFollowersRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test(timeout = 20000L)
    fun WHEN_viewModel_is_initialized_THEN_no_request_is_run() {
        verifyZeroInteractions(mockGitHubFollowersRepository)
    }

    @Test(timeout = 20000L)
    fun WHEN_viewModel_is_initialized_THEN_it_is_idle() {
        assertEquals(false, viewModel.showEmptyState.observeNextValue())
        assertEquals(false, viewModel.showErrorState.observeNextValue())
        assertEquals(false, viewModel.showLoading.observeNextValue())
        assertEquals(false, viewModel.showUsers.observeNextValue())
        assertEquals(false, viewModel.showNoSuchUser.observeNextValue())
        assertEquals(true, viewModel.showWaitingForInput.observeNextValue())
        assertEquals(null, viewModel.users.observeNextValue())
    }

    @Test(timeout = 20000L)
    fun GIVEN_initialized_viewModel_WHEN_a_character_is_typed_THEN_nothing_changes() {
        viewModel.onSearchTextChanged("a")

        assertEquals(false, viewModel.showEmptyState.observeNextValue())
        assertEquals(false, viewModel.showErrorState.observeNextValue())
        assertEquals(false, viewModel.showLoading.observeNextValue())
        assertEquals(false, viewModel.showUsers.observeNextValue())
        assertEquals(false, viewModel.showNoSuchUser.observeNextValue())
        assertEquals(true, viewModel.showWaitingForInput.observeNextValue())
        assertEquals(null, viewModel.users.observeNextValue())
    }

    @Test(timeout = 20000L)
    fun GIVEN_initialized_viewModel_AND_empty_response_WHEN_two_characters_are_typed_THEN_the_states_are_updated() = runBlocking {
        whenever(mockGitHubFollowersRepository.searchForFollowers("ab")).doReturn(FollowersAnswer.Followers(emptyList()))
        viewModel.onSearchTextChanged("ab")

        assertEquals(true, viewModel.showEmptyState.observeNextValue())
        assertEquals(false, viewModel.showErrorState.observeNextValue())
        assertEquals(false, viewModel.showLoading.observeNextValue())
        assertEquals(false, viewModel.showUsers.observeNextValue())
        assertEquals(false, viewModel.showNoSuchUser.observeNextValue())
        assertEquals(false, viewModel.showWaitingForInput.observeNextValue())
        assertEquals(emptyList<User>(), viewModel.users.observeNextValue())
    }

    @Test(timeout = 20000L)
    fun GIVEN_initialized_viewModel_AND_network_exception_thrown_WHEN_two_characters_are_typed_THEN_the_states_are_updated() = runBlocking {
        whenever(mockGitHubFollowersRepository.searchForFollowers("ab")).doThrow(NetworkException())
        viewModel.onSearchTextChanged("ab")

        assertEquals(false, viewModel.showEmptyState.observeNextValue())
        assertEquals(true, viewModel.showErrorState.observeNextValue())
        assertEquals(false, viewModel.showLoading.observeNextValue())
        assertEquals(false, viewModel.showUsers.observeNextValue())
        assertEquals(false, viewModel.showNoSuchUser.observeNextValue())
        assertEquals(false, viewModel.showWaitingForInput.observeNextValue())
        assertEquals(emptyList<User>(), viewModel.users.observeNextValue())
    }

    @Test(timeout = 20000L)
    fun GIVEN_initialized_viewModel_AND_non_empty_response_WHEN_two_characters_are_typed_THEN_the_states_are_updated() = runBlocking {
        val expected = listOf(User(0, "name", "alma"))
        whenever(mockGitHubFollowersRepository.searchForFollowers("ab")).doReturn(FollowersAnswer.Followers(expected))
        viewModel.onSearchTextChanged("ab")

        assertEquals(false, viewModel.showEmptyState.observeNextValue())
        assertEquals(false, viewModel.showErrorState.observeNextValue())
        assertEquals(false, viewModel.showLoading.observeNextValue())
        assertEquals(true, viewModel.showUsers.observeNextValue())
        assertEquals(false, viewModel.showNoSuchUser.observeNextValue())
        assertEquals(false, viewModel.showWaitingForInput.observeNextValue())
        assertEquals(expected, viewModel.users.observeNextValue())
    }

    @Test(timeout = 20000L)
    fun GIVEN_initialized_viewModel_AND_no_such_user_response_WHEN_two_characters_are_typed_THEN_the_states_are_updated() = runBlocking {
        val expected = FollowersAnswer.NoSuchUser
        whenever(mockGitHubFollowersRepository.searchForFollowers("ab")).doReturn(expected)
        viewModel.onSearchTextChanged("ab")

        assertEquals(false, viewModel.showEmptyState.observeNextValue())
        assertEquals(false, viewModel.showErrorState.observeNextValue())
        assertEquals(false, viewModel.showLoading.observeNextValue())
        assertEquals(false, viewModel.showUsers.observeNextValue())
        assertEquals(true, viewModel.showNoSuchUser.observeNextValue())
        assertEquals(false, viewModel.showWaitingForInput.observeNextValue())
        assertEquals(emptyList<User>(), viewModel.users.observeNextValue())
    }

    @Test(timeout = 20000L)
    fun GIVEN_initialized_viewModel_AND_pending_request_WHEN_two_characters_are_typed_THEN_the_states_are_updated() = runBlocking {
        Dispatchers.setMain(Dispatchers.IO)
        val latch = CountDownLatch(1)
        whenever(mockGitHubFollowersRepository.searchForFollowers("ab")).doAnswer {
            latch.await()
            FollowersAnswer.Followers(emptyList())
        }
        viewModel.onSearchTextChanged("ab")

        assertEquals(false, viewModel.showEmptyState.observeNextValue())
        assertEquals(false, viewModel.showErrorState.observeNextValue())
        assertEquals(true, viewModel.showLoading.observeNextValue())
        assertEquals(true, viewModel.showUsers.observeNextValue())
        assertEquals(false, viewModel.showNoSuchUser.observeNextValue())
        assertEquals(false, viewModel.showWaitingForInput.observeNextValue())
        assertEquals(null, viewModel.users.observeNextValue())
        latch.countDown()
    }

    @Test(timeout = 20000L)
    fun GIVEN_initialized_viewModel_AND_pending_request_WHEN_two_characters_are_typed_AND_then_the_again_THEN_no_new_request_is_run() = runBlocking {
        Dispatchers.setMain(Dispatchers.IO)
        val latch = CountDownLatch(1)
        val requestStartedLatch = CountDownLatch(1)
        whenever(mockGitHubFollowersRepository.searchForFollowers("ab")).doAnswer {
            requestStartedLatch.countDown()
            latch.await()
            FollowersAnswer.Followers(emptyList())
        }
        viewModel.onSearchTextChanged("ab")
        viewModel.onSearchTextChanged("ab")
        requestStartedLatch.await()

        verify(mockGitHubFollowersRepository, times(1)).searchForFollowers("ab")
        verifyNoMoreInteractions(mockGitHubFollowersRepository)
        latch.countDown()
    }

    @Test(timeout = 20000L)
    fun GIVEN_initialized_viewModel_AND_pending_request_WHEN_two_characters_are_typed_AND_then_another_THEN_the_previous_request_is_cancelled() = runBlocking {
        Dispatchers.setMain(Dispatchers.IO)
        val requestStartedLatch = CountDownLatch(1)
        val secondRequestStartedFinished = CountDownLatch(1)
        var firstContinuation: Continuation<List<User>>? = null
        whenever(mockGitHubFollowersRepository.searchForFollowers("ab")).doAnswer {
            firstContinuation = (it as InterceptedInvocation).rawArguments[1] as Continuation<List<User>>
            requestStartedLatch.countDown()
            secondRequestStartedFinished.await()

            FollowersAnswer.Followers(emptyList())
        }
        whenever(mockGitHubFollowersRepository.searchForFollowers("abc"))
                .doReturn(FollowersAnswer.Followers(listOf(User(0, "", ""))))
        viewModel.onSearchTextChanged("ab")
        requestStartedLatch.await()
        viewModel.onSearchTextChanged("abc")
        secondRequestStartedFinished.countDown()

        assertEquals(true, firstContinuation?.context?.get(Job)?.isCancelled)
    }
}