package io.outblock.lilico.widgets.webview

import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.just.agentweb.AgentWeb
import com.just.agentweb.DefaultWebClient
import io.outblock.lilico.utils.findActivity

class LilicoWebView(
    private val parentView: ViewGroup,
    private val url: String,
) {
    private lateinit var agentWeb: AgentWeb

    private val activity by lazy { findActivity(parentView) as FragmentActivity }

    private val webViewClient by lazy { WebViewClient() }

    init {
        agentWeb = createWebView().apply {
            jsInterfaceHolder.addJavaObject("android", JsInterface(this.webCreator.webView))
            webViewClient.bindAgentWeb(this)
        }
    }

    private fun createWebView(): AgentWeb {
        return AgentWeb.with(activity)
            .setAgentWebParent(parentView, -1, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
            .useDefaultIndicator(-1, 3)
            .setAgentWebWebSettings(WebViewSettings())
            .setWebViewClient(webViewClient)
            .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
            .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)
            .interceptUnkownUrl()
            .createAgentWeb()
            .ready()
            .go(url)
    }
}