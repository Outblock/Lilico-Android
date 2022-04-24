package io.outblock.lilico.manager.app

import android.app.Activity
import android.app.Application
import android.os.Bundle
import io.outblock.lilico.page.window.sendstate.SendStateBubble

class PageLifecycleObserver : Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
        SendStateBubble.show(activity)
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
        fun init(application: Application) {
            application.registerActivityLifecycleCallbacks(PageLifecycleObserver())
        }
    }
}