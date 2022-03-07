package io.outblock.lilico.page.nftdetail

import android.view.View
import android.view.ViewGroup
import androidx.core.view.drawToBitmap
import io.outblock.lilico.cache.userInfoCache
import io.outblock.lilico.network.model.Nft
import io.outblock.lilico.page.nftdetail.widget.NftShareView
import io.outblock.lilico.utils.*
import kotlinx.coroutines.delay
import java.io.File


fun shareNft(wrapper: ViewGroup, nft: Nft, onReady: (file: File) -> Unit) {
    ioScope {
        val userInfo = userInfoCache().read() ?: return@ioScope
        uiScope {
            val view = NftShareView(wrapper.context, userInfo, nft)
            wrapper.removeAllViews()
            wrapper.addView(view)
            view.setup {
                ioScope {
                    delay(1000)
                    logd("shareNft", "drawToBitmap start")
                    val bitmap = it.drawToBitmap()
                    val file = File(CACHE_PATH, "nft_share.jpg")
                    bitmap.saveToFile(file)
                    uiScope { onReady.invoke(file) }
                    logd("shareNft", "shareNft save bitmap finish")
                }
            }
        }
    }
}

private fun layoutView(v: View) {
    v.layout(0, 0, ScreenUtils.getScreenWidth(), ScreenUtils.getScreenHeight())
    val measuredWidth: Int = View.MeasureSpec.makeMeasureSpec(ScreenUtils.getScreenWidth(), View.MeasureSpec.EXACTLY)
    val measuredHeight: Int = View.MeasureSpec.makeMeasureSpec(10000, View.MeasureSpec.AT_MOST)
    v.measure(measuredWidth, measuredHeight)
    v.layout(0, 0, v.measuredWidth, v.measuredHeight)
}