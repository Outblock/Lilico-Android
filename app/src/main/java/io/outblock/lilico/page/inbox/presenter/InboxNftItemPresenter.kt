package io.outblock.lilico.page.inbox.presenter

import android.annotation.SuppressLint
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.ItemInboxNftBinding
import io.outblock.lilico.manager.config.NftCollectionConfig
import io.outblock.lilico.network.model.InboxNft
import io.outblock.lilico.page.browser.openBrowser
import io.outblock.lilico.page.inbox.InboxViewModel
import io.outblock.lilico.utils.findActivity

class InboxNftItemPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<InboxNft> {
    private val binding by lazy { ItemInboxNftBinding.bind(view) }
    private val viewModel by lazy { ViewModelProvider(findActivity(view) as FragmentActivity)[InboxViewModel::class.java] }

    @SuppressLint("SetTextI18n")
    override fun bind(model: InboxNft) {
        val collection = NftCollectionConfig.get(model.collectionAddress) ?: return
        with(binding) {
            titleView.text = collection.name
            tokenIdView.text = "ID: ${model.tokenId}"
            claimButton.setOnClickListener { viewModel.claimNft(model) }
            Glide.with(nftCollectionCoverView).load(collection.logo).into(nftCollectionCoverView)
            collectionWrapper.setOnClickListener { openBrowser(findActivity(view)!!, collection.officialWebsite) }
        }
    }
}