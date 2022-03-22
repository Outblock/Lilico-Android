package io.outblock.lilico.page.addtoken.model

import io.outblock.lilico.manager.coin.FlowCoin

data class TokenItem(
    val coin: FlowCoin,
    var isAdded: Boolean,
    var isAdding: Boolean? = null,
) {
    fun isNormalState() = !(isAdded || isAdding == true)
}