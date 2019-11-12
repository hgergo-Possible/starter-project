package com.possible.demo.feature

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

fun <T> LiveData<T>.observeNextValue(timeoutInMillis: Long = 200L) : T?{
    val mockLifeCycleOwner = mock<LifecycleOwner>()
    val lifecycle = LifecycleRegistry(mock())
    lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    whenever(mockLifeCycleOwner.lifecycle).doReturn(lifecycle)

    var value: T? = null
    val latch = CountDownLatch(1)
    observe(mockLifeCycleOwner, Observer {
        value = it
        latch.countDown()
    })
    latch.await(timeoutInMillis, TimeUnit.MILLISECONDS)

    return value
}