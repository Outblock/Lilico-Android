package io.outblock.lilico.page.nftdetail.presenter

import android.animation.ArgbEvaluator
import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.core.widget.NestedScrollView
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.zackratos.ultimatebarx.ultimatebarx.addStatusBarTopPadding
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.ActivityNftDetailBinding
import io.outblock.lilico.manager.config.NftCollectionConfig
import io.outblock.lilico.manager.nft.NftSelectionManager
import io.outblock.lilico.network.model.Nft
import io.outblock.lilico.page.nft.cover
import io.outblock.lilico.page.nft.desc
import io.outblock.lilico.page.nftdetail.model.NftDetailModel
import io.outblock.lilico.page.nftdetail.widget.NftMorePopupMenu
import io.outblock.lilico.utils.ScreenUtils
import io.outblock.lilico.utils.extensions.res2color
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.isNftInSelection
import io.outblock.lilico.utils.uiScope
import io.outblock.lilico.widgets.likebutton.LikeButton
import io.outblock.lilico.widgets.likebutton.OnLikeListener
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlin.math.min

private val filterMetadata by lazy { listOf("uri", "img", "description") }

class NftDetailPresenter(
    private val activity: AppCompatActivity,
    private val binding: ActivityNftDetailBinding,
) : BasePresenter<NftDetailModel> {

    private var nft: Nft? = null

    private var pageColor: Int = R.color.text_sub.res2color()

    private val screenHeight by lazy { ScreenUtils.getScreenHeight() }

    private var coverRatio = "1:1"

    init {
        setupToolbar()
        with(binding) {
            toolbar.addStatusBarTopPadding()

            collectButton.setOnLikeListener(object : OnLikeListener {
                override fun liked(likeButton: LikeButton?) {
                    val nft = nft ?: return
                    toggleNftSelection(nft)
                }

                override fun unLiked(likeButton: LikeButton?) {
                    val nft = nft ?: return
                    toggleNftSelection(nft)
                }
            })

            backgroundImage.layoutParams.height = ScreenUtils.getScreenHeight()
            backgroundGradient.layoutParams.height = ScreenUtils.getScreenHeight()

            scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->
                updateToolbarColor(scrollY)
            })

            moreButton.setOnClickListener { NftMorePopupMenu(it, pageColor).show() }
            collectButton.setLikeDrawableTint(R.color.colorSecondary.res2color())
        }
    }

    override fun bind(model: NftDetailModel) {
        model.nft?.let { bindData(it) }
    }

    @SuppressLint("SetTextI18n")
    private fun bindData(nft: Nft) {
        this.nft = nft
        with(binding) {
            val config = NftCollectionConfig.get(nft.contract.address) ?: return
            val title = "${config.name} #${nft.id.tokenId}"
            toolbar.title = title

            uiScope { updateSelectionState(isNftInSelection(nft)) }

            bindCover(nft)
            Glide.with(backgroundImage).load(nft.cover())
                .transition(DrawableTransitionOptions.withCrossFade(100))
                .transform(BlurTransformation(15, 30))
                .into(backgroundImage)

            titleView.text = title
            subtitleView.text = config.name
            Glide.with(collectionIcon).load(config.logo).transform(CenterCrop(), CircleCrop()).into(collectionIcon)
            descView.text = nft.desc()

            purchaseDate.text = "01.01.2022"
            purchasePrice.text = "1239.22"

            bindTags(nft)

            ioScope { updateSelectionState(isNftInSelection(nft)) }
        }
    }

    private fun bindCover(nft: Nft) {
        Glide.with(binding.coverView)
            .asBitmap()
            .load(nft.cover())
            .into(object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    ioScope {
                        coverRatio = "${resource.width}:${resource.height}"
                        val color = Palette.from(resource).generate().getDominantColor(R.color.text_sub.res2color())
                        uiScope {
                            updatePageColor(color)
                            binding.coverView.setImageBitmap(resource)
                        }
                    }
                }
            })
    }

    private fun updatePageColor(color: Int) {
        pageColor = color
        with(binding) {
            with(coverView.layoutParams as ConstraintLayout.LayoutParams) {
                dimensionRatio = coverRatio
                coverView.layoutParams = this
            }

            shareButton.setColorFilter(color)
            uiScope { updateSelectionState(isNftInSelection(nft!!)) }
            tags.children.forEach { tag ->
                tag.background.setTint(color)
                tag.findViewById<TextView>(R.id.title_view).setTextColor(color)
            }

            sendButton.iconTint = ColorStateList.valueOf(color)
            moreButton.iconTint = ColorStateList.valueOf(color)
        }
    }

    private fun bindTags(nft: Nft) {
        with(binding.tags) {
            nft.metadata.metadata.filter { !filterMetadata.contains(it.name.lowercase()) && it.value.isNotBlank() && !it.value.startsWith("https://") }
                .forEach { metadata ->
                    val tagView = (LayoutInflater.from(activity).inflate(R.layout.item_nft_tag, this, false) as ViewGroup)
                    tagView.background.setTint(pageColor)
                    tagView.background.alpha = (0.4f * 255).toInt()
                    val titleView = tagView.findViewById<TextView>(R.id.title_view)
                    val contentView = tagView.findViewById<TextView>(R.id.content_view)

                    titleView.setTextColor(pageColor)
                    titleView.text = metadata.name.uppercase()
                    contentView.text = metadata.value
                    addView(tagView)
                }
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
        uiScope {
            with(binding.collectButton) {
                isLiked = isSelected
                setUnLikeDrawableTint(pageColor)
                setCircleStartColorInt(pageColor)
            }
        }
    }

    private fun toggleNftSelection(nft: Nft) {
        ioScope {
            if (isNftInSelection(nft)) {
                NftSelectionManager.removeNftFromSelection(nft)
                updateSelectionState(false)
            } else {
                NftSelectionManager.addNftToSelection(nft)
                updateSelectionState(true)
            }
        }
    }

    private fun ActivityNftDetailBinding.updateToolbarColor(scrollY: Int) {
        val progress = min(scrollY / (screenHeight * 0.25f), 1.0f)
        toolbar.setBackgroundColor(
            ArgbEvaluator().evaluate(
                progress,
                R.color.transparent.res2color(),
                R.color.colorPrimary.res2color(),
            ) as Int
        )
        toolbar.setTitleTextColor(
            ArgbEvaluator().evaluate(
                progress,
                R.color.transparent.res2color(),
                R.color.text.res2color(),
            ) as Int
        )
    }
}