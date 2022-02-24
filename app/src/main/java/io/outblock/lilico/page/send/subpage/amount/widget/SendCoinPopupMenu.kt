package io.outblock.lilico.page.send.subpage.amount.widget

import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import io.outblock.lilico.page.send.subpage.amount.SendAmountViewModel
import io.outblock.lilico.utils.*
import io.outblock.lilico.utils.extensions.dp2px

class SendCoinPopupMenu(
    private val view: View,
) {
    private val viewModel by lazy { ViewModelProvider(findActivity(view) as FragmentActivity)[SendAmountViewModel::class.java] }

    fun show() {
        ioScope {
            val coinList = generateCoinList()
            uiScope {
                val popup = PopupMenu(view.context, view)
                val iconSize = 22.dp2px().toInt()
                coinList.forEach { coin ->
                    popup.menu.add(generateMenuItem(coin.name(), coin.icon(), maxIconWidth = iconSize, maxIconHeight = iconSize))
                }
                popup.setOnMenuItemClickListener {
                    onMenuItemClick(it)
                }
                popup.show()
            }
        }
    }

    private fun generateCoinList(): List<Coin> {
        return listOf(Coin.FLOW, Coin.FUSD)
    }

    private fun onMenuItemClick(menuItem: MenuItem): Boolean {
        when (menuItem.title) {
            Coin.FUSD.name() -> viewModel.changeCoin(Coin.FUSD)
            Coin.FLOW.name() -> viewModel.changeCoin(Coin.FLOW)
        }
        return true
    }

}