package io.outblock.lilico.page.browser.model

class BrowserInputModel(
    val onLoadNewUrl: String? = null,
    val onHideInputPanel: Boolean? = null,
    val onPageAttach: Boolean? = null,
    val onPageDetach: Boolean? = null,
    val recommendWords: List<RecommendModel>? = null,
)