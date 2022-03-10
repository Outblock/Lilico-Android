package io.outblock.lilico.base.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.outblock.lilico.firebase.firebaseInformationCheck
import java.lang.ref.WeakReference

open class BaseActivity : AppCompatActivity() {
    private var firstVisible = true

    override fun onCreate(savedInstanceState: Bundle?) {
        currentActivity = WeakReference(this)
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        firebaseInformationCheck()
        currentActivity = WeakReference(this)
        super.onResume()
        if (firstVisible) {
            onFirstVisible()
        } else {
            onRevisible()
        }
        firstVisible = false
    }

    override fun onDestroy() {
        super.onDestroy()
        if (currentActivity?.get() == this) {
            currentActivity = null
        }
    }

    open fun onFirstVisible() {}

    open fun onRevisible() {}

    fun isFirstVisible() = firstVisible

    companion object {
        private var currentActivity: WeakReference<BaseActivity>? = null

        fun getCurrentActivity(): BaseActivity? {
            return currentActivity?.get()
        }
    }
}