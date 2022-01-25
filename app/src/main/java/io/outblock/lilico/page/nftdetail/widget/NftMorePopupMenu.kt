package io.outblock.lilico.page.nftdetail.widget

import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import io.outblock.lilico.R
import io.outblock.lilico.page.nftdetail.NftDetailViewModel
import io.outblock.lilico.utils.findActivity
import io.outblock.lilico.utils.generateMenuItem
import io.outblock.lilico.utils.uiScope

class NftMorePopupMenu(
    private val view: View,
    private val color: Int,
) {
    private val viewModel by lazy { ViewModelProvider(findActivity(view) as FragmentActivity)[NftDetailViewModel::class.java] }

    fun show() {
        uiScope {
            val popup = PopupMenu(view.context, view)
            popup.menu.add(generateMenuItem(R.string.download, R.drawable.ic_download, color))
            popup.menu.add(generateMenuItem(R.string.view_on_web, R.drawable.ic_web, color))
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