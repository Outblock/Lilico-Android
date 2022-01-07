package io.outblock.lilico.utils.extensions


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