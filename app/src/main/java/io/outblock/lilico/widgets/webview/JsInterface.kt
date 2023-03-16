package io.outblock.lilico.widgets.webview

import android.graphics.Color
import android.webkit.JavascriptInterface
import androidx.core.graphics.toColorInt
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nftco.flow.sdk.FlowTransactionStatus
import io.outblock.lilico.R
import io.outblock.lilico.manager.transaction.TransactionState
import io.outblock.lilico.manager.transaction.TransactionStateManager
import io.outblock.lilico.page.browser.widgets.LilicoWebView
import io.outblock.lilico.page.window.bubble.tools.pushBubbleStack
import io.outblock.lilico.utils.extensions.res2color
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.uiScope
import io.outblock.lilico.widgets.webview.fcl.FclMessageHandler
import io.outblock.lilico.widgets.webview.fcl.authzTransaction

class JsInterface(
    private val webView: LilicoWebView,
) {

    private val messageHandler by lazy { FclMessageHandler(webView) }

    @JavascriptInterface
    fun message(data: String) {
        messageHandler.onHandleMessage(data)
    }

    @JavascriptInterface
    fun transaction(json: String) {
        logd(TAG, "transaction: $json")
        ioScope {
            val authzTransaction = authzTransaction() ?: return@ioScope
            val tid = Gson().fromJson<Map<String, Any>>(json, object : TypeToken<Map<String, Any>>() {}.type)["txId"] as String
            val transactionState = TransactionState(
                transactionId = tid,
                time = System.currentTimeMillis(),
                state = FlowTransactionStatus.UNKNOWN.num,
                type = TransactionState.TYPE_FCL_TRANSACTION,
                data = Gson().toJson(authzTransaction),
            )
            uiScope {
                if (TransactionStateManager.getTransactionStateById(tid) != null) return@uiScope
                TransactionStateManager.newTransaction(transactionState)
                pushBubbleStack(transactionState)
            }
        }
    }

    @JavascriptInterface
    fun windowColor(color: String) {
        logd(TAG, "window color:$color")
        val colorInt = color.toColorInt()
        webView.onWindowColorChange(if (colorInt == Color.BLACK) R.color.deep_bg.res2color() else colorInt)
    }

    companion object {
        private val TAG = JsInterface::class.java.simpleName
    }
}