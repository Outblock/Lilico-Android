package io.outblock.lilico.widgets.popup

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lxj.xpopup.core.AttachPopupView
import com.lxj.xpopup.interfaces.OnSelectListener
import com.lxj.xpopup.widget.VerticalRecyclerView
import io.outblock.lilico.R
import io.outblock.lilico.base.recyclerview.BaseAdapter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.base.recyclerview.getItemView
import io.outblock.lilico.utils.extensions.setVisible

@SuppressLint("ViewConstructor")
class PopupListView(
    context: Context,
    private val itemData: List<ItemData>
) : AttachPopupView(context) {
    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.recyclerView) }
    private val adapter by lazy { Adapter() }

    private var selectListener: OnSelectListener? = null

    override fun getImplLayoutId(): Int = R.layout.popup_menu_root

    override fun onCreate() {
        with(recyclerView) {
            layoutManager = LinearLayoutManager(context)
            this.adapter = this@PopupListView.adapter
        }
        adapter.setNewDiffData(itemData)
    }

    override fun applyDarkTheme() {
        super.applyDarkTheme()
        (recyclerView as VerticalRecyclerView).setupDivider(true)
    }

    override fun applyLightTheme() {
        super.applyLightTheme()
        (recyclerView as VerticalRecyclerView).setupDivider(false)
    }

    fun setOnSelectListener(selectListener: OnSelectListener): PopupListView {
        this.selectListener = selectListener
        return this
    }

    private inner class Adapter : BaseAdapter<ItemData>() {
        inner class ViewHolder(val view: View) : BaseViewHolder(view) {
            private val iconView by lazy { view.findViewById<ImageView>(R.id.iv_image) }
            private val titleView by lazy { view.findViewById<TextView>(R.id.tv_text) }

            fun bind(item: ItemData) {
                val icon = item.iconRes ?: item.iconUrl
                Glide.with(iconView).load(icon).into(iconView)
                iconView.setVisible(icon != null)
                item.iconTint?.let { iconView.setColorFilter(it) }
                titleView.text = item.title
                view.setOnClickListener {
                    selectListener?.onSelect(layoutPosition, item.title)
                    if (popupInfo.autoDismiss) dismiss()
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return ViewHolder(parent.getItemView(R.layout.popup_menu_item))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val data = getItem(position)
            (holder as ViewHolder).bind(data)
        }
    }

    class ItemData(
        val title: String,
        val iconRes: Int? = null,
        val iconUrl: String? = null,
        val iconTint: Int? = null,
    )
}