package io.outblock.lilico.page.token.detail.model

import io.outblock.lilico.network.model.CryptowatchSummaryData
import io.outblock.lilico.page.token.detail.Quote

class TokenDetailChartModel(
    val chartData: List<Quote>? = null,
    val summary: CryptowatchSummaryData.Result? = null,
)