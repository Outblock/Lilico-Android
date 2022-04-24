package io.outblock.lilico.widgets.floatview

import android.app.Service
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import io.outblock.lilico.utils.Env

object FloatWindow {
    private val windowManagers = mutableMapOf<String, WindowManager>()
    private val containerViews = mutableMapOf<String, View>()
    private val configs = mutableMapOf<String, FloatWindowConfig>()

    fun builder(): FloatWindowBuilder = FloatWindowBuilder()

    fun dismiss(tag: String) {
        windowManagers[tag]?.removeView(containerViews[tag])
        removeConfig(tag)
    }

    fun isShowing(tag: String): Boolean {
        return containerViews[tag] != null && windowManagers[tag] != null
    }

    fun updateLayout(tag: String, params: WindowManager.LayoutParams) {
        windowManagers[tag]?.apply {
            updateViewLayout(containerViews[tag], params)
        }
    }

    internal fun show(config: FloatWindowConfig) {
        if (containerViews[config.tag] != null && windowManagers[config.tag] != null) {
            return
        }

//        if (!OverflowPermissionUtils.checkPermission(Env.getApp())) {
//            return
//        }

        windowManagers[config.tag] = windowManagers[config.tag] ?: Env.getApp().getSystemService(Service.WINDOW_SERVICE) as WindowManager
        configs[config.tag] = config

        val contentView = config.contentView ?: LayoutInflater.from(Env.getApp()).inflate(config.layoutId, null)
        containerViews[config.tag] = contentView
        windowManagers[config.tag]?.apply {
            addView(contentView, createParams(config))
        }
    }

    private fun removeConfig(tag: String) {
        windowManagers.remove(tag)
        configs.remove(tag)
        containerViews.remove(tag)
    }
}

class FloatWindowBuilder {
    var config = FloatWindowConfig()

    fun setConfig(config: FloatWindowConfig) = apply { this.config = config }

    fun show() {
        FloatWindow.show(config)
    }
}