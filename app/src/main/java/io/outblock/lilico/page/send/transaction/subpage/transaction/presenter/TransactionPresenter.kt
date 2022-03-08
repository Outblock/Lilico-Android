package io.outblock.lilico.page.send.transaction.subpage.transaction.presenter

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.cache.recentTransactionCache
import io.outblock.lilico.databinding.DialogSendConfirmBinding
import io.outblock.lilico.network.model.AddressBookContactBookList
import io.outblock.lilico.network.model.AddressBookDomain
import io.outblock.lilico.network.model.UserInfoData
import io.outblock.lilico.page.main.MainActivity
import io.outblock.lilico.page.send.transaction.subpage.transaction.TransactionDialog
import io.outblock.lilico.page.send.transaction.subpage.transaction.TransactionViewModel
import io.outblock.lilico.page.send.transaction.subpage.transaction.model.TransactionDialogModel
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.utils.formatPrice
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.loadAvatar
import io.outblock.lilico.utils.uiScope
import io.outblock.lilico.wallet.toAddress

class TransactionPresenter(
    private val fragment: TransactionDialog,
    private val binding: DialogSendConfirmBinding,
) : BasePresenter<TransactionDialogModel> {

    private val viewModel by lazy { ViewModelProvider(fragment)[TransactionViewModel::class.java] }

    private val transaction by lazy { viewModel.transaction }
    private val contact by lazy { viewModel.transaction.target }


    init {
        binding.sendButton.setOnClickListener {
            binding.sendButton.setProgressVisible(true)
            viewModel.send()
        }
        binding.amountWrapper.setVisible()
    }

    override fun bind(model: TransactionDialogModel) {
        model.userInfo?.let {
            setupUserInfo(it)
            setupAmount()
        }
        model.amountConvert?.let { updateAmountConvert(it) }
        model.isSendSuccess?.let { updateSendState(it) }
    }

    @SuppressLint("SetTextI18n")
    private fun setupUserInfo(userInfo: UserInfoData) {
        with(binding) {
            fromAvatarView.loadAvatar(userInfo.avatar)
            fromNameView.text = userInfo.nickname
            fromAddressView.text = "(${userInfo.address?.toAddress()})"

            toNameView.text = "${contact.name()} ${if (!contact.username.isNullOrEmpty()) "  (@${contact.username})" else ""}"
            namePrefixView.text = contact.prefixName()
            namePrefixView.setVisible(contact.prefixName().isNotEmpty())

            if (contact.domain?.domainType ?: 0 == 0) {
                toAvatarView.setVisible(!contact.avatar.isNullOrEmpty(), invisible = true)
                toAvatarView.loadAvatar(contact.avatar.orEmpty())
            } else {
                val avatar =
                    if (contact.domain?.domainType == AddressBookDomain.DOMAIN_FIND_XYZ) R.drawable.ic_domain_logo_findxyz else R.drawable.ic_domain_logo_flowns
                toAvatarView.setVisible(true)
                Glide.with(toAvatarView).load(avatar).into(toAvatarView)
            }

            toAddressView.text = "(${contact.address})"
        }
    }

    private fun updateSendState(isSuccess: Boolean) {
        binding.sendButton.setProgressVisible(false)
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