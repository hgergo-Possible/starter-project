package com.possible.demo

import com.possible.demo.di.NetworkModule
import com.possible.demo.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication

class DemoApp : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> =
            DaggerAppComponent.builder()
                    .setApplication(this)
                    .setCoreModule(NetworkModule("https://api.github.com/"))
                    .build()

}