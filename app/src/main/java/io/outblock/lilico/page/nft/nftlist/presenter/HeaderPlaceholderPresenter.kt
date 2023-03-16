package io.outblock.lilico.page.nft.nftlist.presenter

import android.view.View
import android.view.ViewGroup
import com.zackratos.ultimatebarx.ultimatebarx.statusBarHeight
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.page.nft.nftlist.model.HeaderPlaceholderModel
import io.outblock.lilico.utils.extensions.res2pix

class HeaderPlaceholderPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<HeaderPlaceholderModel> {

    private val baseHeight by lazy { R.dimen.nft_tool_bar_height.res2pix() + statusBarHeight }
    private val dividerHeight by lazy { R.dimen.nft_list_divider_size.res2pix() }

    override fun bind(model: HeaderPlaceholderModel) {
        val layoutParam =
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, if (model.addDivider) baseHeight else baseHeight - dividerHeight)
        view.layoutParams = layoutParam
    }
}