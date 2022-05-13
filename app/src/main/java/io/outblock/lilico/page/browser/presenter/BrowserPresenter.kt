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
import io.outblock.lilico.page.browser.*
import io.outblock.lilico.page.browser.model.BrowserModel
import io.outblock.lilico.page.browser.tools.*
import io.outblock.lilico.page.browser.widgets.BrowserPopupMenu
import io.outblock.lilico.page.browser.widgets.WebviewCallback
import io.outblock.lilico.page.window.WindowFrame
import io.outblock.lilico.page.window.bubble.tools.inBubbleStack
import io.outblock.lilico.utils.extensions.isVisible
import io.outblock.lilico.utils.extensions.setVisible
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class BrowserPresenter(
    private val browser: Browser,
    private val binding: LayoutBrowserBinding,
    private val viewModel: BrowserViewModel,
) : BasePresenter<BrowserModel>, WebviewCallback {

    private fun webview() = browserTabLast()?.webView

    init {
        with(binding) {
            contentWrapper.post {
                contentWrapper.addStatusBarTopPadding()
                root.addNavigationBarBottomPadding()
                with(root.layoutParams as ViewGroup.MarginLayoutParams) {
                    bottomMargin = navigationBarHeight
                    root.layoutParams = this
                }
            }
            with(binding.toolbar) {
                refreshButton.setOnClickListener { webview()?.reload() }
                backButton.setOnClickListener { handleBackPressed() }
                homeButton.setOnClickListener { popBrowserLastTab() }
                floatButton.setOnClickListener { shrinkBrowser() }
                menuButton.setOnClickListener { browserTabLast()?.let { BrowserPopupMenu(menuButton, it).show() } }
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
            translationY = min(max, max(0f, translationY + offset))
        }
    }

    override fun onProgressChange(progress: Float) {
        with(binding.toolbar.progressBar) {
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