package com.andrefillype.iwanttobelieve.di.module

import android.content.Context
import com.andrefillype.iwanttobelieve.data.remote.firebase.FirebaseAuthService
import com.andrefillype.iwanttobelieve.data.remote.firebase.FirestoreService

object FirebaseModule {

    fun provideFirebaseAuthService(context: Context): FirebaseAuthService {
        return FirebaseAuthService(context)
    }

    fun provideFirestoreService(): FirestoreService {
        return FirestoreService()
    }
}