package io.outblock.lilico.page.staking.providers.adapter

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.outblock.lilico.R
import io.outblock.lilico.base.recyclerview.BaseAdapter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.manager.staking.StakingProvider
import io.outblock.lilico.page.staking.providers.model.ProviderTitleModel
import io.outblock.lilico.page.staking.providers.presenter.ProviderItemPresenter
import io.outblock.lilico.page.staking.providers.presenter.ProviderTitlePresenter

class StakeProviderAdapter : BaseAdapter<Any>(diffCallback) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ProviderTitleModel -> TYPE_TITLE
            else -> TYPE_PROVIDER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_TITLE -> ProviderTitlePresenter(parent.inflate(R.layout.item_stake_provider_title))
            TYPE_PROVIDER -> ProviderItemPresenter(parent.inflate(R.layout.item_stake_provider))
            else -> BaseViewHolder(View(parent.context))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ProviderTitlePresenter -> holder.bind(getItem(position) as ProviderTitleModel)
            is ProviderItemPresenter -> holder.bind(getItem(position) as StakingProvider)
        }
    }

    companion object {
        private const val TYPE_TITLE = 1
        private const val TYPE_PROVIDER = 2
    }
}

private val diffCallback = object : DiffUtil.ItemCallback<Any>() {
    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
        if (oldItem is StakingProvider && newItem is StakingProvider) {
            return oldItem.id == newItem.id
        }
        return oldItem == newItem
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
        if (oldItem is StakingProvider && newItem is StakingProvider) {
            return oldItem == newItem
        }
        return oldItem == newItem
    }
}