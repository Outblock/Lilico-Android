package io.outblock.lilico.page.component.deeplinking

import android.net.Uri
import io.outblock.lilico.manager.walletconnect.WalletConnect
import io.outblock.lilico.utils.logd
import java.net.URLDecoder

private const val TAG = "DeepLinkingDispatch"

fun dispatchDeepLinking(uri: Uri) {
    val host = uri.host ?: return
    if (!host.contains("lilico.app")) {
        return
    }

    if (dispatchWalletConnect(uri)) return
}

// https://lilico.app/?uri=wc%3A83ba9cb3adf9da4b573ae0c499d49be91995aa3e38b5d9a41649adfaf986040c%402%3Frelay-protocol%3Diridium%26symKey%3D618e22482db56c3dda38b52f7bfca9515cc307f413694c1d6d91931bbe00ae90
// wc:83ba9cb3adf9da4b573ae0c499d49be91995aa3e38b5d9a41649adfaf986040c@2?relay-protocol=iridium&symKey=618e22482db56c3dda38b52f7bfca9515cc307f413694c1d6d91931bbe00ae90
private fun dispatchWalletConnect(uri: Uri): Boolean {
    return runCatching {
        val data = URLDecoder.decode(uri.getQueryParameter("uri"), "UTF-8")
        logd(TAG, "dispatchWalletConnect: $data")
        if (data.startsWith("wc:")) {
            WalletConnect.get().pair(data)
        }
    }.getOrNull() != null
}