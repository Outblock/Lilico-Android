package io.outblock.lilico.page.webview.presenter

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.transition.Fade
import androidx.transition.Scene
import androidx.transition.TransitionManager
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.ActivityWebviewBinding
import io.outblock.lilico.page.webview.model.WebviewModel
import io.outblock.lilico.page.webview.saveRecentRecord
import io.outblock.lilico.page.webview.widgets.WebViewLayout
import io.outblock.lilico.page.webview.widgets.WebviewCallback
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.utils.logd
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
        model.onPageClose?.let { webview().saveRecentRecord() }
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
            logd("webview", "onProgressChange:$progress")
            val params = layoutParams as ConstraintLayout.LayoutParams
            params.matchConstraintPercentWidth = progress
            requestLayout()
            TransitionManager.go(Scene(binding.toolbarContent), Fade().apply {
                duration = if (progress == 1f) 400 else 200
                startDelay = if (progress == 1f) 400 else -1
            })
            postDelayed({ setVisible(progress != 1f) }, if (progress == 1f) 400 else 0)
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