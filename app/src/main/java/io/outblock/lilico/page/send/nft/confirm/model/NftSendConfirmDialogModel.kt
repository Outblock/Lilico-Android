package io.outblock.lilico.page.send.nft.confirm.model

import io.outblock.lilico.network.model.UserInfoData

class NftSendConfirmDialogModel(
    val userInfo: UserInfoData? = null,
    val amountConvert: Float? = null,
    val isSendSuccess: Boolean? = null,
)