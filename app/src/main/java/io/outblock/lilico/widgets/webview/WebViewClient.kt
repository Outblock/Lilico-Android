package io.outblock.lilico.widgets.webview

import android.webkit.WebView
import com.just.agentweb.AgentWeb
import com.just.agentweb.WebViewClient as Client

class WebViewClient : Client() {

    private var agentWeb: AgentWeb? = null

    fun bindAgentWeb(agentWeb: AgentWeb) {
        this.agentWeb = agentWeb
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        view.executeJs(JS_FCL_EXTENSIONS)
        view.executeJs(JS_LISTEN_WINDOW_FCL_MESSAGE)
        view.executeJs(JS_LISTEN_FLOW_TRANSACTION_MESSAGE)
        view.executeJs(JS_LISTEN_FLOW_WALLET_TRANSACTION)
        super.onPageFinished(view, url)
    }
}