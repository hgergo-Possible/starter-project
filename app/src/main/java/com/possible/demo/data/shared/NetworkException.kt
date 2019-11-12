package com.possible.demo.data.shared

/**
 * Exception representing all network related exceptions, no-internet, failing request, failing parsing.
 */
class NetworkException(cause: Throwable? = null) : RuntimeException(cause)