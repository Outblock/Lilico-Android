package io.outblock.lilico.widgets.floatwindow

import android.app.Activity
import android.app.Application
import android.os.Bundle

internal class FloatWindowPageObserver : Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
        FloatWindow.onPageChange(activity)
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }

    companion object {
        private var isRegistered = false

        fun register(application: Application) {
            if (!isRegistered) {
                application.registerActivityLifecycleCallbacks(FloatWindowPageObserver())
            }
            isRegistered = true
        }
    }
}