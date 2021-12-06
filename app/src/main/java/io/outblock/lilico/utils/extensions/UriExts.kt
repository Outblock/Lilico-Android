package io.outblock.lilico.utils.extensions

import android.graphics.drawable.Drawable
import android.net.Uri
import io.outblock.lilico.utils.Env
import io.outblock.lilico.utils.logd


fun Uri?.getDrawable(): Drawable? {
    this ?: return null
    logd("UriExts", "getDrawable():$this")
//    Env.getApp().contentResolver.openInputStream(this).use {
//        logd("UriExts", "stream:$it")
//        return Drawable.createFromStream(it, null)
//    }
    return Drawable.createFromStream(Env.getApp().contentResolver.openInputStream(this), null)
}