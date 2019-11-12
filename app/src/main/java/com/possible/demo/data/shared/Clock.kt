package com.possible.demo.data.shared

/**
 * Interface to request the current time in UTC milliseconds
 */
interface Clock {

    val currentTimeInMillis: Long
}