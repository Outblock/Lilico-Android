package io.outblock.lilico.page.webview

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.widgets.webview.LilicoWebView

class WebViewActivity : BaseActivity() {
    private val url by lazy { intent.getStringExtra(EXTRA_URL)!! }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = FrameLayout(this)
        setContentView(root)
        LilicoWebView(root, url)
    }

    companion object {
        private const val EXTRA_URL = "EXTRA_URL"
        fun open(context: Context, url: String) {
            context.startActivity(Intent(context, WebViewActivity::class.java).apply {
                putExtra(EXTRA_URL, url)
            })
        }
    }
}