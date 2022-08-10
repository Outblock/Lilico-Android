package io.outblock.lilico.page.nft.nftlist.adapter

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseAdapter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.ItemNftSelectionsBinding
import io.outblock.lilico.network.model.Nft
import io.outblock.lilico.page.nft.nftdetail.NftDetailActivity
import io.outblock.lilico.page.nft.nftlist.NFTFragmentViewModelV0
import io.outblock.lilico.page.nft.nftlist.cover
import io.outblock.lilico.utils.ScreenUtils
import io.outblock.lilico.utils.extensions.dp2px
import io.outblock.lilico.utils.findActivity

class SelectionsAdapter : BaseAdapter<Nft>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_nft_selections))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(getItem(position))
    }
}

private class ViewHolder(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<Nft> {
    private val binding by lazy { ItemNftSelectionsBinding.bind(view) }

    private val corners by lazy { 10.dp2px().toInt() }

    private val viewModel by lazy { ViewModelProvider(findActivity(view) as FragmentActivity)[NFTFragmentViewModelV0::class.java] }

    init {
        val size = (ScreenUtils.getScreenWidth() * 0.7f).toInt()
        view.layoutParams = ViewGroup.LayoutParams(size, size)
    }

    override fun bind(model: Nft) {
        with(binding) {
            Glide.with(imageView).load(model.cover()).transform(CenterCrop(), RoundedCorners(corners)).into(imageView)
        }
        view.setOnClickListener {
            NftDetailActivity.launch(view.context, model.uniqueId())
        }
    }
}