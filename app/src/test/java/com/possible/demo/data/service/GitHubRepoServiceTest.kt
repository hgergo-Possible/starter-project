package com.possible.demo.data.service

import com.possible.demo.data.models.Repo
import com.possible.demo.data.services.GitHubRepoService
import com.possible.demo.data.shared.DaggerNetworkTestComponent
import com.possible.demo.data.shared.NetworkException
import com.possible.demo.data.shared.enqueueRequest
import com.possible.demo.data.shared.readJsonResourceFileToString
import com.possible.demo.di.NetworkModule
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import javax.inject.Inject

@Suppress("TestFunctionName")
class GitHubRepoServiceTest {

    private lateinit var basePath: String
    private lateinit var mockWebServer: MockWebServer
    @Inject
    lateinit var gitHubRepoService: GitHubRepoService

    @Before
    fun setupUp() {
        mockWebServer = MockWebServer()
        basePath = "/github/test/"
        val baseUrl = mockWebServer.url(basePath).toString()
        DaggerNetworkTestComponent.builder()
                .setCoreModule(NetworkModule(baseUrl))
                .build()
                .inject(this)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test(timeout = 20000L)
    fun GIVEN_call_to_repo_service_WHEN_the_request_is_fired_THEN_it_is_setup_properly() = runBlocking {
        val username = "alma"
        mockWebServer.enqueueRequest(200,"[]")

        try {
            gitHubRepoService.get(username)
        } catch (t: Throwable) {

        }
        val request = mockWebServer.takeRequest()

        Assert.assertEquals("${basePath}users/${username}/repos", request.requestUrl.encodedPath())
        Assert.assertEquals("GET", request.method)
        Assert.assertEquals(null, request.requestUrl.query())
    }

    @Test(timeout = 20000L)
    fun GIVEN_empty_array_response_WHEN_the_request_is_fired_THEN_empty_list_is_returned() = runBlocking {
        val expectedResponse = emptyList<Repo>()
        mockWebServer.enqueueRequest(200, "[]")

        val actualResponse = gitHubRepoService.get("alma")

        Assert.assertEquals(expectedResponse, actualResponse)
    }

    @Test(timeout = 20000L)
    fun GIVEN_example_array_response_WHEN_the_request_is_fired_THEN_parsed_list_is_returned() = runBlocking {
        val expectedResponse = listOf(
                Repo(
                        id = 3070104,
                        name = "abs.io",
                        description = "Simple URL shortener for ActionBarSherlock using node.js and express.",
                        url = "https://github.com/JakeWharton/abs.io",
                        language = "JavaScript",
                        watchersCount = 8),
                Repo(
                        id = 1451060,
                        name = "ActionBarSherlock",
                        description = "[DEPRECATED] Action bar implementation which uses the native action bar on Android 4.0+ and a custom implementation on pre-4.0 through a single API and theme.",
                        url = "https://github.com/JakeWharton/ActionBarSherlock",
                        language = "Java",
                        watchersCount = 7245)
        )
        mockWebServer.enqueueRequest(200, readJsonResourceFileToString("data/repo/example_repo_response.json"))

        val actualResponse = gitHubRepoService.get("alma")

        Assert.assertEquals(expectedResponse, actualResponse)
    }

    @Test(timeout = 20000L, expected = NetworkException::class)
    fun GIVEN_invalid_json_response_WHEN_the_request_is_fired_THEN_network_exception_is_thrown() = runBlocking<Unit> {
        mockWebServer.enqueueRequest(200, "{}")

        gitHubRepoService.get("alma")
    }

    @Test(timeout = 20000L, expected = NetworkException::class)
    fun GIVEN_internal_server_error_WHEN_the_request_is_fired_THEN_network_exception_is_thrown() = runBlocking<Unit> {
        mockWebServer.enqueueRequest(500, "[]")

        gitHubRepoService.get("alma")
    }
}