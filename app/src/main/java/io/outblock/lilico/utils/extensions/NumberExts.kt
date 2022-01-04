package io.outblock.lilico.utils.extensions

fun Int?.orZero() = this ?: 0

fun Double?.orZero() = this ?: 0.0

fun Float?.orZero() = this ?: 0f

fun String?.toSafeInt(default: Int = 0): Int {
    if (this.isNullOrBlank()) {
        return default
    }

    return this.toIntOrNull() ?: default
}

fun String?.toSafeLong(default: Long = 0): Long {
    if (this.isNullOrBlank()) {
        return default
    }

    return this.toLongOrNull() ?: default
}