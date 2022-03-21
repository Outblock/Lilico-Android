package io.outblock.lilico.page.addtoken.model

import io.outblock.lilico.manager.coin.FlowCoin

class TokenItem(
    val coin: FlowCoin,
    val isAdded: Boolean,
    val isAdding: Boolean? = null,
) {
    fun isNormalState() = !(isAdded || isAdding == true)
}