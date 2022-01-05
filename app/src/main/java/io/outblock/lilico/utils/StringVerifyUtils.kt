package io.outblock.lilico.utils

import io.outblock.lilico.R
import io.outblock.lilico.utils.extensions.res2String


private val PASSWORD_REGEX by lazy { Regex("^(?=.*[A-Za-z])[A-Za-z\\d@\$!%*#?&]{6,}\$") }
private val REGEX_CONTAINS_NUMBER by lazy { Regex(".*\\d.*") }
private val REGEX_CONTAINS_CHARACTER by lazy { Regex(".*[A-Za-z].*") }

private val USERNAME_REGEX by lazy { Regex("^[A-Za-z0-9_]{3,15}\$") }

private const val USERNAME_MAX_SIZE = 15
private const val USERNAME_MIN_SIZE = 3

fun verifyPassword(password: String): String? {
    if (password.isEmpty()) {
        return R.string.password_verify_format.res2String()
    }

    if (!REGEX_CONTAINS_CHARACTER.matches(password)) {
        return R.string.password_verify_no_number.res2String()
    }

    if (!PASSWORD_REGEX.matches(password)) {
        return "Wrong format"
    }

    return null
}


fun usernameVerify(username: String): String? {
    return when {
        username.length < USERNAME_MIN_SIZE -> R.string.username_too_short.res2String()
        username.length > USERNAME_MAX_SIZE -> R.string.username_too_long.res2String()
        !USERNAME_REGEX.matches(username) -> "Wrong format"
        else -> null
    }
}