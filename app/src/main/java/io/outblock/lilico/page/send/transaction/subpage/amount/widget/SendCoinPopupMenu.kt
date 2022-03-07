package io.outblock.lilico.page.send.transaction.subpage.amount.widget

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import io.outblock.lilico.page.send.transaction.subpage.amount.SendAmountViewModel
import io.outblock.lilico.utils.*

class SendCoinPopupMenu(
    private val view: View,
) {
    private val viewModel by lazy { ViewModelProvider(findActivity(view) as FragmentActivity)[SendAmountViewModel::class.java] }

    fun show() {
        ioScope {
            val coinList = generateCoinList()
            uiScope {
                popupMenu(
                    view,
                    titles = coinList.map { it.name() },
                    icons = coinList.map { it.icon() },
                    selectListener = { _, text -> onMenuItemClick(text) },
                ).show()
            }
        }
    }

    private fun generateCoinList(): List<Coin> {
        return listOf(Coin.FLOW, Coin.FUSD)
    }

    private fun onMenuItemClick(text: String): Boolean {
        when (text) {
            Coin.FUSD.name() -> viewModel.changeCoin(Coin.FUSD)
            Coin.FLOW.name() -> viewModel.changeCoin(Coin.FLOW)
        }
        return true
    }

}