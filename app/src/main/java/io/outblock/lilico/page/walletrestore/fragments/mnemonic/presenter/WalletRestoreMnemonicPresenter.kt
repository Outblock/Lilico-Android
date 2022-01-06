package io.outblock.lilico.page.walletrestore.fragments.mnemonic.presenter

import android.graphics.Color
import android.graphics.Rect
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.FragmentWalletRestoreMnemonicBinding
import io.outblock.lilico.page.walletrestore.fragments.mnemonic.WalletRestoreMnemonicFragment
import io.outblock.lilico.page.walletrestore.fragments.mnemonic.WalletRestoreMnemonicViewModel
import io.outblock.lilico.page.walletrestore.fragments.mnemonic.adapter.MnemonicSuggestAdapter
import io.outblock.lilico.page.walletrestore.fragments.mnemonic.model.WalletRestoreMnemonicModel
import io.outblock.lilico.utils.extensions.dp2px
import io.outblock.lilico.utils.extensions.res2color
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.utils.listeners.SimpleTextWatcher
import io.outblock.lilico.widgets.itemdecoration.ColorDividerItemDecoration

class WalletRestoreMnemonicPresenter(
    private val fragment: WalletRestoreMnemonicFragment,
    private val binding: FragmentWalletRestoreMnemonicBinding,
) : BasePresenter<WalletRestoreMnemonicModel> {
    private val keyboardObserver by lazy { keyboardObserver() }
    private val rootView by lazy { fragment.requireActivity().findViewById<View>(R.id.rootView) }

    private val mnemonicAdapter by lazy { MnemonicSuggestAdapter() }

    private val viewModel by lazy { ViewModelProvider(fragment.requireActivity())[WalletRestoreMnemonicViewModel::class.java] }

    private val errorColor by lazy { R.color.error.res2color() }

    init {
        observeKeyboardVisible()
        with(binding.recyclerView) {
            adapter = mnemonicAdapter
            layoutManager = LinearLayoutManager(fragment.requireContext(), LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(ColorDividerItemDecoration(Color.TRANSPARENT, 10.dp2px().toInt(), LinearLayout.HORIZONTAL))
        }

        binding.editText.addTextChangedListener(object : SimpleTextWatcher() {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                viewModel.suggestMnemonic(s.toString())
                viewModel.invalidMnemonicCheck(s.toString())
            }
        })
    }

    override fun bind(model: WalletRestoreMnemonicModel) {
        model.mnemonicList?.let { mnemonicAdapter.setNewDiffData(it) }
        model.selectSuggest?.let { selectSuggest(it) }
        model.invalidWordList?.let { invalidWord(it) }
    }

    private fun invalidWord(array: List<Pair<Int, String>>) {
        with(binding.editText) {
            val textList = text.split(" ")
            val formatTextList = mutableListOf<CharSequence>()
            textList.forEachIndexed { index, s ->
                val isInvalid = array.firstOrNull { it.first == index } != null
                if (isInvalid) {
                    formatTextList.add(SpannableString(s).apply {
                        setSpan(
                            ForegroundColorSpan(errorColor),
                            0,
                            length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    })
                } else {
                    formatTextList.add(s)
                }
            }
            setText(formatTextList.joinToString(" ") { it })
        }
    }

    private fun selectSuggest(word: String) {
        with(binding.editText) {
            val text = text.split(" ").dropLast(1).toMutableList().apply { add(word) }
            setText(text.joinToString(" ") { it })
            setSelection(getText().length)
        }
    }

    fun unbind() {
        with(rootView.viewTreeObserver) {
            if (isAlive) {
                removeOnGlobalLayoutListener(keyboardObserver)
            }
        }
    }

    private fun observeKeyboardVisible() {
        rootView.post { rootView.viewTreeObserver.addOnGlobalLayoutListener(keyboardObserver) }
    }

    private fun keyboardObserver(): ViewTreeObserver.OnGlobalLayoutListener {
        return ViewTreeObserver.OnGlobalLayoutListener {
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)
            val contentHeight = rootView.rootView.height

            val isKeyboardVisible = contentHeight - rect.bottom > contentHeight * 0.15f
            binding.nextButton.setVisible(!isKeyboardVisible)
            binding.recyclerView.setVisible(isKeyboardVisible)
        }
    }
}