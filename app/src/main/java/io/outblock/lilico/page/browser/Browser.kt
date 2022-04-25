package io.outblock.lilico.page.browser

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.widget.FrameLayout
import io.outblock.lilico.databinding.LayoutBrowserBinding
import io.outblock.lilico.page.browser.model.WebviewModel
import io.outblock.lilico.page.browser.presenter.WebviewPresenter

class Browser : FrameLayout {
    private var binding: LayoutBrowserBinding = LayoutBrowserBinding.inflate(LayoutInflater.from(context))
    private var presenter: WebviewPresenter

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr)

    init {
        addView(binding.root)

        presenter = WebviewPresenter(this, binding)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        focus()
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_BACK) {
            if (presenter.handleBackPressed()) {
                return true
            }
        }
        return super.dispatchKeyEvent(event)
    }

    fun loadUrl(url: String) {
        focus()
        presenter.bind(WebviewModel(url = url))
    }

    fun onRelease() {
        presenter.bind(WebviewModel(onPageClose = true))
    }

    private fun focus() {
        isFocusableInTouchMode = true
        isFocusable = true
        requestFocus()
    }
}