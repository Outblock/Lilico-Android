package io.outblock.lilico.page.nft.widget

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import io.outblock.lilico.R
import io.outblock.lilico.network.model.Nft
import io.outblock.lilico.page.nft.NFTFragmentViewModel
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.findActivity
import io.outblock.lilico.utils.popupMenu
import io.outblock.lilico.utils.uiScope

class NftItemPopupMenu(
    private val view: View,
    val nft: Nft,
) {
    private val viewModel by lazy { ViewModelProvider(findActivity(view) as FragmentActivity)[NFTFragmentViewModel::class.java] }

    fun show() {
        uiScope {
            popupMenu(
                view,
                titles = listOf(
                    R.string.top_selection.res2String(),
                    R.string.share.res2String(),
                    R.string.send.res2String(),
                ),
                icons = listOf(
                    R.drawable.ic_selection_star,
                    R.drawable.ic_share,
                    R.drawable.ic_send_simple,
                ),
                selectListener = { _, text -> onMenuItemClick(text) },
            ).show()
        }
    }

    private fun onMenuItemClick(text: String): Boolean {
        when (text) {
            R.id.action_select.res2String() -> {}
            R.id.action_share.res2String() -> {}
            R.id.action_send.res2String() -> {}
        }
        return true
    }
}