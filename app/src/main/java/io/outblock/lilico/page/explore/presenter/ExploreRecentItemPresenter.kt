package io.outblock.lilico.page.explore.presenter

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.database.WebviewRecord
import io.outblock.lilico.databinding.ItemExploreRecentBinding
import io.outblock.lilico.page.browser.openBrowser
import io.outblock.lilico.page.browser.screenshotFile
import io.outblock.lilico.page.browser.screenshotFileName
import io.outblock.lilico.page.browser.toFavIcon
import io.outblock.lilico.page.explore.ExploreViewModel
import io.outblock.lilico.utils.findActivity

class ExploreRecentItemPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<WebviewRecord> {
    private val binding by lazy { ItemExploreRecentBinding.bind(view) }

    private val activity = findActivity(view)

    private val viewModel by lazy { ViewModelProvider(findActivity(view) as FragmentActivity)[ExploreViewModel::class.java] }

    override fun bind(model: WebviewRecord) {
        with(binding) {
            Glide.with(coverView).load(screenshotFile(screenshotFileName(model.url))).into(coverView)
            Glide.with(iconView).load(model.url.toFavIcon()).into(iconView)
            titleView.text = model.title

            view.setOnClickListener {
                viewModel.onDAppClick(model.url)
                openBrowser(activity!!, model.url)
            }
        }
    }
}