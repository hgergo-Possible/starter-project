package com.possible.demo.data.shared

import javax.inject.Inject

/**
 * Implementation of [Clock] which returns the [System.currentTimeMillis]
 */
class ClockImpl @Inject constructor() : Clock {

    override val currentTimeInMillis: Long get() = System.currentTimeMillis()

}