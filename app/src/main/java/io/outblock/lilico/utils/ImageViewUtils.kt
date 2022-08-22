package io.outblock.lilico.utils

import android.graphics.Bitmap
import android.widget.ImageView
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.load
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import io.outblock.lilico.R


fun ImageView.loadAvatar(url: String, placeholderEnable: Boolean = true, transformation: Transformation<Bitmap>? = null) {
    val avatar = url.parseAvatarUrl()
    logd("loadAvatar", avatar)
    if (avatar.contains("boringavatars.com") || avatar.contains("flovatar.com")) {
        loadAvatarSvg(avatar, placeholderEnable)
    } else {
        loadAvatarNormal(avatar, placeholderEnable, transformation)
    }
}

private fun ImageView.loadAvatarNormal(url: String, placeholderEnable: Boolean = true, transformation: Transformation<Bitmap>? = null) {
    var request = Glide.with(this).load(url)

    if (placeholderEnable) {
        request = request.placeholder(R.drawable.placeholder)
    }
    if (transformation != null) {
        request = request.transform(transformation)
    }
    request.into(this)
}

private fun ImageView.loadAvatarSvg(url: String, placeholderEnable: Boolean = true) {
    val loader = ImageLoader.Builder(context).componentRegistry {
        add(SvgDecoder(context))
    }.build()
    load(url, loader) {
        if (placeholderEnable) {
            placeholder(R.drawable.placeholder)
        }
    }
}