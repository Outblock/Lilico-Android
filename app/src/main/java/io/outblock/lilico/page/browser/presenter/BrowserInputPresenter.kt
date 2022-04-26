package io.outblock.lilico.page.browser.presenter

import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.LayoutBrowserBinding
import io.outblock.lilico.page.browser.model.BrowserInputModel

class BrowserInputPresenter(
    private val binding: LayoutBrowserBinding,
) : BasePresenter<BrowserInputModel> {

    init {
        binding.textWrapper.setOnClickListener { }
    }

    override fun bind(model: BrowserInputModel) {
    }
}