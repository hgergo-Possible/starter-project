package com.possible.demo.feature.shared

import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.navigation.NavArgs
import androidx.navigation.NavArgsLazy

/**
 * Helper function returning a delegate to make it easier to use in fragments.
 *
 * Note: [androidx.navigation.fragment.navArgs] defines a function like this, but it's build with java 1.6 so it cannot be inlined.
 */
@MainThread
inline fun <reified Args : NavArgs> Fragment.navArgs() = NavArgsLazy(Args::class) {
    arguments ?: throw IllegalStateException("Fragment $this has null arguments")
}