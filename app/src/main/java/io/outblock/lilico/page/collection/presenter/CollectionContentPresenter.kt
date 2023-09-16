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
import io.outblock.lilico.manager.config.NftCollection
import io.outblock.lilico.manager.wallet.WalletManager
import io.outblock.lilico.network.model.NftCollectionWrapper
import io.outblock.lilico.page.browser.openBrowser
import io.outblock.lilico.page.collection.CollectionActivity
import io.outblock.lilico.page.collection.model.CollectionContentModel
import io.outblock.lilico.page.nft.nftlist.adapter.NFTListAdapter
import io.outblock.lilico.page.profile.subpage.wallet.ChildAccountCollectionManager
import io.outblock.lilico.utils.ScreenUtils
import io.outblock.lilico.utils.extensions.dp2px
import io.outblock.lilico.utils.extensions.gone
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.extensions.res2color
import io.outblock.lilico.utils.extensions.res2dip
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.utils.extensions.visible
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
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (this@CollectionContentPresenter.adapter.isSingleLineItem(position)) spanCount else 1
                    }
                }
            }
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
        model.data?.let {
            binding.progressBar.setVisible(it.isEmpty())
            adapter.setNewDiffData(it)
        }
        model.collection?.let { bindHeader(it) }
    }

    private fun bindHeader(collectionWrapper: NftCollectionWrapper) {
        val collection = collectionWrapper.collection ?: return
        with(binding) {
            Glide.with(coverView).load(collection.logo).transform(CenterCrop(), RoundedCorners(16.dp2px().toInt())).into(coverView)
            Glide.with(backgroundImage).load(collection.logo)
                .transition(DrawableTransitionOptions.withCrossFade(100))
                .transform(BlurTransformation(15, 30))
                .into(backgroundImage)

            titleView.text = collection.name
            subtitleView.text = activity.getString(R.string.collectibles_count, collectionWrapper.count)

            toolbar.title = collection.name

            collection.officialWebsite.let { url ->
                exploreButton.setOnClickListener { openBrowser(activity, url) }
            }
        }
        bindAccessible(collection)
    }

    private fun bindAccessible(collection: NftCollection) {
        if (ChildAccountCollectionManager.isNFTCollectionAccessible(collection.id)) {
            binding.inaccessibleTip.gone()
            return
        }
        val accountName = WalletManager.childAccount(WalletManager.selectedWalletAddress())?.name ?: R.string.default_child_account_name.res2String()
        binding.tvInaccessibleTip.text = activity.getString(R.string.inaccessible_token_tip, collection.name, accountName)
        binding.inaccessibleTip.visible()
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