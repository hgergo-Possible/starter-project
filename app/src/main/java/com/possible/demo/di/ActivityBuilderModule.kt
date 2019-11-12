package com.possible.demo.di

import com.possible.demo.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Module to define injections to activities.
 */
@Module
abstract class ActivityBuilderModule {

    @ContributesAndroidInjector(modules = [FragmentBuilderModule::class])
    abstract fun bindMainActivity(): MainActivity
}