package io.outblock.lilico.page.explore.presenter

import android.view.View
import com.bumptech.glide.Glide
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.database.WebviewRecord
import io.outblock.lilico.databinding.ItemExploreRecentBinding
import io.outblock.lilico.page.browser.faviconFileName
import io.outblock.lilico.page.browser.openBrowser
import io.outblock.lilico.page.browser.screenshotFile
import io.outblock.lilico.page.browser.screenshotFileName
import io.outblock.lilico.utils.findActivity

class ExploreRecentItemPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<WebviewRecord> {
    private val binding by lazy { ItemExploreRecentBinding.bind(view) }

    override fun bind(model: WebviewRecord) {
        with(binding) {
            Glide.with(coverView).load(screenshotFile(screenshotFileName(model.url))).into(coverView)
            Glide.with(iconView).load(screenshotFile(faviconFileName(model.url))).into(iconView)
            titleView.text = model.title

            view.setOnClickListener { openBrowser(findActivity(view)!!, model.url) }
        }
    }
}