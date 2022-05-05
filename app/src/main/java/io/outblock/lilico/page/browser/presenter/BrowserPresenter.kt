package io.outblock.lilico.page.browser.presenter

import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.transition.Fade
import androidx.transition.Scene
import androidx.transition.TransitionManager
import com.zackratos.ultimatebarx.ultimatebarx.addNavigationBarBottomPadding
import com.zackratos.ultimatebarx.ultimatebarx.addStatusBarTopPadding
import com.zackratos.ultimatebarx.ultimatebarx.navigationBarHeight
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.LayoutBrowserBinding
import io.outblock.lilico.page.browser.Browser
import io.outblock.lilico.page.browser.BrowserViewModel
import io.outblock.lilico.page.browser.model.BrowserModel
import io.outblock.lilico.page.browser.releaseBrowser
import io.outblock.lilico.page.browser.saveRecentRecord
import io.outblock.lilico.page.browser.tools.expandWebView
import io.outblock.lilico.page.browser.tools.shrinkWebView
import io.outblock.lilico.page.browser.widgets.WebviewCallback
import io.outblock.lilico.utils.extensions.isVisible
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.utils.logd
import io.outblock.lilico.widgets.floatwindow.widgets.WindowRemoveLayout
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class BrowserPresenter(
    private val browser: Browser,
    private val binding: LayoutBrowserBinding,
    private val viewModel: BrowserViewModel,
) : BasePresenter<BrowserModel>, WebviewCallback {

    private val webview = binding.webview

    private val removeLayout by lazy { WindowRemoveLayout(binding.root, binding.floatBubble) { releaseBrowser() } }

    init {
        with(binding) {
            contentWrapper.post {
                contentWrapper.addStatusBarTopPadding()
                contentWrapper.addNavigationBarBottomPadding()
                with(contentWrapper.layoutParams as ViewGroup.MarginLayoutParams) {
                    bottomMargin = navigationBarHeight
                    contentWrapper.layoutParams = this
                }
            }
            with(binding.toolbar) {
                refreshButton.setOnClickListener { webview.reload() }
                backButton.setOnClickListener { if (webview.canGoBack()) webview.goBack() else releaseBrowser() }
                homeButton.setOnClickListener { releaseBrowser() }
                floatButton.setOnClickListener { switchBubble() }
            }
            floatBubble.setOnClickListener { switchBubble() }
            floatBubble.setOnDragListener(removeLayout)
            webview.setWebViewCallback(this@BrowserPresenter)
        }
    }

    override fun bind(model: BrowserModel) {
        model.url?.let { onOpenNewUrl(it) }
        model.onPageClose?.let { webview.saveRecentRecord() }
        model.searchBoxPosition?.let { }
    }

    private fun onOpenNewUrl(url: String) {
        webview.loadUrl(url)
        with(binding) {
            switchBubble(true)
        }
    }

    override fun onScrollChange(scrollY: Int, offset: Int) {
        if (abs(offset) > 300) return
        val max = binding.toolbar.root.height * 2f
        with(binding.toolbar.root) {
            translationY = min(max, max(0f, translationY + offset))
        }
    }

    override fun onProgressChange(progress: Float) {
        with(binding.toolbar.progressBar) {
            logd("webview", "onProgressChange:$progress")
            val params = layoutParams as ConstraintLayout.LayoutParams
            params.matchConstraintPercentWidth = progress
            requestLayout()
            TransitionManager.go(Scene(binding.toolbar.toolbarContent), Fade().apply {
                duration = if (progress == 1f) 400 else 200
                startDelay = if (progress == 1f) 400 else -1
            })
            postDelayed({ setVisible(progress != 1f) }, if (progress == 1f) 400 else 0)
        }
    }

    override fun onTitleChange(title: String) {
        binding.toolbar.titleView.text = title.ifBlank { webview.url }
    }

    override fun onPageUrlChange(url: String, isReload: Boolean) {
        binding.toolbar.root.translationY = 0f
    }

    fun handleBackPressed(): Boolean {
        // bubble mode
        if (!binding.contentWrapper.isVisible()) {
            return false
        }
        when {
            isSearchBoxVisible() -> viewModel.hideInputPanel()
            webview.canGoBack() -> webview.goBack()
            else -> releaseBrowser()
        }
        return true
    }

    private fun switchBubble(isExpand: Boolean? = null) {
        if (!binding.root.isAttachedToWindow) {
            return
        }
        with(binding) {
            val expand = isExpand ?: !contentWrapper.isVisible()
            if (expand) expandWebView(contentWrapper, floatBubble) else shrinkWebView(contentWrapper, floatBubble)
        }
    }

    private fun isSearchBoxVisible() = binding.inputLayout.root.isVisible()
}