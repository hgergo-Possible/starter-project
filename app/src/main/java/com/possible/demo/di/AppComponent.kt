package com.possible.demo.di

import android.app.Application
import com.possible.demo.DemoApp
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

/**
 * The Base Component of the whole application.
 */
@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    NetworkModule::class,
    ViewModelModule::class,
    ActivityBuilderModule::class]
)
interface AppComponent : AndroidInjector<DemoApp> {

    override fun inject(app: DemoApp)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun setApplication(application: Application): Builder

        fun setCoreModule(networkModule: NetworkModule): Builder

        fun build(): AppComponent
    }
}