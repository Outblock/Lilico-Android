package io.outblock.lilico.page.component.deeplinking

import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.zackratos.ultimatebarx.ultimatebarx.UltimateBarX
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.page.main.MainActivity
import io.outblock.lilico.utils.logd

private val TAG = DeepLinkingActivity::class.java.simpleName

class DeepLinkingActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(View(this))
        UltimateBarX.with(this).color(Color.TRANSPARENT).fitWindow(false).light(false).applyStatusBar()

        val uri = intent.data
        if (uri == null) {
            finish()
            return
        }

        logd(TAG, "uri:$uri")

        MainActivity.launch(this)
        dispatchDeepLinking(uri)
        finish()
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}