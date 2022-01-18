package io.outblock.lilico.page.nft.presenter

import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.flyco.tablayout.listener.OnTabSelectListener
import com.zackratos.ultimatebarx.ultimatebarx.addStatusBarTopPadding
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.cache.NftSelections
import io.outblock.lilico.cache.nftSelectionCache
import io.outblock.lilico.databinding.FragmentNftBinding
import io.outblock.lilico.page.nft.NFTFragment
import io.outblock.lilico.page.nft.NFTFragmentViewModel
import io.outblock.lilico.page.nft.adapter.NFTListAdapter
import io.outblock.lilico.page.nft.cover
import io.outblock.lilico.page.nft.model.HeaderPlaceholderModel
import io.outblock.lilico.page.nft.model.NFTFragmentModel
import io.outblock.lilico.page.nft.model.NFTTitleModel
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.extensions.res2color
import io.outblock.lilico.utils.extensions.res2dip
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.uiScope
import io.outblock.lilico.widgets.itemdecoration.DividerVisibleCheck
import io.outblock.lilico.widgets.itemdecoration.GridSpaceItemDecoration
import jp.wasabeef.glide.transformations.BlurTransformation

class NFTFragmentPresenter(
    private val fragment: NFTFragment,
    private val binding: FragmentNftBinding,
) : BasePresenter<NFTFragmentModel> {
    private val adapter by lazy { NFTListAdapter() }

    private val dividerSize by lazy { R.dimen.nft_list_divider_size.res2dip().toDouble() }

    private val viewModel by lazy { ViewModelProvider(fragment.requireActivity())[NFTFragmentViewModel::class.java] }

    init {
        binding.toolbar.post { binding.toolbar.addStatusBarTopPadding() }
        setupRecyclerView()
        setupTabs()
    }

    override fun bind(model: NFTFragmentModel) {
        model.data?.let {
            adapter.setNewDiffData(it)
            updateSelection(0)
        }
        model.selectionIndex?.let { updateSelection(it) }
    }

    private fun updateSelection(index: Int) {
        ioScope {
            if (viewModel.isGridMode()) {
                uiScope { Glide.with(binding.backgroundImage).clear(binding.backgroundImage) }
                return@ioScope
            }
            val nft = nftSelectionCache().read()?.data?.reversed()?.getOrNull(index) ?: return@ioScope
            uiScope { Glide.with(binding.backgroundImage).load(nft.cover()).transform(BlurTransformation(15, 30)).into(binding.backgroundImage) }
        }
    }

    private fun setupRecyclerView() {
        with(binding.recyclerView) {
            adapter = this@NFTFragmentPresenter.adapter
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (isSingleLineItem(position)) spanCount else 1
                    }
                }
            }
            addItemDecoration(
                GridSpaceItemDecoration(
                    vertical = dividerSize,
                    horizontal = dividerSize,
                    start = dividerSize,
                    end = dividerSize
                ).apply {
                    setDividerVisibleCheck(object : DividerVisibleCheck {
                        override fun dividerVisible(position: Int): Boolean {
                            val item = this@NFTFragmentPresenter.adapter.getData().getOrNull(position) ?: return true
                            return item !is NftSelections
                        }
                    })
                })
        }
    }

    private fun setupTabs() {
        with(binding.tabs) {
            setTabData(listOf(R.string.list.res2String(), R.string.grid.res2String()).toTypedArray())
            setOnTabSelectListener(object : OnTabSelectListener {
                override fun onTabSelect(position: Int) {
                    viewModel.updateLayoutMode(position != 0)
                    binding.toolbar.setBackgroundResource(if (position == 0) R.color.transparent else R.color.colorPrimary)
                    binding.tabsBackground.background.setTint(if (position == 0) R.color.white.res2color() else R.color.neutrals4.res2color())
                }

                override fun onTabReselect(position: Int) {}
            })
        }
    }

    private fun isSingleLineItem(position: Int): Boolean {
        val item = adapter.getData().getOrNull(position) ?: return false
        return item is HeaderPlaceholderModel || item is NFTTitleModel || item is NftSelections
    }
}