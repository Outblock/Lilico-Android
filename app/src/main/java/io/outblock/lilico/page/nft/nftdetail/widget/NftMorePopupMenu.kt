package io.outblock.lilico.page.nft.nftdetail.widget

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import io.outblock.lilico.R
import io.outblock.lilico.manager.wallet.WalletManager
import io.outblock.lilico.network.model.Nft
import io.outblock.lilico.page.browser.openBrowser
import io.outblock.lilico.page.nft.nftdetail.NftDetailViewModel
import io.outblock.lilico.page.nft.nftlist.cover
import io.outblock.lilico.page.nft.nftlist.video
import io.outblock.lilico.page.nft.nftlist.websiteUrl
import io.outblock.lilico.utils.downloadToGallery
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.findActivity
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.popupMenu
import io.outblock.lilico.utils.uiScope
import io.outblock.lilico.widgets.popup.PopupListView

class NftMorePopupMenu(
    private val nft: Nft,
    private val view: View,
    private val color: Int,
) {
    private val viewModel by lazy { ViewModelProvider(findActivity(view) as FragmentActivity)[NftDetailViewModel::class.java] }

    fun show() {
        uiScope {
            popupMenu(
                view,
                items = listOf(
                    PopupListView.ItemData(R.string.download.res2String(), iconRes = R.drawable.ic_download, iconTint = color),
                    PopupListView.ItemData(R.string.view_on_web.res2String(), iconRes = R.drawable.ic_web, iconTint = color),
                ),
                selectListener = { _, text -> onMenuItemClick(text) },
            ).show()
        }
    }

    private fun onMenuItemClick(text: String): Boolean {
        when (text) {
            R.string.download.res2String() -> downloadNftMedia()
            R.string.view_on_web.res2String() -> openNftWebsite()
        }
        return true
    }

    private fun downloadNftMedia() {
        val media = nft.video() ?: nft.cover()
        media?.downloadToGallery(R.string.saved_to_album.res2String())
    }

    private fun openNftWebsite() {
        ioScope {
            val address = WalletManager.wallet()?.walletAddress().orEmpty()
            uiScope {
                openBrowser(
                    findActivity(view)!!,
                    url = nft.websiteUrl(address)
                )
            }
        }
    }
}
