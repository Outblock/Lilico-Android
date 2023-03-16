package io.outblock.lilico.widgets.webview

import com.just.agentweb.AbsAgentWebSettings
import com.just.agentweb.AgentWeb

class WebViewSettings : AbsAgentWebSettings() {
    private var webView: AgentWeb? = null

    override fun bindAgentWebSupport(agentWeb: AgentWeb?) {
        webView = agentWeb
    }
}