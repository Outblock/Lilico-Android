package io.outblock.lilico.page.nft.presenter

import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.helper.widget.Carousel
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.cache.NftSelections
import io.outblock.lilico.databinding.ItemNftListSelectionsBinding
import io.outblock.lilico.manager.nft.NftSelectionManager
import io.outblock.lilico.manager.nft.OnNftSelectionChangeListener
import io.outblock.lilico.network.model.Nft
import io.outblock.lilico.page.nft.NFTFragmentViewModel
import io.outblock.lilico.page.nft.widget.NftCardView
import io.outblock.lilico.page.nftdetail.NftDetailActivity
import io.outblock.lilico.utils.ScreenUtils
import io.outblock.lilico.utils.extensions.dp2px
import io.outblock.lilico.utils.findActivity
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.uiScope
import kotlin.math.min

class SelectionItemPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<NftSelections>, OnNftSelectionChangeListener {
    private val binding by lazy { ItemNftListSelectionsBinding.bind(view) }

    private val activity by lazy { findActivity(view) as FragmentActivity }

    private val viewModel by lazy { ViewModelProvider(activity)[NFTFragmentViewModel::class.java] }

    private var currentIndex = 0

    private var data: List<Nft>? = null

    init {
        with(binding.root) {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (ScreenUtils.getScreenWidth() * 0.7f + 32.dp2px()).toInt())
            setOnClickListener {
                data?.getOrNull(currentIndex)?.let {
                    NftDetailActivity.launch(context, viewModel.getWalletAddress()!!, it.contract.address, it.id.tokenId)
                }
            }
        }
        NftSelectionManager.addOnNftSelectionChangeListener(this)
    }

    override fun onAddSelection(nft: Nft) {
        if (data?.firstOrNull { it.uniqueId() == nft.uniqueId() } != null) {
            return
        }
        logd(TAG, "onAddSelection")
        val data = (data ?: emptyList()).toMutableList()
        data.add(0, nft)
        currentIndex = 0
        updateData(data)
    }

    override fun onRemoveSelection(nft: Nft) {
        logd(TAG, "onRemoveSelection")
        val data = (data ?: emptyList()).toMutableList()
        val index = data.indexOf(nft)
        data.remove(nft)
        if (index == currentIndex) {
            currentIndex = min(data.size - 1, index + 1)
        }
        updateData(data)
    }

    override fun bind(model: NftSelections) {
        val list = model.data.reversed()
        if (this.data == list) {
            return
        }
        logd(TAG, "bind")
        updateData(list)
    }

    private fun updateData(list: List<Nft>) {
        uiScope {
            this.data = list
            binding.carousel.setAdapter(SelectionsAdapter(list))
            if (list.isNotEmpty()) {
                binding.root.post { binding.carousel.jumpToIndex(currentIndex) }
            }
            binding.carousel.refresh()
            viewModel.updateSelectionIndex(min(list.size - 1, currentIndex))
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
