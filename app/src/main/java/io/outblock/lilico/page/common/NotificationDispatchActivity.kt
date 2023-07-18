package io.outblock.lilico.page.common

import android.graphics.Color
import android.os.Bundle
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zackratos.ultimatebarx.ultimatebarx.UltimateBarX
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.manager.app.isTestnet
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.safeRun

private val TAG = NotificationDispatchActivity::class.java.simpleName

class NotificationDispatchActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        UltimateBarX.with(this).color(Color.TRANSPARENT).fitWindow(false).light(false).applyStatusBar()

        val data = intent.getStringExtra("data")

        logd(TAG, "data: $data")
        safeRun {
            val json = Gson().fromJson<Map<String, Any>>(data, object : TypeToken<Map<String, Any>>() {}.type)
            dispatch(json)
        }

        finish()
        logd("xxx", "data: $data")
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    private fun dispatch(data: Map<String, Any>) {
        val type = (data["type"] as? String) ?: return
        when (type) {
            "sent" -> WebViewActivity.launch(this, (data["transactionId"] as? String)?.toFlowScanTransactionUrl())
        }
    }

    private fun String.toFlowScanTransactionUrl(): String {
        return if (isTestnet()) {
            "https://testnet.flowscan.org/transaction/$this"
        } else "https://flowscan.org/transaction/$this"
    }
}