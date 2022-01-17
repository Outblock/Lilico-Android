package io.outblock.lilico.page.nftdetail.presenter

import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.zackratos.ultimatebarx.ultimatebarx.addStatusBarTopPadding
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.ActivityNftDetailBinding
import io.outblock.lilico.network.model.Nft
import io.outblock.lilico.page.nft.cover
import io.outblock.lilico.page.nft.desc
import io.outblock.lilico.page.nft.name
import io.outblock.lilico.page.nftdetail.model.NftDetailModel
import io.outblock.lilico.utils.extensions.res2color
import jp.wasabeef.glide.transformations.BlurTransformation

class NftDetailPresenter(
    private val activity: AppCompatActivity,
    private val binding: ActivityNftDetailBinding,
) : BasePresenter<NftDetailModel> {

    init {
        setupToolbar()
        with(binding) {
            toolbar.addStatusBarTopPadding()
        }
    }

    override fun bind(model: NftDetailModel) {
        model.nft?.let { bindData(it) }
    }

    private fun bindData(nft: Nft) {
        with(binding) {
            Glide.with(coverView).load(nft.cover()).into(coverView)
            Glide.with(backgroundImage).load(nft.cover()).transform(BlurTransformation(15, 30)).into(backgroundImage)
            titleView.text = nft.name()
            subtitleView.text = nft.contract.externalDomain
            descView.text = nft.desc()
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