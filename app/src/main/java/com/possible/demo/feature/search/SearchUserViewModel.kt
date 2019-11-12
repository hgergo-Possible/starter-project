package com.possible.demo.feature.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.possible.demo.data.models.FollowersAnswer
import com.possible.demo.data.models.User
import com.possible.demo.data.repositories.GitHubFollowersRepository
import com.possible.demo.data.shared.NetworkException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel of [SearchUsersFragment], it accesses the [GitHubFollowersRepository] to search for users defined via [onSearchTextChanged]
 */
class SearchUserViewModel @Inject constructor(private val gitHubFollowersRepository: GitHubFollowersRepository) : ViewModel() {

    private val state = MutableLiveData<State>().apply { value = State.IDLE }
    val showLoading = Transformations.map(state) { it == State.SEARCHING }
    val showEmptyState = Transformations.map(state) { it == State.NO_FOLLOWERS }
    val showWaitingForInput = Transformations.map(state) { it == State.IDLE }
    val showErrorState = Transformations.map(state) { it == State.ERROR }
    val showUsers = Transformations.map(state) { it == State.FOLLOWERS_FOUND || it == State.SEARCHING }
    val showNoSuchUser = Transformations.map(state) { it == State.NO_SUCH_USER}

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> get() = _users
    private val parentJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(Dispatchers.Main + parentJob)
    private var searchJob: Job? = null
    private var lastTextToSearch: String? = null

    /**
     * Notifies the viewModel about the user input.
     *
     * @param textToSearch the text the user wrote to search for
     */
    fun onSearchTextChanged(textToSearch: String) {
        if (lastTextToSearch == textToSearch && state.value != State.ERROR) {
            return
        }
        lastTextToSearch = textToSearch

        searchJob?.cancel()
        if (textToSearch.length < MINIMUM_LENGTH_OF_QUERY_STRING) {
            state.value = State.IDLE
            return
        }

        state.value = State.SEARCHING
        searchJob = viewModelScope.launch {
            val followersAnswer = try {
                gitHubFollowersRepository.searchForFollowers(textToSearch)
            } catch (networkException: NetworkException) {
                _users.value = emptyList()
                state.value = State.ERROR
                return@launch
            }
            state.value = when {
                followersAnswer == FollowersAnswer.NoSuchUser -> State.NO_SUCH_USER
                followersAnswer is FollowersAnswer.Followers && followersAnswer.followers.isEmpty() -> State.NO_FOLLOWERS
                else -> State.FOLLOWERS_FOUND
            }
            _users.value = (followersAnswer as? FollowersAnswer.Followers)?.followers.orEmpty()
        }
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }

    private enum class State {
        IDLE, SEARCHING, ERROR, NO_FOLLOWERS, FOLLOWERS_FOUND, NO_SUCH_USER
    }

    companion object {
        private const val MINIMUM_LENGTH_OF_QUERY_STRING = 2
    }
}