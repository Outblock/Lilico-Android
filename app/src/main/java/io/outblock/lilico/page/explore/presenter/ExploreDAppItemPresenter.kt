package io.outblock.lilico.page.explore.presenter

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.ItemExploreDappBinding
import io.outblock.lilico.manager.app.isTestnet
import io.outblock.lilico.page.browser.openBrowser
import io.outblock.lilico.page.explore.ExploreViewModel
import io.outblock.lilico.page.explore.model.DAppModel
import io.outblock.lilico.utils.extensions.urlHost
import io.outblock.lilico.utils.findActivity

class ExploreDAppItemPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<DAppModel> {
    private val binding by lazy { ItemExploreDappBinding.bind(view) }

    private val activity = findActivity(view)

    private val viewModel by lazy { ViewModelProvider(findActivity(view) as FragmentActivity)[ExploreViewModel::class.java] }

    override fun bind(model: DAppModel) {
        with(binding) {
            Glide.with(iconView).load(model.logo).into(iconView)
            titleView.text = model.name
            domainView.text = model.url?.urlHost()
            descView.text = model.description

            view.setOnClickListener {
                val url = if (isTestnet()) model.testnetUrl else model.url
                viewModel.onDAppClick(url!!)
                openBrowser(activity!!, url)
            }
        }
    }
}