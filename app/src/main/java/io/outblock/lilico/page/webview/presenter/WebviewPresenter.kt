package io.outblock.lilico.page.webview.presenter

import android.animation.ObjectAnimator
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.ActivityWebviewBinding
import io.outblock.lilico.page.webview.model.WebviewModel
import io.outblock.lilico.page.webview.widgets.WebViewLayout
import io.outblock.lilico.page.webview.widgets.WebviewCallback
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class WebviewPresenter(
    private val activity: FragmentActivity,
    private val binding: ActivityWebviewBinding,
) : BasePresenter<WebviewModel>, WebviewCallback {

    private var webviewLayout: WebViewLayout

    private fun webview() = webviewLayout.webview()

    init {
        with(binding) {
            webviewLayout = WebViewLayout(binding.root).apply { setWebViewCallback(this@WebviewPresenter) }
            refreshButton.setOnClickListener { webview().reload() }
            backButton.setOnClickListener { if (webview().canGoBack()) webview().goBack() else activity.finish() }
            homeButton.setOnClickListener { webview().loadUrl("https://google.com") }
        }
    }

    override fun bind(model: WebviewModel) {
        model.url?.let { webviewLayout.updateUrl(it) }
    }

    override fun onScrollChange(scrollY: Int, offset: Int) {
        if (abs(offset) > 300) return
        val max = binding.toolbar.height * 2f
        with(binding.toolbar) {
            translationY = min(max, max(0f, translationY + offset))
        }
    }

    override fun onProgressChange(progress: Float) {
        with(binding.progressBar) {
            (layoutParams as ConstraintLayout.LayoutParams).matchConstraintPercentWidth = progress
            if (progress == 1f) {
                ObjectAnimator.ofFloat(this, View.ALPHA, 1f, 0f).apply {
                    duration = 500
                    start()
                }
            } else {
                alpha = 1f
            }
            requestLayout()
        }
    }

    override fun onTitleChange(title: String) {
        binding.titleView.text = title.ifBlank { webview().url }
    }

    override fun onPageUrlChange(url: String, isReload: Boolean) {
        binding.toolbar.translationY = 0f
    }

    fun handleBackPressed(): Boolean {
        if (webview().canGoBack()) {
            webview().goBack()
            return true
        }
        return false
    }

}