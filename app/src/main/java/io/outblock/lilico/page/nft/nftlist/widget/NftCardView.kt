package io.outblock.lilico.page.nft.nftlist.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.children
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.card.MaterialCardView
import io.outblock.lilico.R
import io.outblock.lilico.network.model.Nft
import io.outblock.lilico.page.nft.nftlist.cover

class NftCardView : MaterialCardView {

    private val imageView by lazy { findViewById<ImageView>(R.id.image_view) }

    private var nft: Nft? = null

    private var cover: String? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
      : super(context, attrs, defStyleAttr)

    init {
        LayoutInflater.from(context).inflate(R.layout.item_nft_selections, this)
    }

    fun bindData(nft: Nft) {
        if (this.nft == nft) {
            return
        }
        this.nft = nft
        cover = nft.cover()
        Glide.with(imageView)
            .load(nft.cover())
            .placeholder(getSameDrawable())
            .transition(DrawableTransitionOptions.withCrossFade(50))
            .into(imageView)
    }

    private fun getSameDrawable(): Drawable? {
        (parent as ViewGroup).children.forEach {
            if (it is NftCardView && it.id() == id() && it != this) {
                return it.findViewById<ImageView>(R.id.image_view).drawable
            }
        }
        return null
    }

    fun id() = cover
}