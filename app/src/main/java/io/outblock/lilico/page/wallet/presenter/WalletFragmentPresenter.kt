package io.outblock.lilico.page.wallet.presenter

import android.graphics.Color
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.cache.userInfoCache
import io.outblock.lilico.databinding.FragmentWalletBinding
import io.outblock.lilico.firebase.analytics.reportEvent
import io.outblock.lilico.page.main.presenter.openDrawerLayout
import io.outblock.lilico.page.wallet.WalletFragmentViewModel
import io.outblock.lilico.page.wallet.adapter.WalletFragmentAdapter
import io.outblock.lilico.page.wallet.model.WalletCoinItemModel
import io.outblock.lilico.page.wallet.model.WalletFragmentModel
import io.outblock.lilico.utils.extensions.dp2px
import io.outblock.lilico.utils.extensions.res2color
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.loadAvatar
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.uiScope
import io.outblock.lilico.widgets.itemdecoration.ColorDividerItemDecoration

class WalletFragmentPresenter(
    private val fragment: Fragment,
    private val binding: FragmentWalletBinding,
) : BasePresenter<WalletFragmentModel> {

    private val recyclerView = binding.recyclerView
    private val adapter by lazy { WalletFragmentAdapter() }

    private val viewModel by lazy { ViewModelProvider(fragment.requireActivity())[WalletFragmentViewModel::class.java] }

    init {
        with(recyclerView) {
            this.adapter = this@WalletFragmentPresenter.adapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(ColorDividerItemDecoration(Color.TRANSPARENT, 10.dp2px().toInt(), LinearLayout.VERTICAL))
        }
        with(binding.refreshLayout) {
            setOnRefreshListener { viewModel.load() }
            setColorSchemeColors(R.color.colorSecondary.res2color())
        }

        binding.avatarView.setOnClickListener { openDrawerLayout(fragment.requireContext()) }
        bindAvatar()
    }

    override fun bind(model: WalletFragmentModel) {
        model.data?.let {
            reportEvent("wallet_coin_list_loaded", mapOf("count" to it.size.toString()))
            adapter.setNewDiffData(it)
            binding.refreshLayout.isRefreshing = false
            bindAvatar()
            it.forEach {
                if (it is WalletCoinItemModel) {
                    logd("xxxxxxx", "${it.coin.symbol}: ${it.currency},${it.balance}x${it.coinRate}")
                }
            }
        }
    }

    private fun bindAvatar() {
        ioScope {
            val userInfo = userInfoCache().read() ?: return@ioScope
            uiScope { binding.avatarView.loadAvatar(userInfo.avatar) }
        }
    }
}