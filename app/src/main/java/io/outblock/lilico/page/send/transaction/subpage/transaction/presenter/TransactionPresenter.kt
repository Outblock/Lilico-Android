package io.outblock.lilico.page.send.transaction.subpage.transaction.presenter

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProvider
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.cache.recentTransactionCache
import io.outblock.lilico.databinding.DialogSendConfirmBinding
import io.outblock.lilico.network.model.AddressBookContactBookList
import io.outblock.lilico.page.main.MainActivity
import io.outblock.lilico.page.send.transaction.subpage.bindUserInfo
import io.outblock.lilico.page.send.transaction.subpage.transaction.TransactionDialog
import io.outblock.lilico.page.send.transaction.subpage.transaction.TransactionViewModel
import io.outblock.lilico.page.send.transaction.subpage.transaction.model.TransactionDialogModel
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.utils.formatPrice
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.uiScope

class TransactionPresenter(
    private val fragment: TransactionDialog,
    private val binding: DialogSendConfirmBinding,
) : BasePresenter<TransactionDialogModel> {

    private val viewModel by lazy { ViewModelProvider(fragment)[TransactionViewModel::class.java] }

    private val transaction by lazy { viewModel.transaction }
    private val contact by lazy { viewModel.transaction.target }

    init {
        binding.sendButton.setOnProcessing { viewModel.send() }
        binding.amountWrapper.setVisible()
    }

    override fun bind(model: TransactionDialogModel) {
        model.userInfo?.let {
            binding.bindUserInfo(it, contact)
            setupAmount()
        }
        model.amountConvert?.let { updateAmountConvert(it) }
        model.isSendSuccess?.let { updateSendState(it) }
    }

    private fun updateSendState(isSuccess: Boolean) {
        if (isSuccess) {
            ioScope {
                val recentCache = recentTransactionCache().read() ?: AddressBookContactBookList(emptyList())
                val list = recentCache.contacts.orEmpty().toMutableList()
                list.removeAll { it.address == transaction.target.address }
                list.add(0, transaction.target)
                recentCache.contacts = list
                recentTransactionCache().cache(recentCache)
                uiScope { MainActivity.launch(fragment.requireContext()) }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupAmount() {
        binding.amountView.text = "${transaction.amount.formatPrice()} Flow"
    }

    private fun updateAmountConvert(amountConvert: Float) {
        binding.amountConvertView.text = "≈ \$ ${amountConvert.formatPrice()}"
    }
}