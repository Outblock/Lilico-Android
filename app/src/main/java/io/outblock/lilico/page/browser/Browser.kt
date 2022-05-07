package io.outblock.lilico.page.browser

import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.widget.FrameLayout
import io.outblock.lilico.databinding.LayoutBrowserBinding
import io.outblock.lilico.page.browser.model.BrowserFloatTabsModel
import io.outblock.lilico.page.browser.model.BrowserInputModel
import io.outblock.lilico.page.browser.model.BrowserModel
import io.outblock.lilico.page.browser.presenter.BrowserBubblePresenter
import io.outblock.lilico.page.browser.presenter.BrowserFloatTabsPresenter
import io.outblock.lilico.page.browser.presenter.BrowserInputPresenter
import io.outblock.lilico.page.browser.presenter.BrowserPresenter

class Browser : FrameLayout {
    private var binding: LayoutBrowserBinding = LayoutBrowserBinding.inflate(LayoutInflater.from(context))
    private var presenter: BrowserPresenter
    private var inputPresenter: BrowserInputPresenter
    private var floatTabsPresenter: BrowserFloatTabsPresenter
    private var bubblePresenter: BrowserBubblePresenter

    private val viewModel by lazy { BrowserViewModel() }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr)

    init {
        addView(binding.root)

        presenter = BrowserPresenter(this, binding, viewModel)
        inputPresenter = BrowserInputPresenter(binding, viewModel)
        floatTabsPresenter = BrowserFloatTabsPresenter(binding, viewModel)
        bubblePresenter = BrowserBubblePresenter(binding, viewModel)

        with(viewModel) {
            onUrlUpdateLiveData = {
                presenter.bind(BrowserModel(url = it))
                inputPresenter.bind(BrowserInputModel(onLoadNewUrl = it))
            }
            recommendWordsLiveData = { inputPresenter.bind(BrowserInputModel(recommendWords = it)) }
            onHideInputPanel = { inputPresenter.bind(BrowserInputModel(onHideInputPanel = true)) }
            onRemoveBrowserTab = {
                presenter.bind(BrowserModel(removeTab = it))
                floatTabsPresenter.bind(BrowserFloatTabsModel(removeTab = it))
            }
            onShowFloatTabs = { floatTabsPresenter.bind(BrowserFloatTabsModel(showTabs = true)) }
            onHideFloatTabs = {
                presenter.bind(BrowserModel(onFloatTabsHide = true))
                floatTabsPresenter.bind(BrowserFloatTabsModel(closeTabs = true))
            }
            onTabChange = { presenter.bind(BrowserModel(onTabChange = true)) }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        focus()
        inputPresenter.bind(BrowserInputModel(onPageAttach = true))
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        inputPresenter.bind(BrowserInputModel(onPageDetach = true))
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
        presenter.bind(BrowserModel(url = url))
    }

    fun open(searchBoxPosition: Point? = null) {
        searchBoxPosition?.let { presenter.bind(BrowserModel(searchBoxPosition = it)) }
    }

    fun onRelease() {
        presenter.bind(BrowserModel(onPageClose = true))
    }

    fun viewModel() = viewModel

    fun binding() = binding

    private fun focus() {
        isFocusableInTouchMode = true
        isFocusable = true
        requestFocus()
    }
}