package io.outblock.lilico.page.nft.presenter

import androidx.recyclerview.widget.GridLayoutManager
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.FragmentNftBinding
import io.outblock.lilico.page.nft.NFTFragment
import io.outblock.lilico.page.nft.adapter.NFTListAdapter
import io.outblock.lilico.page.nft.model.NFTFragmentModel
import io.outblock.lilico.widgets.itemdecoration.GridSpaceItemDecoration

class NFTFragmentPresenter(
    private val fragment: NFTFragment,
    private val binding: FragmentNftBinding,
) : BasePresenter<NFTFragmentModel> {
    private val adapter by lazy { NFTListAdapter() }

    init {
        with(binding.recyclerView) {
            adapter = this@NFTFragmentPresenter.adapter
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
            addItemDecoration(GridSpaceItemDecoration(vertical = 16.0, horizontal = 16.0, start = 16.0, end = 16.0))
        }
    }

    override fun bind(model: NFTFragmentModel) {
        model.data?.let { adapter.setNewDiffData(it) }
    }
}