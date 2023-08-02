package io.outblock.lilico.page.send.nft.confirm.presenter

import androidx.lifecycle.ViewModelProvider
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.cache.recentTransactionCache
import io.outblock.lilico.databinding.DialogSendConfirmBinding
import io.outblock.lilico.network.model.AddressBookContactBookList
import io.outblock.lilico.page.main.MainActivity
import io.outblock.lilico.page.send.nft.confirm.NftSendConfirmDialog
import io.outblock.lilico.page.send.nft.confirm.NftSendConfirmViewModel
import io.outblock.lilico.page.send.nft.confirm.model.NftSendConfirmDialogModel
import io.outblock.lilico.page.send.transaction.subpage.bindNft
import io.outblock.lilico.page.send.transaction.subpage.bindUserInfo
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.uiScope

class NftSendConfirmPresenter(
    private val fragment: NftSendConfirmDialog,
    private val binding: DialogSendConfirmBinding,
) : BasePresenter<NftSendConfirmDialogModel> {

    private val viewModel by lazy { ViewModelProvider(fragment)[NftSendConfirmViewModel::class.java] }

    private val sendModel by lazy { viewModel.nft }
    private val contact by lazy { viewModel.nft.target }

    init {
        binding.sendButton.button().setOnProcessing { viewModel.send() }
        binding.nftWrapper.setVisible()
        binding.titleView.setText(R.string.send_nft)
    }

    override fun bind(model: NftSendConfirmDialogModel) {
        model.userInfo?.let {
            binding.bindUserInfo(it, contact)
            binding.bindNft(sendModel.nft)
        }
        model.isSendSuccess?.let { updateSendState(it) }
    }

    private fun updateSendState(isSuccess: Boolean) {
        if (isSuccess) {
            ioScope {
                val recentCache = recentTransactionCache().read() ?: AddressBookContactBookList(emptyList())
                val list = recentCache.contacts.orEmpty().toMutableList()
                list.removeAll { it.address == sendModel.target.address }
                list.add(0, sendModel.target)
                recentCache.contacts = list
                recentTransactionCache().cache(recentCache)
                uiScope { MainActivity.launch(fragment.requireContext()) }
            }
        }
    }
}