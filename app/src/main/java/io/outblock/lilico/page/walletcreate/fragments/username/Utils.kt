package io.outblock.lilico.page.walletcreate.fragments.username

import io.outblock.lilico.R
import io.outblock.lilico.utils.extensions.res2String

private const val USERNAME_MAX_SIZE = 16
private const val USERNAME_MIN_SIZE = 3

fun usernameVerify(username: String): String? {
    return when {
        username.length < USERNAME_MIN_SIZE -> R.string.username_too_short.res2String()
        username.length > USERNAME_MAX_SIZE -> R.string.username_too_long.res2String()
        else -> null
    }
}