package io.outblock.lilico.utils


fun String.toCoverUrl(): String {
    if (startsWith("ipfs://")) {
        return replace("ipfs://", "https://ipfs.io/ipfs/")
    }
    return this
}