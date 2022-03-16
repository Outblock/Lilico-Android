package io.outblock.lilico.utils

import android.os.Looper
import android.widget.Toast
import androidx.annotation.StringRes
import io.outblock.lilico.utils.extensions.res2String

fun toast(@StringRes msgRes: Int = 0, msg: String? = null, duration: Int = Toast.LENGTH_SHORT) {

    fun toastInternal() {
        Toast.makeText(Env.getApp(), if (msgRes == 0) msg.orEmpty() else msgRes.res2String(), duration).show()
    }

    if (Looper.myLooper() == Looper.getMainLooper()) {
        toastInternal()
    } else {
        uiScope { toastInternal() }
    }
}
