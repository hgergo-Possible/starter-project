package com.possible.demo.data.service

import com.possible.demo.data.models.FollowersAnswer
import com.possible.demo.data.models.User
import com.possible.demo.data.services.GitHubFollowersService
import com.possible.demo.data.shared.DaggerNetworkTestComponent
import com.possible.demo.data.shared.NetworkException
import com.possible.demo.data.shared.enqueueRequest
import com.possible.demo.data.shared.readJsonResourceFileToString
import com.possible.demo.di.NetworkModule
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import javax.inject.Inject

@Suppress("TestFunctionName")
class GitHubFollowersServiceTest {

    private lateinit var basePath: String
    private lateinit var mockWebServer: MockWebServer
    @Inject
    lateinit var gitHubFollowersService: GitHubFollowersService

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
    fun GIVEN_call_to_user_service_WHEN_the_request_is_fired_THEN_it_is_setup_properly() = runBlocking {
        val username = "alma"
        mockWebServer.enqueueRequest(200, "[]")

        try {
            gitHubFollowersService.get(username)
        } catch (t: Throwable) {

        }
        val request = mockWebServer.takeRequest()

        Assert.assertEquals("${basePath}users/$username/followers", request.requestUrl.encodedPath())
        Assert.assertEquals("GET", request.method)
        Assert.assertEquals(emptySet<String?>(), request.requestUrl.queryParameterNames())
    }

    @Test(timeout = 20000L)
    fun GIVEN_empty_array_response_WHEN_the_request_is_fired_THEN_empty_list_is_returned() = runBlocking {
        val expectedResponse = FollowersAnswer.Followers(emptyList<User>())
        mockWebServer.enqueueRequest(200, readJsonResourceFileToString("data/user/empty_users_response.json"))

        val actualResponse = gitHubFollowersService.get("alma")

        Assert.assertEquals(expectedResponse, actualResponse)
    }

    @Test(timeout = 20000L)
    fun GIVEN_example_array_response_WHEN_the_request_is_fired_THEN_parsed_list_is_returned() = runBlocking {
        val expectedResponse = FollowersAnswer.Followers(
                listOf(
                        User(
                                id = 66577,
                                name = "JakeWharton",
                                imageUrl = "https://avatars0.githubusercontent.com/u/66577?v=4"
                        ),
                        User(
                                id = 463941,
                                name = "highgic",
                                imageUrl = "https://avatars1.githubusercontent.com/u/463941?v=4"
                        )
                )
        )
        mockWebServer.enqueueRequest(200, readJsonResourceFileToString("data/user/example_users_response.json"))

        val actualResponse = gitHubFollowersService.get("alma")

        Assert.assertEquals(expectedResponse, actualResponse)
    }

    @Test(timeout = 20000L)
    fun GIVEN_not_found_response_WHEN_the_request_is_fired_THEN_network_exception_is_thrown() = runBlocking<Unit> {
        val expected = FollowersAnswer.NoSuchUser
        mockWebServer.enqueueRequest(404, readJsonResourceFileToString("data/user/user_not_found_response.json"))

        val actual = gitHubFollowersService.get("alma")

        Assert.assertEquals(expected, actual)
    }

    @Test(timeout = 20000L, expected = NetworkException::class)
    fun GIVEN_invalid_json_response_WHEN_the_request_is_fired_THEN_network_exception_is_thrown() = runBlocking<Unit> {
        mockWebServer.enqueueRequest(200, "{}")

        gitHubFollowersService.get("alma")
    }

    @Test(timeout = 20000L, expected = NetworkException::class)
    fun GIVEN_internal_server_error_WHEN_the_request_is_fired_THEN_network_exception_is_thrown() = runBlocking<Unit> {
        mockWebServer.enqueueRequest(500, "[]")

        gitHubFollowersService.get("alma")
    }
}