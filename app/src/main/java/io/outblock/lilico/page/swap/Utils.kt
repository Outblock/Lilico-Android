package io.outblock.lilico.page.swap

import android.view.inputmethod.EditorInfo
import androidx.core.widget.doOnTextChanged
import com.bumptech.glide.Glide
import io.outblock.lilico.databinding.ActivitySwapBinding
import io.outblock.lilico.manager.coin.FlowCoin
import io.outblock.lilico.utils.extensions.hideKeyboard
import io.outblock.lilico.utils.isLegalAmountNumber


fun ActivitySwapBinding.bindInputListener() {
    bindFromListener()
    bindToListener()
}

fun ActivitySwapBinding.updateFromCoin(coin: FlowCoin) {
    Glide.with(fromCoinIcon).load(coin.icon).into(fromCoinIcon)
    fromCoinName.text = coin.name.uppercase()
}

fun ActivitySwapBinding.updateToCoin(coin: FlowCoin) {
    Glide.with(toCoinIcon).load(coin.icon).into(toCoinIcon)
    toCoinName.text = coin.name.uppercase()
}

private fun ActivitySwapBinding.bindFromListener() {
    with(fromInput) {
        doOnTextChanged { _, _, _, _ ->
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

private fun ActivitySwapBinding.updateAmountPrice() {
}

private fun ActivitySwapBinding.legalCheck() {
    fromLegalCheck()
    toLegalCheck()
}


private fun ActivitySwapBinding.fromLegalCheck(): Boolean {
    return false
}

private fun ActivitySwapBinding.toLegalCheck(): Boolean {
    return false
}

fun ActivitySwapBinding.setMaxAmount() {

}

private fun ActivitySwapBinding.showSendDialog() {

}

private fun ActivitySwapBinding.isLegalFrom() = fromInput.text.isLegalAmountNumber()

private fun ActivitySwapBinding.isLegalTo() = toInput.text.isLegalAmountNumber()