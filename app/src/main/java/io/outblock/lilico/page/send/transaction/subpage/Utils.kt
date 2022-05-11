package io.outblock.lilico.page.send.transaction.subpage

import android.annotation.SuppressLint
import com.bumptech.glide.Glide
import io.outblock.lilico.R
import io.outblock.lilico.databinding.DialogSendConfirmBinding
import io.outblock.lilico.manager.config.NftCollectionConfig
import io.outblock.lilico.network.model.AddressBookContact
import io.outblock.lilico.network.model.AddressBookDomain
import io.outblock.lilico.network.model.Nft
import io.outblock.lilico.network.model.UserInfoData
import io.outblock.lilico.page.nft.nftlist.cover
import io.outblock.lilico.page.nft.nftlist.name
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.utils.loadAvatar
import io.outblock.lilico.wallet.toAddress


@SuppressLint("SetTextI18n")
fun DialogSendConfirmBinding.bindUserInfo(userInfo: UserInfoData, contact: AddressBookContact) {
    fromAvatarView.loadAvatar(userInfo.avatar)
    fromNameView.text = userInfo.nickname
    fromAddressView.text = "(${userInfo.address?.toAddress()})"

    toNameView.text = "${contact.name()} ${if (!contact.username.isNullOrEmpty()) "  (@${contact.username})" else ""}"
    namePrefixView.text = contact.prefixName()
    namePrefixView.setVisible(contact.prefixName().isNotEmpty())

    if ((contact.domain?.domainType ?: 0) == 0) {
        toAvatarView.setVisible(!contact.avatar.isNullOrEmpty(), invisible = true)
        toAvatarView.loadAvatar(contact.avatar.orEmpty())
        namePrefixView.setVisible(false)
    } else {
        val avatar =
            if (contact.domain?.domainType == AddressBookDomain.DOMAIN_FIND_XYZ) R.drawable.ic_domain_logo_findxyz else R.drawable.ic_domain_logo_flowns
        toAvatarView.setVisible(true)
        Glide.with(toAvatarView).load(avatar).into(toAvatarView)
    }

    toAddressView.text = "(${contact.address})"
}

fun DialogSendConfirmBinding.bindNft(nft: Nft) {
    val config = NftCollectionConfig.get(nft.contract.address) ?: return
    Glide.with(nftCover).load(nft.cover()).into(nftCover)
    nftName.text = nft.name()
    Glide.with(nftCollectionIcon).load(config.logo).into(nftCollectionIcon)
    nftCollectionName.text = config.name
    nftCoinTypeIcon.setImageResource(R.drawable.ic_coin_flow)
}