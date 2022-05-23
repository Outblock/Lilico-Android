package io.outblock.lilico.page.explore.presenter

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.database.Bookmark
import io.outblock.lilico.databinding.ItemExploreBookmarkManageBinding
import io.outblock.lilico.page.browser.openBrowser
import io.outblock.lilico.page.browser.toFavIcon
import io.outblock.lilico.page.explore.ExploreViewModel
import io.outblock.lilico.page.explore.subpage.BookmarkPopupMenu
import io.outblock.lilico.utils.extensions.urlHost
import io.outblock.lilico.utils.findActivity

class ExploreBookmarkManageItemPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<Bookmark> {
    private val binding by lazy { ItemExploreBookmarkManageBinding.bind(view) }

    private val activity = findActivity(view)

    private val viewModel by lazy { ViewModelProvider(findActivity(view) as FragmentActivity)[ExploreViewModel::class.java] }

    override fun bind(model: Bookmark) {
        with(binding) {
            Glide.with(iconView).load(model.url.toFavIcon()).placeholder(R.drawable.placeholder).into(iconView)
            titleView.text = model.title
            domainView.text = model.url.urlHost()
        }
        view.setOnClickListener {
            viewModel.onDAppClick(model.url)
            openBrowser(activity!!, model.url)
        }
        view.setOnLongClickListener {
            BookmarkPopupMenu(view, model).show()
            true
        }
    }
}