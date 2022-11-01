package io.outblock.lilico.page.profile.subpage.currency.model

class CurrencyModel(
    val data: List<CurrencyItemModel>? = null,
)

data class CurrencyItemModel(
    val currency: Currency,
    val isSelected: Boolean,
)