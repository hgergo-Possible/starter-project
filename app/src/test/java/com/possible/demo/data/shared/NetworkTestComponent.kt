package com.possible.demo.data.shared

import com.possible.demo.data.service.GitHubFollowersServiceTest
import com.possible.demo.data.service.GitHubRepoServiceTest
import com.possible.demo.di.NetworkModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class])
interface NetworkTestComponent {

    fun inject(gitHubRepoServiceTest: GitHubRepoServiceTest)
    fun inject(gitHubUserRepositoryTest: GitHubFollowersServiceTest)

    @Component.Builder
    interface Builder {

        fun setCoreModule(networkModule: NetworkModule): Builder

        fun build(): NetworkTestComponent
    }
}