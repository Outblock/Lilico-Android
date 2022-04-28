package io.outblock.lilico.utils.extensions

import java.net.URLEncoder


fun String.indexOfAll(s: String): List<Int> {
    val result = mutableListOf<Int>()
    var index: Int = indexOf(s)
    while (index >= 0) {
        println(index)
        result.add(index)
        index = indexOf(s, index + 1)
    }
    return result
}

fun String.capitalizeV2(): String = replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

fun String.removeUrlParams(): String {
    return this.split("?")[0]
}

fun String.urlEncode(): String = URLEncoder.encode(this)