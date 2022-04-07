package io.outblock.lilico.page.token.detail.model

import io.outblock.lilico.page.token.detail.Quote

class TokenDetailModel(
    val balanceAmount: Float? = null,
    val balancePrice: Float? = null,
    val chartData: List<Quote>? = null,
)