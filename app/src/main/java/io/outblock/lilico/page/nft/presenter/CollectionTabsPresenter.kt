package io.outblock.lilico.page.nft.presenter

import android.graphics.Color
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.page.nft.NFTFragmentViewModel
import io.outblock.lilico.page.nft.adapter.CollectionTabsAdapter
import io.outblock.lilico.page.nft.model.CollectionTabsModel
import io.outblock.lilico.utils.extensions.dp2px
import io.outblock.lilico.utils.findActivity
import io.outblock.lilico.widgets.itemdecoration.ColorDividerItemDecoration

class CollectionTabsPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<CollectionTabsModel> {
    private val recyclerView by lazy { view as RecyclerView }

    private val activity by lazy { findActivity(view) as FragmentActivity }
    private val viewModel by lazy { ViewModelProvider(activity)[NFTFragmentViewModel::class.java] }

    private val adapter by lazy { CollectionTabsAdapter() }

    init {
        with(recyclerView) {
            adapter = this@CollectionTabsPresenter.adapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(ColorDividerItemDecoration(Color.TRANSPARENT, 12.dp2px().toInt(), LinearLayout.HORIZONTAL))
        }

        viewModel.collectionTabChangeLiveData.observe(activity) { address ->
            if (view.isShown) {
                val data = adapter.getData().toList().map { it.copy() }.onEach { it.isSelected = it.address == address }
                adapter.setNewDiffData(data)
            }
        }
    }

    override fun bind(model: CollectionTabsModel) {
        adapter.setNewDiffData(model.collections)
    }
}