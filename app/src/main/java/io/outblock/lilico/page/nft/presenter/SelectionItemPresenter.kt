package io.outblock.lilico.page.nft.presenter

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.helper.widget.Carousel
import androidx.core.view.children
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.card.MaterialCardView
import com.zackratos.ultimatebarx.ultimatebarx.statusBarHeight
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.cache.NftSelections
import io.outblock.lilico.databinding.ItemNftTopSelectionHeaderBinding
import io.outblock.lilico.network.model.Nft
import io.outblock.lilico.page.nft.NFTFragmentViewModel
import io.outblock.lilico.page.nft.widget.NftCardView
import io.outblock.lilico.page.nftdetail.NftDetailActivity
import io.outblock.lilico.utils.ScreenUtils
import io.outblock.lilico.utils.extensions.dp2px
import io.outblock.lilico.utils.extensions.res2pix
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.utils.findActivity
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.uiScope

class SelectionItemPresenter(
    private val view: ViewGroup,
) : BaseViewHolder(view), BasePresenter<NftSelections> {
    private val binding by lazy { ItemNftTopSelectionHeaderBinding.bind(view.getChildAt(0)) }

    private val activity by lazy { findActivity(view) as FragmentActivity }

    private val viewModel by lazy { ViewModelProvider(activity)[NFTFragmentViewModel::class.java] }

    private var currentIndex = 0

    private var data: List<Nft>? = null

    init {
        with(binding.motionLayout) {
            layoutParams.height = (ScreenUtils.getScreenWidth() * 0.7f + 32.dp2px()).toInt()
            setOnClickListener {
                data?.getOrNull(currentIndex)?.let {
                    NftDetailActivity.launch(activity, viewModel.getWalletAddress()!!, it.contract.address, it.id.tokenId)
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                children.forEach { child ->
                    if (child is MaterialCardView) {
                        child.outlineAmbientShadowColor = Color.TRANSPARENT
                        child.outlineSpotShadowColor = Color.TRANSPARENT
                        child.setCardBackgroundColor(ColorStateList.valueOf(Color.TRANSPARENT))
                    }
                }
            }
        }

        with(binding.placeholder) {
            layoutParams.height = R.dimen.nft_tool_bar_height.res2pix() + statusBarHeight
        }
    }

    override fun bind(model: NftSelections) {
        val list = model.data.reversed()
        currentIndex = 0
        view.setVisible(list.isNotEmpty())
        updateData(list)
    }

    private fun updateData(list: List<Nft>) {
        uiScope {
            this.data = list
            binding.titleWrapper.setVisible(list.isNotEmpty())
            binding.motionLayout.setVisible(list.isNotEmpty())
            binding.carousel.setAdapter(SelectionsAdapter(list))
            binding.motionLayout.post { binding.carousel.jumpToIndex(currentIndex) }
            binding.carousel.refresh()
            viewModel.updateSelectionIndex(if (list.isEmpty()) -1 else currentIndex)
        }
    }

    private inner class SelectionsAdapter(
        private val data: List<Nft>,
    ) : Carousel.Adapter {

        override fun count(): Int = data.size

        override fun populate(view: View?, index: Int) {
            val itemView = view as NftCardView
            itemView.bindData(data[index])
        }

        override fun onNewItem(index: Int) {
            logd(TAG, "onNewItem:$index")
            currentIndex = index
            viewModel.updateSelectionIndex(index)
        }
    }

    companion object {
        private val TAG = SelectionItemPresenter::class.java.simpleName
    }
}
