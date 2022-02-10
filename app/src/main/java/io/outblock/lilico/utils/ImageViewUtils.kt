package io.outblock.lilico.utils

import android.widget.ImageView
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.load
import com.bumptech.glide.Glide
import io.outblock.lilico.R


fun ImageView.loadAvatar(url: String, placeholderEnable: Boolean = true) {
    val avatar = url.parseAvatarUrl()
    logd("loadAvatar", avatar)
    if (avatar.contains("boringavatars.com")) {
        loadAvatarSvg(avatar, placeholderEnable)
    } else {
        loadAvatarNormal(avatar, placeholderEnable)
    }
}

private fun ImageView.loadAvatarNormal(url: String, placeholderEnable: Boolean = true) {
    var request = Glide.with(this).load(url)

    if (placeholderEnable) {
        request = request.placeholder(R.drawable.placeholder)
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