package com.possible.demo.data.shared

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer

/**
 * Enqueue a [MockResponse] with the given [bodyJson] as [MockResponse.body] and given [responseCode] as [MockResponse.setResponseCode]
 */
fun MockWebServer.enqueueRequest(responseCode: Int = 200, bodyJson: String) =
        enqueue(MockResponse().apply {
            setBody(bodyJson)
            setResponseCode(responseCode)
        })