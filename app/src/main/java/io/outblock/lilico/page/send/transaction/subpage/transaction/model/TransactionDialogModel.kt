package io.outblock.lilico.page.send.transaction.subpage.transaction.model

import io.outblock.lilico.network.model.UserInfoData

class TransactionDialogModel(
    val userInfo: UserInfoData? = null,
    val amountConvert: Float? = null,
    val isSendSuccess: Boolean? = null,
)