package com.possible.demo.data.shared

import kotlinx.coroutines.CancellationException

/**
 * Helper function to wrap all network exceptions into one so it's easier to handle
 */
fun wrapExceptionIfNeeded(throwable: Throwable): Throwable =
        throwable as? CancellationException ?: NetworkException(throwable)