package io.outblock.lilico.firebase

import android.app.Application
import com.google.firebase.BuildConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import io.outblock.lilico.firebase.messaging.uploadPushToken


fun firebaseInformationCheck() {
    uploadPushToken()
}

fun firebaseInitialize(application: Application) {
    FirebaseApp.initializeApp(application)
    setupAppCheck()
}

private fun setupAppCheck() {
    FirebaseAppCheck.getInstance().apply {
        installAppCheckProviderFactory(SafetyNetAppCheckProviderFactory.getInstance())
        if (BuildConfig.DEBUG) {
            installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance())
            installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance())
        } else {
            installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance())
        }
    }
}