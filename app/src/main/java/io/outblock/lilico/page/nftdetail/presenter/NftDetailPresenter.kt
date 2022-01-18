package io.outblock.lilico.page.nftdetail.presenter

import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.zackratos.ultimatebarx.ultimatebarx.addStatusBarTopPadding
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.ActivityNftDetailBinding
import io.outblock.lilico.network.model.Nft
import io.outblock.lilico.page.nft.*
import io.outblock.lilico.page.nftdetail.model.NftDetailModel
import io.outblock.lilico.utils.extensions.res2color
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.isNftInSelection
import io.outblock.lilico.utils.uiScope
import jp.wasabeef.glide.transformations.BlurTransformation

class NftDetailPresenter(
    private val activity: AppCompatActivity,
    private val binding: ActivityNftDetailBinding,
) : BasePresenter<NftDetailModel> {

    private var nft: Nft? = null

    init {
        setupToolbar()
        with(binding) {
            toolbar.addStatusBarTopPadding()

            collectButton.setOnClickListener {
                val nft = nft ?: return@setOnClickListener
                toggleNftSelection(nft)
            }
        }
    }

    override fun bind(model: NftDetailModel) {
        model.nft?.let { bindData(it) }
    }

    private fun bindData(nft: Nft) {
        this.nft = nft
        with(binding) {
            Glide.with(coverView).load(nft.cover()).into(coverView)
            Glide.with(backgroundImage).load(nft.cover()).transform(BlurTransformation(15, 30)).into(backgroundImage)
            titleView.text = nft.name()
            subtitleView.text = nft.contract.externalDomain
            descView.text = nft.desc()
            purchaseDate.text = "01.01.2022"

            ioScope { updateSelectionState(isNftInSelection(nft)) }
        }
    }

    private fun setupToolbar() {
        binding.toolbar.navigationIcon?.mutate()?.setTint(R.color.neutrals1.res2color())
        activity.setSupportActionBar(binding.toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setDisplayShowHomeEnabled(true)
        activity.title = ""
    }

    private fun updateSelectionState(isSelected: Boolean) {
        uiScope { binding.collectButton.setImageResource(if (isSelected) R.drawable.ic_collect_nft_on else R.drawable.ic_collect_nft) }
    }

    private fun toggleNftSelection(nft: Nft) {
        ioScope {
            if (isNftInSelection(nft)) {
                removeNftFromSelection(nft)
                updateSelectionState(false)
            } else {
                addNftToSelection(nft)
                updateSelectionState(true)
            }
        }
    }
}