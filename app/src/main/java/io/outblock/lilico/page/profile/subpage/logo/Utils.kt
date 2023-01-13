package io.outblock.lilico.page.profile.subpage.logo

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import io.outblock.lilico.utils.loge
import io.outblock.lilico.utils.logw

const val LILICO_LOGO_DEFAULT = "io.outblock.lilico.page.profile.subpage.logo.pages.LilicoLogoDefault"
const val LILICO_LOGO1 = "io.outblock.lilico.page.profile.subpage.logo.pages.LilicoLogo1"
const val LILICO_LOGO2 = "logo2"
const val LILICO_LOGO3 = "logo3"
const val LILICO_LOGO4 = "logo4"

private val logos = listOf(
    LILICO_LOGO1,
//    LILICO_LOGO2,
//    LILICO_LOGO3,
//    LILICO_LOGO4,
    LILICO_LOGO_DEFAULT,
)

fun changeAppIcon(context: Context, logo: String) {
    logw("xxx", "logos:$logos , logo:$logo")

    context.setComponentEnabledSetting(logo, true)
    logos.filter { it != logo }.forEach { context.setComponentEnabledSetting(it, false) }
}

private fun Context.setComponentEnabledSetting(cls: String, isEnable: Boolean) {
    val componentName = ComponentName(this, cls)

    val state: Int = packageManager.getComponentEnabledSetting(componentName)
    val isSystemEnabled =
        (state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) || (state != PackageManager.COMPONENT_ENABLED_STATE_DISABLED && cls == LILICO_LOGO_DEFAULT)

    logw("xxx", "cls = $cls, isEnable = $isEnable, state = $state, isSystemEnabled = $isSystemEnabled")

    if (isSystemEnabled == isEnable) return


    loge("xxx", "set cls = $cls to $isEnable")
    packageManager.setComponentEnabledSetting(
        componentName,
        if (isEnable) {
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        } else {
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        },
        PackageManager.DONT_KILL_APP
    )
}