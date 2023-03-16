package io.outblock.lilico.page.send.transaction.subpage.amount.widget

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import io.outblock.lilico.manager.coin.FlowCoinListManager
import io.outblock.lilico.page.send.transaction.subpage.amount.SendAmountViewModel
import io.outblock.lilico.utils.findActivity
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.popupMenu
import io.outblock.lilico.utils.uiScope
import io.outblock.lilico.widgets.popup.PopupListView

class SendCoinPopupMenu(
    private val view: View,
) {
    private val viewModel by lazy { ViewModelProvider(findActivity(view) as FragmentActivity)[SendAmountViewModel::class.java] }

    fun show() {
        ioScope {
            val coinList = FlowCoinListManager.getEnabledCoinList()
            uiScope {
                popupMenu(
                    view,
                    items = coinList.map { PopupListView.ItemData(it.name, iconUrl = it.icon) },
                    selectListener = { _, text -> onMenuItemClick(text) },
                ).show()
            }
        }
    }

    private fun onMenuItemClick(text: String): Boolean {
        FlowCoinListManager.coinList().firstOrNull { it.name.lowercase() == text.lowercase() }?.let { viewModel.changeCoin(it) }
        return true
    }

}