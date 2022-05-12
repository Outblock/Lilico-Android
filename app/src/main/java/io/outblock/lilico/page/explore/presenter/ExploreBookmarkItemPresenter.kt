package io.outblock.lilico.page.explore.presenter

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.database.Bookmark
import io.outblock.lilico.page.browser.openBrowser
import io.outblock.lilico.page.browser.toFavIcon
import io.outblock.lilico.utils.findActivity

class ExploreBookmarkItemPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<Bookmark> {
    private val iconView by lazy { view.findViewById<ImageView>(R.id.icon_view) }

    private val activity = findActivity(view)

    override fun bind(model: Bookmark) {
        Glide.with(iconView).load(model.url.toFavIcon()).into(iconView)
        view.setOnClickListener {
            openBrowser(activity!!, model.url)
        }
    }
}