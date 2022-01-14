package io.outblock.lilico.page.nftdetail.presenter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.ActivityNftDetailBinding
import io.outblock.lilico.network.model.Nft
import io.outblock.lilico.page.nftdetail.model.NftDetailModel
import io.outblock.lilico.utils.extensions.res2color
import io.outblock.lilico.utils.toCoverUrl

class NftDetailPresenter(
    private val activity: AppCompatActivity,
    private val binding: ActivityNftDetailBinding,
) : BasePresenter<NftDetailModel> {

    init {
        setupToolbar()
    }

    override fun bind(model: NftDetailModel) {
        model.nft?.let { bindData(it) }
    }

    private fun bindData(nft: Nft) {
        with(binding) {
            Glide.with(coverView).load(nft.media?.uri?.toCoverUrl()).into(coverView)
            titleView.text = nft.title ?: nft.description
            descView.text = nft.contract.name
            purchaseDate.text = "01.01.2022"
        }
    }

    private fun setupToolbar() {
        binding.toolbar.navigationIcon?.mutate()?.setTint(R.color.neutrals1.res2color())
        activity.setSupportActionBar(binding.toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setDisplayShowHomeEnabled(true)
        activity.title = ""
    }
}