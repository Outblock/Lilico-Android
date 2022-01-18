package io.outblock.lilico.page.nft.presenter

import android.view.View
import android.view.ViewGroup
import com.zackratos.ultimatebarx.ultimatebarx.statusBarHeight
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.ItemNftListBinding
import io.outblock.lilico.page.nft.model.HeaderPlaceholderModel
import io.outblock.lilico.utils.extensions.res2pix

class HeaderPlaceholderPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<HeaderPlaceholderModel> {
    private val binding by lazy { ItemNftListBinding.bind(view) }
    private val context = view.context

    init {
        val height = R.dimen.nft_tool_bar_height.res2pix() + statusBarHeight - R.dimen.nft_list_divider_size.res2pix()
        val layoutParam = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)
        view.layoutParams = layoutParam
    }

    override fun bind(model: HeaderPlaceholderModel) {
    }
}