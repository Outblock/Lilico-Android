package io.outblock.lilico.page.main.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import io.outblock.lilico.R

class DrawerItem : FrameLayout {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet? = null) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.DrawerItem, defStyleAttr, 0)
        val text = array.getString(R.styleable.DrawerItem_drawer_item_text).orEmpty()
        val image = array.getResourceId(R.styleable.DrawerItem_drawer_item_icon, 0)
        array.recycle()

        LayoutInflater.from(context).inflate(R.layout.widget_drawer_item, this)
        val imageView = findViewById<ImageView>(R.id.image_view)
        val textView = findViewById<TextView>(R.id.text_view)

        imageView.setImageResource(image)
        textView.text = text
    }

    override fun setOnClickListener(l: OnClickListener?) {
        findViewById<View>(R.id.wrapper_view).setOnClickListener(l)
    }

}