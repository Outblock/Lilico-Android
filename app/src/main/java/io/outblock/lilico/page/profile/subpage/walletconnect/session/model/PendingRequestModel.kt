package io.outblock.lilico.page.profile.subpage.walletconnect.session.model

import com.walletconnect.sign.client.Sign

class PendingRequestModel(
    val request: Sign.Model.PendingRequest,
    val metadata: Sign.Model.AppMetaData?,
)