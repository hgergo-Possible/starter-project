package com.possible.demo.di

import com.possible.demo.feature.repositories.RepositoriesFragment
import com.possible.demo.feature.search.SearchUsersFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Module to define injections to fragments.
 */
@Module
abstract class FragmentBuilderModule {

    @ContributesAndroidInjector
    internal abstract fun provideSearchUsersFragmentFactory(): SearchUsersFragment

    @ContributesAndroidInjector
    internal abstract fun provideRepositoriesFragmentFactory(): RepositoriesFragment
}