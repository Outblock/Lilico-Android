package io.outblock.lilico.page.swap

import android.view.inputmethod.EditorInfo
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import io.outblock.lilico.R
import io.outblock.lilico.databinding.ActivitySwapBinding
import io.outblock.lilico.manager.coin.FlowCoin
import io.outblock.lilico.utils.extensions.hideKeyboard
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.utils.extensions.toSafeFloat
import io.outblock.lilico.utils.findActivity
import io.outblock.lilico.utils.formatPrice
import io.outblock.lilico.utils.isLegalAmountNumber


fun ActivitySwapBinding.viewModel(): SwapViewModel {
    return ViewModelProvider(findActivity(root) as FragmentActivity)[SwapViewModel::class.java]
}

fun ActivitySwapBinding.bindInputListener() {
    bindFromListener()
    bindToListener()
}

fun ActivitySwapBinding.updateFromCoin(coin: FlowCoin) {
    Glide.with(fromCoinIcon).load(coin.icon).into(fromCoinIcon)
    fromCoinName.text = coin.symbol.uppercase()
    legalCheck()
}

fun ActivitySwapBinding.updateToCoin(coin: FlowCoin) {
    Glide.with(toCoinIcon).load(coin.icon).into(toCoinIcon)
    toCoinName.text = coin.symbol.uppercase()
    legalCheck()
}

fun ActivitySwapBinding.updateFromAmount(amount: Float) {
    fromInput.setText(amount.formatPrice())
}

fun ActivitySwapBinding.updateToAmount(amount: Float) {
    toInput.setText(amount.formatPrice())
}

fun ActivitySwapBinding.updateProgressState(isLoading: Boolean) {
    progressBar.setVisible(isLoading)
    switchButton.setVisible(!isLoading, invisible = true)
}

private fun ActivitySwapBinding.bindFromListener() {
    with(fromInput) {
        doOnTextChanged { _, _, _, _ ->
            if (!hasFocus()) return@doOnTextChanged
            viewModel().updateFromAmount(fromAmount())
            updateAmountPrice()
            legalCheck()
        }
        setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                hideKeyboard()
                legalCheck()
            }
            return@setOnEditorActionListener false
        }
    }
}

private fun ActivitySwapBinding.bindToListener() {
    with(toInput) {
        doOnTextChanged { _, _, _, _ ->
            if (!hasFocus()) return@doOnTextChanged
            viewModel().updateToAmount(toAmount())
            updateAmountPrice()
            legalCheck()
        }
        setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                hideKeyboard()
                legalCheck()
            }
            return@setOnEditorActionListener false
        }
    }
}

fun ActivitySwapBinding.onBalanceUpdate() {

}

fun ActivitySwapBinding.onCoinRateUpdate() {
    updateAmountPrice()
}

fun ActivitySwapBinding.updateAmountPrice() {
    val amount = fromInput.text.toString().toSafeFloat()
    priceAmountView.text = "$ ${(viewModel().fromCoinRate() * amount).formatPrice()}"
}

private fun ActivitySwapBinding.legalCheck() {
    val viewModel = viewModel()
    val balance = viewModel.fromCoinBalance()
    if (fromAmount() > balance) {
        swapButton.isEnabled = false
        swapButton.setText(R.string.insufficient_balance)
        return
    }

    if (fromAmount() == 0.0f) {
        swapButton.isEnabled = false
        swapButton.setText(R.string.swap)
        return
    }

    swapButton.setText(R.string.swap)
    swapButton.isEnabled = toAmount() > 0
}

private fun ActivitySwapBinding.fromAmount() = fromInput.text.toString().toSafeFloat()
private fun ActivitySwapBinding.toAmount() = toInput.text.toString().toSafeFloat()


private fun ActivitySwapBinding.fromLegalCheck(): Boolean {
    return false
}

private fun ActivitySwapBinding.toLegalCheck(): Boolean {
    return false
}

fun ActivitySwapBinding.setMaxAmount() {
    val viewModel = viewModel()
    fromInput.setText(viewModel.fromCoinBalance().formatPrice())
}

private fun ActivitySwapBinding.showSendDialog() {

}

private fun ActivitySwapBinding.isLegalFrom() = fromInput.text.isLegalAmountNumber()

private fun ActivitySwapBinding.isLegalTo() = toInput.text.isLegalAmountNumber()