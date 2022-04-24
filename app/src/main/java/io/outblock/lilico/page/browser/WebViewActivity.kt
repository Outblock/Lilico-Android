package io.outblock.lilico.page.browser

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.databinding.ActivityWebviewBinding
import io.outblock.lilico.page.browser.model.WebviewModel
import io.outblock.lilico.page.browser.presenter.WebviewPresenter
import io.outblock.lilico.utils.logd

class WebViewActivity : BaseActivity() {
    private lateinit var binding: ActivityWebviewBinding
    private lateinit var presenter: WebviewPresenter
    private lateinit var viewModel: WebviewViewModel

    private val url by lazy { intent.getStringExtra(EXTRA_URL)!! }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter = WebviewPresenter(this, binding)
        viewModel = ViewModelProvider(this)[WebviewViewModel::class.java].apply {
            urlLiveData.observe(this@WebViewActivity) { presenter.bind(WebviewModel(url = it)) }
        }

        presenter.bind(WebviewModel(url = url))
    }

    override fun onBackPressed() {
        if (presenter.handleBackPressed()) {
            return
        }
        super.onBackPressed()
    }

    override fun finish() {
        logd("webview", "finish")
        presenter.bind(WebviewModel(onPageClose = true))
        super.finish()
    }

    companion object {
        private const val EXTRA_URL = "EXTRA_URL"
        fun open(context: Context, url: String) {
            context.startActivity(Intent(context, WebViewActivity::class.java).apply {
                putExtra(EXTRA_URL, url)
            })
        }
    }
}