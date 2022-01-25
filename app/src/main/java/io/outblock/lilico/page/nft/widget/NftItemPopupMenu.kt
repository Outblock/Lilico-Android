package io.outblock.lilico.page.nft.widget

import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import io.outblock.lilico.R
import io.outblock.lilico.network.model.Nft
import io.outblock.lilico.page.nft.NFTFragmentViewModel
import io.outblock.lilico.utils.findActivity
import io.outblock.lilico.utils.generateMenuItem
import io.outblock.lilico.utils.uiScope

class NftItemPopupMenu(
    private val view: View,
    val nft: Nft,
) {
    private val viewModel by lazy { ViewModelProvider(findActivity(view) as FragmentActivity)[NFTFragmentViewModel::class.java] }

    fun show() {
        uiScope {
            val popup = PopupMenu(view.context, view)
            popup.menu.add(generateMenuItem(R.string.top_selection, R.drawable.ic_selection_star))
            popup.menu.add(generateMenuItem(R.string.share, R.drawable.ic_share))
            popup.menu.add(generateMenuItem(R.string.send, R.drawable.ic_send_simple))
            popup.setOnMenuItemClickListener {
                onMenuItemClick(it)
            }
            popup.show()
        }
    }

    private fun onMenuItemClick(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.action_select -> {}
            R.id.action_share -> {}
            R.id.action_send -> {}
        }
        return true
    }

}