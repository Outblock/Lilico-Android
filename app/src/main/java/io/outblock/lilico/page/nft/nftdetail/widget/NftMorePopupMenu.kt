package io.outblock.lilico.page.nft.nftdetail.widget

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import io.outblock.lilico.R
import io.outblock.lilico.page.nft.nftdetail.NftDetailViewModel
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.findActivity
import io.outblock.lilico.utils.popupMenu
import io.outblock.lilico.utils.uiScope
import io.outblock.lilico.widgets.popup.PopupListView

class NftMorePopupMenu(
    private val view: View,
    private val color: Int,
) {
    private val viewModel by lazy { ViewModelProvider(findActivity(view) as FragmentActivity)[NftDetailViewModel::class.java] }

    fun show() {
        uiScope {
            popupMenu(
                view,
                items = listOf(
                    PopupListView.ItemData(R.string.download.res2String(), iconRes = R.drawable.ic_download),
                    PopupListView.ItemData(R.string.view_on_web.res2String(), iconRes = R.drawable.ic_web),
                ),
                selectListener = { _, text -> onMenuItemClick(text) },
            ).show()
        }
    }

    private fun onMenuItemClick(text: String): Boolean {
        when (text) {
            R.string.download.res2String() -> {}
            R.string.view_on_web.res2String() -> {}
        }
        return true
    }

}