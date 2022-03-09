package io.outblock.lilico.page.send.nft.confirm.presenter

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.cache.recentTransactionCache
import io.outblock.lilico.databinding.DialogSendConfirmBinding
import io.outblock.lilico.manager.config.NftCollectionConfig
import io.outblock.lilico.network.model.AddressBookContactBookList
import io.outblock.lilico.network.model.AddressBookDomain
import io.outblock.lilico.network.model.UserInfoData
import io.outblock.lilico.page.main.MainActivity
import io.outblock.lilico.page.nft.cover
import io.outblock.lilico.page.nft.name
import io.outblock.lilico.page.send.nft.confirm.NftSendConfirmDialog
import io.outblock.lilico.page.send.nft.confirm.NftSendConfirmViewModel
import io.outblock.lilico.page.send.nft.confirm.model.NftSendConfirmDialogModel
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.loadAvatar
import io.outblock.lilico.utils.uiScope
import io.outblock.lilico.wallet.toAddress

class NftSendConfirmPresenter(
    private val fragment: NftSendConfirmDialog,
    private val binding: DialogSendConfirmBinding,
) : BasePresenter<NftSendConfirmDialogModel> {

    private val viewModel by lazy { ViewModelProvider(fragment)[NftSendConfirmViewModel::class.java] }

    private val sendModel by lazy { viewModel.nft }
    private val contact by lazy { viewModel.nft.target }

    init {
        binding.sendButton.setOnProcessing { viewModel.send() }
        binding.nftWrapper.setVisible()
        binding.titleView.setText(R.string.send_nft)
    }

    override fun bind(model: NftSendConfirmDialogModel) {
        model.userInfo?.let {
            setupUserInfo(it)
            setupNft()
        }
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

    private fun setupNft() {
        with(binding) {
            val nft = sendModel.nft
            val config = NftCollectionConfig.get(nft.contract.address) ?: return@with
            Glide.with(nftCover).load(nft.cover()).into(nftCover)
            nftName.text = nft.name()
            Glide.with(nftCollectionIcon).load(config.logo).into(nftCollectionIcon)
            nftCollectionName.text = config.name
            nftCoinTypeIcon.setImageResource(R.drawable.ic_coin_flow)
        }
    }

    private fun updateSendState(isSuccess: Boolean) {
//        binding.sendButton.setProgressVisible(false)
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