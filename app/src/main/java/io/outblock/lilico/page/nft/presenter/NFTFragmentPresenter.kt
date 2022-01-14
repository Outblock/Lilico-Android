package io.outblock.lilico.page.nft.presenter

import androidx.recyclerview.widget.GridLayoutManager
import com.flyco.tablayout.listener.OnTabSelectListener
import com.zackratos.ultimatebarx.ultimatebarx.addStatusBarTopPadding
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.FragmentNftBinding
import io.outblock.lilico.page.nft.NFTFragment
import io.outblock.lilico.page.nft.adapter.NFTListAdapter
import io.outblock.lilico.page.nft.model.GridHeaderPlaceholderModel
import io.outblock.lilico.page.nft.model.NFTFragmentModel
import io.outblock.lilico.page.nft.model.NFTTitleModel
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.extensions.res2dip
import io.outblock.lilico.utils.extensions.res2pix
import io.outblock.lilico.utils.logd
import io.outblock.lilico.widgets.itemdecoration.GridSpaceItemDecoration

class NFTFragmentPresenter(
    private val fragment: NFTFragment,
    private val binding: FragmentNftBinding,
) : BasePresenter<NFTFragmentModel> {
    private val adapter by lazy { NFTListAdapter() }

    private val dividerSize by lazy { R.dimen.nft_list_divider_size.res2dip().toDouble() }

    init {
        binding.toolbar.post { binding.toolbar.addStatusBarTopPadding() }
        with(binding.recyclerView) {
            adapter = this@NFTFragmentPresenter.adapter
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (isSingleLineItem(position)) spanCount else 1
                    }
                }
            }
            addItemDecoration(GridSpaceItemDecoration(vertical = dividerSize, horizontal = dividerSize, start = dividerSize, end = dividerSize))
        }
        with(binding.tabs) {
            setTabData(listOf(R.string.list.res2String(), R.string.grid.res2String()).toTypedArray())
            setOnTabSelectListener(object: OnTabSelectListener{
                override fun onTabSelect(position: Int) {
                    logd("xxx","onTabSelect:$position")
                }

                override fun onTabReselect(position: Int) {}
            })
        }
    }

    private fun isSingleLineItem(position: Int): Boolean {
        val item = adapter.getData().getOrNull(position) ?: return false
        return item is GridHeaderPlaceholderModel || item is NFTTitleModel
    }

    override fun bind(model: NFTFragmentModel) {
        model.data?.let { adapter.setNewDiffData(it) }
    }
}