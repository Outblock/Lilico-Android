package io.outblock.lilico.page.browser.presenter

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Point
import android.graphics.Rect
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Fade
import androidx.transition.Scene
import androidx.transition.TransitionManager
import com.zackratos.ultimatebarx.ultimatebarx.navigationBarHeight
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.LayoutBrowserBinding
import io.outblock.lilico.page.browser.BrowserViewModel
import io.outblock.lilico.page.browser.adapter.BrowserRecommendWordsAdapter
import io.outblock.lilico.page.browser.browserViewModel
import io.outblock.lilico.page.browser.model.BrowserInputModel
import io.outblock.lilico.page.browser.releaseBrowser
import io.outblock.lilico.page.browser.toSearchUrl
import io.outblock.lilico.page.browser.tools.browserTabsCount
import io.outblock.lilico.utils.extensions.*
import io.outblock.lilico.widgets.itemdecoration.ColorDividerItemDecoration

class BrowserInputPresenter(
    private val binding: LayoutBrowserBinding,
    private val viewModel: BrowserViewModel,
) : BasePresenter<BrowserInputModel> {

    private val inputBinding = binding.inputLayout

    private val recommendAdapter by lazy { BrowserRecommendWordsAdapter() }

    private val keyboardObserver by lazy { keyboardObserver() }

    init {
        binding.toolbar.textWrapper.setOnClickListener {
            binding.inputLayout.root.setVisible()
            inputBinding.inputView.requestFocus()
            inputBinding.inputView.showKeyboard()
        }
        with(inputBinding.recyclerView) {
            adapter = recommendAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
            addItemDecoration(ColorDividerItemDecoration(Color.TRANSPARENT, 5.dp2px().toInt(), ColorDividerItemDecoration.VERTICAL))
        }

        with(inputBinding.inputView) {
            doOnTextChanged { _, _, _, _ -> onKeywordChange() }
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val keyword = text.toString()
                    clearPage()
                    viewModel.updateUrl(keyword.toSearchUrl())
                }
                return@setOnEditorActionListener false
            }
        }

        with(inputBinding) {
            clearButton.setOnClickListener { inputView.setText("") }
            cancelButton.setOnClickListener { clearPage() }
        }
    }

    private fun onKeywordChange() {
        with(inputBinding) {
            val keyword = inputView.text.toString()
            viewModel.queryRecommendWord(keyword)

            val cancelVisible = keyword.isNotEmpty()
            if (cancelWrapper.isVisible() != cancelVisible) {
                TransitionManager.go(Scene(inputBinding.root), Fade().apply { duration = 150 })
                cancelWrapper.setVisible(cancelVisible)
            }
        }
    }

    override fun bind(model: BrowserInputModel) {
        model.onLoadNewUrl?.let { clearPage() }
        model.onHideInputPanel?.let { clearPage() }
        model.recommendWords?.let { recommendAdapter.setNewDiffData(it) }
        model.onPageAttach?.let { observeKeyboardVisible() }
        model.onPageDetach?.let { removeObserveKeyboardVisible() }
        model.searchBoxPosition?.let { openFromSearchBox(it) }
    }

    private fun observeKeyboardVisible() {
        binding.root.post { binding.root.viewTreeObserver.addOnGlobalLayoutListener(keyboardObserver) }
    }


    private fun removeObserveKeyboardVisible() {
        with(binding.root.viewTreeObserver) {
            if (isAlive) {
                removeOnGlobalLayoutListener(keyboardObserver)
            }
        }
    }

    private fun keyboardObserver(): ViewTreeObserver.OnGlobalLayoutListener {
        return ViewTreeObserver.OnGlobalLayoutListener {
            val rect = Rect()
            binding.root.getWindowVisibleDisplayFrame(rect)
            val contentHeight = binding.root.rootView.height

            val isKeyboardVisible = contentHeight - rect.bottom > contentHeight * 0.15f
            with(inputBinding.root) {
                inputBinding.root.setPadding(
                    paddingLeft,
                    paddingTop,
                    paddingRight,
                    if (isKeyboardVisible) contentHeight - rect.bottom - navigationBarHeight else 0,
                )
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun clearPage() {
        with(inputBinding.inputView) {
            hideKeyboard()
            setText("")
        }
        recommendAdapter.setNewDiffData(emptyList())
        recommendAdapter.notifyDataSetChanged()
        inputBinding.root.setVisible(false)
        browserViewModel()?.onSearchBoxHide()

        if (binding.webviewContainer.childCount == 0) {
            binding.root.setVisible(false)
        }

        if (browserTabsCount() == 0) {
            releaseBrowser()
        }
    }

    private fun openFromSearchBox(point: Point) {
        binding.root.setVisible()
        binding.inputLayout.root.setVisible()
        binding.inputLayout.inputView.requestFocus()
        binding.inputLayout.inputView.showKeyboard()
    }
}