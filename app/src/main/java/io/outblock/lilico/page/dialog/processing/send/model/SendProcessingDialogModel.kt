package io.outblock.lilico.page.dialog.processing.send.model

import io.outblock.lilico.manager.transaction.TransactionState
import io.outblock.lilico.network.model.UserInfoData

class SendProcessingDialogModel(
    val userInfo: UserInfoData? = null,
    val amountConvert: Float? = null,
    val stateChange: TransactionState? = null,
)