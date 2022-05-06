package io.outblock.lilico.page.browser

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.outblock.lilico.page.browser.model.DockDuckGoRecommend
import io.outblock.lilico.page.browser.model.RecommendModel
import io.outblock.lilico.page.browser.tools.BrowserTab
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.uiScope
import io.outblock.lilico.widgets.floatwindow.FloatWindow
import java.net.URL
import java.net.URLEncoder

class BrowserViewModel {
    internal var onUrlUpdateLiveData: ((String) -> Unit)? = null

    internal var onHideInputPanel: (() -> Unit)? = null

    internal var recommendWordsLiveData: ((List<RecommendModel>) -> Unit)? = null

    internal var onRemoveBrowserTab: ((BrowserTab) -> Unit)? = null

    internal var onShowFloatTabs: (() -> Unit)? = null
    internal var onHideFloatTabs: (() -> Unit)? = null

    internal var onTabChange: (() -> Unit)? = null

    private var searchKeyword = ""

    fun updateUrl(url: String) {
        onUrlUpdateLiveData?.invoke(url)
    }

    fun queryRecommendWord(keyword: String) {
        this.searchKeyword = keyword
        ioScope {
            runCatching {
                if (keyword.isBlank()) {
                    uiScope { recommendWordsLiveData?.invoke(emptyList()) }
                    return@ioScope
                }

                val list = queryRecommendWordInternal(keyword)
                if (FloatWindow.isShowing(BROWSER_TAG) && searchKeyword == keyword) {
                    uiScope { recommendWordsLiveData?.invoke(list.map { RecommendModel(it, keyword, this@BrowserViewModel) }) }
                }
            }
        }
    }

    private fun queryRecommendWordInternal(keyword: String): List<String> {
        val json = URL("https://ac.duckduckgo.com/ac?q=${URLEncoder.encode(keyword)}&type=json").openStream().bufferedReader().use { it.readText() }
        return Gson().fromJson<List<DockDuckGoRecommend>>(json, object : TypeToken<List<DockDuckGoRecommend>>() {}.type).map { it.phrase }
    }

    fun hideInputPanel() {
        onHideInputPanel?.invoke()
    }

    fun popTab(tab: BrowserTab) {
        onRemoveBrowserTab?.invoke(tab)
    }

    fun showFloatTabs() {
        onShowFloatTabs?.invoke()
    }

    fun onHideFloatTabs() {
        onHideFloatTabs?.invoke()
    }

    fun onTabChange() {
        onTabChange?.invoke()
    }
}