package io.outblock.lilico.page.token.detail.model

import io.outblock.lilico.network.model.CryptowatchSummaryResponse
import io.outblock.lilico.page.token.detail.Quote

class TokenDetailChartModel(
    val chartData: List<Quote>? = null,
    val summary: CryptowatchSummaryResponse.Result? = null,
)