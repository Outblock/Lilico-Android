package io.outblock.lilico.page.receive

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.page.receive.model.ReceiveData
import io.outblock.lilico.utils.ScreenUtils
import io.outblock.lilico.utils.extensions.dp2px
import io.outblock.lilico.utils.toQRBitmap
import io.outblock.lilico.utils.viewModelIOScope
import io.outblock.lilico.wallet.toAddress

class ReceiveViewModel : ViewModel() {

    private val size by lazy { ScreenUtils.getScreenWidth() - (61 * 2).dp2px().toInt() }

    val qrcodeLiveData = MutableLiveData<Bitmap>()
    val walletLiveData = MutableLiveData<ReceiveData>()

    fun load() {
        viewModelIOScope(this) {
            val wallet = walletCache().read()?.primaryWallet() ?: return@viewModelIOScope
            walletLiveData.postValue(ReceiveData(walletName = wallet.name, address = wallet.blockchain.first().address))

            val bitmap = wallet.blockchain.first().address.toAddress().toQRBitmap(width = size, height = size) ?: return@viewModelIOScope
            qrcodeLiveData.postValue(bitmap)
        }
    }

}