package io.outblock.lilico.page.browser.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.outblock.lilico.R
import io.outblock.lilico.base.recyclerview.BaseAdapter
import io.outblock.lilico.page.browser.model.RecommendModel
import io.outblock.lilico.page.browser.presenter.BrowserRecommendWordPresenter

class BrowserRecommendWordsAdapter : BaseAdapter<RecommendModel>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return BrowserRecommendWordPresenter(parent.inflate(R.layout.item_browser_recommend_word))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as BrowserRecommendWordPresenter).bind(getItem(position))
    }
}