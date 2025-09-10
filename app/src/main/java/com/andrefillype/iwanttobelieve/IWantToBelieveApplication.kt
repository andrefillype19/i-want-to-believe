package com.andrefillype.iwanttobelieve

import android.app.Application
import com.andrefillype.iwanttobelieve.di.module.AppContainer
import com.google.firebase.FirebaseApp

class IWantToBelieveApplication : Application() {

    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()

        appContainer = AppContainer(context = this)
    }
}

val android.content.Context.appContainer: AppContainer
    get() = (applicationContext as IWantToBelieveApplication).appContainer