package com.possible.demo.feature.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.possible.demo.data.repositories.GitHubRepoRepository
import com.possible.demo.data.shared.NetworkException
import com.possible.demo.data.models.Repo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel of [RepositoriesFragment], it accesses the [GitHubRepoRepository] to get the repositories of the given user defined via [setUserName]
 */
class RepositoriesViewModel @Inject constructor(private val gitHubRepoRepository: GitHubRepoRepository) : ViewModel() {

    private val parentJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(Dispatchers.Main + parentJob)

    private val _repositories = MutableLiveData<List<Repo>>()
    val repositories: LiveData<List<Repo>> get() = _repositories
    private val state = MutableLiveData<State>()
    val showLoading = Transformations.map(state) { it == State.LOADING }
    val showEmptyState = Transformations.map(state) { it == State.NO_RESULTS }
    val showErrorState = Transformations.map(state) { it == State.ERROR }

    private var username: String? = null

    /**
     * Sets the username the ViewModel should work with.
     */
    fun setUserName(username: String) {
        if (username == this.username) return

        this.username = username
        runRequest()
    }

    /**
     * Retries the repository request if it failed
     */
    fun onRetry() {
        if (state.value != State.ERROR) return

        runRequest()
    }

    private fun runRequest() {
        val username = username ?: return
        state.value = State.LOADING
        viewModelScope.launch {
            val repositories = try {
                gitHubRepoRepository.getRepos(username)
            } catch (networkException: NetworkException) {
                state.value = State.ERROR
                return@launch
            }
            state.value = if (repositories.isEmpty()) State.NO_RESULTS else State.RESULTS
            _repositories.value = repositories
        }
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }

    private enum class State {
        LOADING, ERROR, NO_RESULTS, RESULTS
    }
}