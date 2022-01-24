package io.outblock.lilico.page.nft.presenter

import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.cache.NftSelections
import io.outblock.lilico.databinding.ItemNftListSelectionsBinding
import io.outblock.lilico.page.nft.NFTFragmentViewModel
import io.outblock.lilico.page.nft.adapter.SelectionsAdapter
import io.outblock.lilico.utils.ScreenUtils
import io.outblock.lilico.utils.extensions.dp2px
import io.outblock.lilico.utils.findActivity
import io.outblock.lilico.utils.logd
import io.outblock.lilico.widgets.cardstackview.*

class SelectionItemPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<NftSelections>, CardStackListener {
    private val binding by lazy { ItemNftListSelectionsBinding.bind(view) }

    private val context = view.context

    private val viewModel by lazy { ViewModelProvider(findActivity(view) as FragmentActivity)[NFTFragmentViewModel::class.java] }

    private val layoutManager by lazy { createCardStackManager() }

    private val adapter by lazy { SelectionsAdapter() }

    init {
        with(binding.cardStackView) {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (ScreenUtils.getScreenWidth() * 0.7f + 12.dp2px()).toInt())
            layoutManager = this@SelectionItemPresenter.layoutManager
            adapter = this@SelectionItemPresenter.adapter
        }
    }

    override fun bind(model: NftSelections) {
        adapter.setNewDiffData(model.data.reversed())
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {
        logd(TAG, "onCardDragging")
    }

    override fun onCardSwiped(direction: Direction?) {
        logd(TAG, "onCardSwiped")
    }

    override fun onCardRewound() {
        logd(TAG, "onCardRewound")
    }

    override fun onCardCanceled() {
        logd(TAG, "onCardCanceled")
    }

    override fun onCardAppeared(view: View?, position: Int) {
        logd(TAG, "onCardAppeared")
        viewModel.updateSelectionIndex(position)
    }

    override fun onCardDisappeared(view: View?, position: Int) {
        logd(TAG, "onCardDisappeared")
    }

    private fun createCardStackManager(): CardStackLayoutManager {
        return CardStackLayoutManager(context, this@SelectionItemPresenter).apply {
            setStackFrom(StackFrom.Right)
            setVisibleCount(6)
            setTranslationInterval(15.dp2px())
            setScaleInterval(0.9f)
            setSwipeThreshold(0.3f)
            setMaxDegree(20.0f)
            setDirections(Direction.HORIZONTAL)
            setCanScrollHorizontal(true)
            setCanScrollVertical(false)
            setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
            setOverlayInterpolator(LinearInterpolator())
        }
    }

    companion object {
        private val TAG = SelectionItemPresenter::class.java.simpleName
    }
}