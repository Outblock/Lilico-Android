package io.outblock.lilico.page.guide.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.outblock.lilico.R
import io.outblock.lilico.base.recyclerview.BaseAdapter
import io.outblock.lilico.page.guide.model.GuideItemModel
import io.outblock.lilico.page.guide.presenter.GuideItemPresenter

class GuideItemAdapter : BaseAdapter<GuideItemModel>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return GuideItemPresenter(parent.inflate(R.layout.item_guide_page))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as GuideItemPresenter).bind(getData()[position])
    }
}