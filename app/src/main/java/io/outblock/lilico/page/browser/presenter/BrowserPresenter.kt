package io.outblock.lilico.page.browser.presenter

import android.animation.ObjectAnimator
import android.view.ViewGroup
import com.zackratos.ultimatebarx.ultimatebarx.addNavigationBarBottomPadding
import com.zackratos.ultimatebarx.ultimatebarx.addStatusBarTopPadding
import com.zackratos.ultimatebarx.ultimatebarx.statusBarHeight
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.LayoutBrowserBinding
import io.outblock.lilico.page.browser.*
import io.outblock.lilico.page.browser.model.BrowserModel
import io.outblock.lilico.page.browser.tools.*
import io.outblock.lilico.page.browser.widgets.BrowserPopupMenu
import io.outblock.lilico.page.browser.widgets.WebviewCallback
import io.outblock.lilico.page.window.WindowFrame
import io.outblock.lilico.page.window.bubble.tools.inBubbleStack
import io.outblock.lilico.utils.extensions.isVisible
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.widgets.RenderScriptBlur2
import kotlin.math.abs

class BrowserPresenter(
    private val browser: Browser,
    private val binding: LayoutBrowserBinding,
    private val viewModel: BrowserViewModel,
) : BasePresenter<BrowserModel>, WebviewCallback {

    private fun webview() = browserTabLast()?.webView

    private var toolbarTranslationAnimator: ObjectAnimator? = null

    init {
        with(binding) {
            contentWrapper.post {
                contentWrapper.addStatusBarTopPadding()
                root.addNavigationBarBottomPadding()
                with(root.layoutParams as ViewGroup.MarginLayoutParams) {
                    bottomMargin = statusBarHeight
                    root.layoutParams = this
                }
            }
            with(binding.toolbar) {
                refreshButton.setOnClickListener { webview()?.reload() }
                backButton.setOnClickListener { handleBackPressed() }
                homeButton.setOnClickListener { popBrowserLastTab() }
                floatButton.setOnClickListener { shrinkBrowser() }
                menuButton.setOnClickListener { browserTabLast()?.let { BrowserPopupMenu(menuButton, it).show() } }
                blurView.setupWith(binding.webviewContainer)
                    .setBlurAlgorithm(RenderScriptBlur2(root.context)).setBlurAutoUpdate(true).setBlurRadius(20.0f)
            }
        }
    }

    override fun bind(model: BrowserModel) {
        model.url?.let { onOpenNewUrl(it) }
        model.onPageClose?.let { browserTabLast()?.webView?.saveRecentRecord() }
        model.searchBoxPosition?.let { }
        model.removeTab?.let { removeTab(it) }
        model.onTabChange?.let { onBrowserTabChange() }
    }

    override fun onScrollChange(scrollY: Int, offset: Int) {
        if (abs(offset) > 300) return
        val max = binding.toolbar.root.height * 2f
        with(binding.toolbar.root) {
            // offset > 0 -> scroll up
            if (toolbarTranslationAnimator?.isRunning == true || (offset > 0 && translationY > 0) || (offset < 0 && translationY == 0.0f)) {
                return
            }
            val toTranslationY = if (offset > 0) height.toFloat() * 2 else 0.0f
            toolbarTranslationAnimator = ObjectAnimator.ofFloat(this, "translationY", translationY, toTranslationY).apply {
                duration = 200
                start()
            }
        }
    }

    override fun onProgressChange(progress: Float) {
        binding.toolbar.progressBar.setProgress(progress)
    }

    override fun onTitleChange(title: String) {
        binding.toolbar.titleView.text = title.ifBlank { webview()?.url }
    }

    override fun onPageUrlChange(url: String, isReload: Boolean) {
        binding.toolbar.root.translationY = 0f
    }

    private fun removeTab(tab: BrowserTab) {
        popBrowserTab(tab.id)
        showBrowserLastTab()
    }

    private fun onOpenNewUrl(url: String) {
        WindowFrame.browserContainer()?.setVisible(true)
        newAndPushBrowserTab(url)?.let { tab ->
            tab.webView.setWebViewCallback(this@BrowserPresenter)
            expandBrowser()
            onTitleChange(tab.title() ?: (tab.url().orEmpty()))
        }
    }

    private fun onBrowserTabChange() {
        WindowFrame.browserContainer()?.setVisible(true)
        onTitleChange(webview()?.title.orEmpty())
    }

    fun handleBackPressed(): Boolean {
        // bubble mode
        if (!binding.root.isVisible()) {
            return false
        }
        val lastTab = browserTabLast()
        when {
            isSearchBoxVisible() -> viewModel.hideInputPanel()
            webview()?.canGoBack() ?: false -> webview()?.goBack()
            lastTab != null && inBubbleStack(lastTab) -> shrinkBrowser()
            lastTab != null -> removeTabAndHideBrowser(lastTab.id)
            else -> return false
        }
        return true
    }

    private fun isSearchBoxVisible() = binding.inputLayout.root.isVisible()
}