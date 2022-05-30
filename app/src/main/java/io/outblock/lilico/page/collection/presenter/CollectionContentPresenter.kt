package io.outblock.lilico.page.collection.presenter

import android.animation.ArgbEvaluator
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.zackratos.ultimatebarx.ultimatebarx.addStatusBarTopPadding
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.ActivityCollectionBinding
import io.outblock.lilico.manager.config.NftCollectionConfig
import io.outblock.lilico.page.browser.openBrowser
import io.outblock.lilico.page.collection.CollectionActivity
import io.outblock.lilico.page.collection.model.CollectionContentModel
import io.outblock.lilico.page.nft.nftlist.adapter.NFTListAdapter
import io.outblock.lilico.page.nft.nftlist.model.NFTItemModel
import io.outblock.lilico.utils.ScreenUtils
import io.outblock.lilico.utils.extensions.dp2px
import io.outblock.lilico.utils.extensions.res2color
import io.outblock.lilico.utils.extensions.res2dip
import io.outblock.lilico.widgets.itemdecoration.GridSpaceItemDecoration
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlin.math.min

class CollectionContentPresenter(
    private val activity: CollectionActivity,
    private val binding: ActivityCollectionBinding,
) : BasePresenter<CollectionContentModel> {
    private val adapter by lazy { NFTListAdapter() }

    private val dividerSize by lazy { R.dimen.nft_list_divider_size.res2dip().toDouble() }

    private val screenHeight by lazy { ScreenUtils.getScreenHeight() }

    init {
        with(binding.recyclerView) {
            adapter = this@CollectionContentPresenter.adapter
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
            addItemDecoration(
                GridSpaceItemDecoration(vertical = dividerSize, horizontal = dividerSize, start = dividerSize, end = dividerSize)
            )
            minimumHeight = screenHeight - 222.dp2px().toInt()
        }

        setupToolbar()

        binding.scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->
            updateToolbarColor(scrollY)
        })
        binding.backgroundImage.layoutParams.height = ScreenUtils.getScreenHeight()
    }

    override fun bind(model: CollectionContentModel) {
        model.data?.let { bindData(it) }
    }

    private fun bindData(data: List<NFTItemModel>) {
        bindHeader(data)
        adapter.setNewDiffData(data)
    }

    private fun bindHeader(data: List<NFTItemModel>) {
        val config = NftCollectionConfig.get(data.first().nft.contract.address) ?: return
        with(binding) {
            Glide.with(coverView).load(config.logo).transform(CenterCrop(), RoundedCorners(16.dp2px().toInt())).into(coverView)
            Glide.with(backgroundImage).load(config.logo)
                .transition(DrawableTransitionOptions.withCrossFade(100))
                .transform(BlurTransformation(15, 30))
                .into(backgroundImage)

            titleView.text = config.name
            subtitleView.text = activity.getString(R.string.collections_count, data.size)

            toolbar.title = config.name

            NftCollectionConfig.get(data.first().nft.contract.address)?.officialWebsite?.let { url ->
                exploreButton.setOnClickListener { openBrowser(activity, url) }
            }
        }
    }

    private fun setupToolbar() {
        binding.toolbar.navigationIcon?.mutate()?.setTint(R.color.neutrals1.res2color())
        binding.toolbar.addStatusBarTopPadding()
        activity.setSupportActionBar(binding.toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setDisplayShowHomeEnabled(true)
        activity.title = ""
    }

    private fun updateToolbarColor(scrollY: Int) {
        val progress = min(scrollY / (screenHeight * 0.15f), 1.0f)
        binding.toolbar.setBackgroundColor(
            ArgbEvaluator().evaluate(
                progress,
                R.color.transparent.res2color(),
                R.color.background.res2color(),
            ) as Int
        )
        binding.toolbar.setTitleTextColor(
            ArgbEvaluator().evaluate(
                progress,
                R.color.transparent.res2color(),
                R.color.text.res2color(),
            ) as Int
        )
    }
}