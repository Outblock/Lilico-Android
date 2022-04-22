package io.outblock.lilico.page.explore.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.outblock.lilico.R
import io.outblock.lilico.base.recyclerview.BaseAdapter
import io.outblock.lilico.database.WebviewRecord
import io.outblock.lilico.page.explore.exploreRecentDiffCallback
import io.outblock.lilico.page.explore.presenter.ExploreRecentItemPresenter

class ExploreRecentAdapter : BaseAdapter<WebviewRecord>(exploreRecentDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ExploreRecentItemPresenter(parent.inflate(R.layout.item_explore_recent))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ExploreRecentItemPresenter).bind(getData()[position])
    }
}