package io.outblock.lilico.page.send.transaction.subpage.amount.presenter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.transition.Fade
import androidx.transition.Scene
import androidx.transition.TransitionManager
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.databinding.ActivitySendAmountBinding
import io.outblock.lilico.network.model.AddressBookContact
import io.outblock.lilico.page.address.model.AddressBookPersonModel
import io.outblock.lilico.page.address.presenter.AddressBookPersonPresenter
import io.outblock.lilico.page.send.transaction.subpage.amount.SendAmountActivity
import io.outblock.lilico.page.send.transaction.subpage.amount.SendAmountViewModel
import io.outblock.lilico.page.send.transaction.subpage.amount.model.SendAmountModel
import io.outblock.lilico.page.send.transaction.subpage.amount.model.SendBalanceModel
import io.outblock.lilico.page.send.transaction.subpage.amount.model.TransactionModel
import io.outblock.lilico.page.send.transaction.subpage.amount.widget.SendCoinPopupMenu
import io.outblock.lilico.page.send.transaction.subpage.transaction.TransactionDialog
import io.outblock.lilico.utils.*
import io.outblock.lilico.utils.extensions.*
import wallet.core.jni.CoinType

class SendAmountPresenter(
    private val activity: SendAmountActivity,
    private val binding: ActivitySendAmountBinding,
    private val contact: AddressBookContact,
) : BasePresenter<SendAmountModel> {

    private val viewModel by lazy { ViewModelProvider(activity)[SendAmountViewModel::class.java] }

    private fun balance() = viewModel.balanceLiveData.value

    init {
        setupToolbar()
        setupContactCard()
        with(binding.transferAmountInput) {
            doOnTextChanged { _, _, _, _ ->
                updateTransferAmountConvert()
                checkAmount()
                binding.nextButton.isEnabled = verifyAmount()
            }
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    hideKeyboard()
                    if (verifyAmount()) showSendDialog()
                }
                return@setOnEditorActionListener false
            }
        }
        with(binding) {
            nextButton.setOnClickListener { showSendDialog() }
            cancelButton.setOnClickListener { activity.finish() }
            swapButton.setOnClickListener { viewModel.swapCoin() }
            coinWrapper.setOnClickListener { SendCoinPopupMenu(coinWrapper).show() }
            maxButton.setOnClickListener { setMaxAmount() }
        }
    }

    private fun checkAmount() {
        val amount = binding.transferAmountInput.text.ifBlank { "0" }.toString().toSafeFloat()
        val coinRate = balance()?.coinRate ?: 0f
        val inputBalance = if (viewModel.convertCoin() == Coin.USD) amount else amount / (if (coinRate == 0f) 1f else coinRate)
        val isOutOfBalance = inputBalance > (balance()?.balance ?: 0f)
        if (isOutOfBalance && !binding.errorWrapper.isVisible()) {
            TransitionManager.go(Scene(binding.root as ViewGroup), Fade().apply { })
        } else if (!isOutOfBalance && binding.errorWrapper.isVisible()) {
            TransitionManager.go(Scene(binding.root as ViewGroup), Fade().apply { })
        }
        binding.errorWrapper.setVisible(isOutOfBalance)
    }

    override fun bind(model: SendAmountModel) {
        model.balance?.let { updateBalance(it) }
        model.onCoinSwap?.let { updateCoinState() }
    }

    private fun updateCoinState() {
        with(binding) {
            coinIconView.setImageResource(viewModel.currentCoin().icon())
            updateTransferAmountConvert()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateBalance(balance: SendBalanceModel) {
        with(binding) {
            balanceAmountView.text = "${balance.balance.formatPrice()} Flow "
            balanceAmountConvertView.text =
                activity.getString(
                    R.string.coin_rate_usd_convert,
                    (if (balance.coinRate > 0) balance.coinRate * balance.balance else 0f).formatPrice()
                )
            transferAmountInput.text = transferAmountInput.text
            transferAmountInput.setSelection(transferAmountInput.text.length)
            updateTransferAmountConvert()
        }
    }

    private fun updateTransferAmountConvert() {
        with(binding) {
            convertAmountIconView.setImageResource(viewModel.convertCoin().icon())
            if (viewModel.convertCoin() == Coin.USD) {
                convertAmountIconView.imageTintList = ColorStateList.valueOf(R.color.note.res2color())
            } else {
                convertAmountIconView.imageTintList = null
            }
            convertAmountView.text = getAmountConvert()
        }
    }

    private fun showSendDialog() {
        ioScope {
            val wallet = walletCache().read() ?: return@ioScope
            uiScope {
                TransactionDialog.newInstance(
                    TransactionModel(
                        amount = binding.transferAmountInput.text.toString().toSafeFloat(),
                        coinType = CoinType.FLOW.value(),
                        target = viewModel.contact(),
                        fromAddress = wallet.wallets?.first()?.blockchain?.first()?.address.orEmpty(),
                    )
                ).show(activity.supportFragmentManager, "")
            }
        }
    }

    private fun setupContactCard() {
        with(binding.contactCard.root) {
            AddressBookPersonPresenter(this).bind(AddressBookPersonModel(data = contact))
            setPadding(0, paddingTop, 0, paddingBottom)
            isClickable = false
        }
        binding.contactCard.addButton.setVisible(false)
        binding.closeButton.setOnClickListener { activity.finish() }
    }

    private fun setMaxAmount() {
        val balance = balance()?.balance ?: 0f
        val coinRate = balance()?.coinRate ?: 0f
        val amount = (if (viewModel.convertCoin() == Coin.USD) balance else balance * coinRate).formatPrice()
        with(binding) {
            transferAmountInput.setText(amount)
            transferAmountInput.setSelection(transferAmountInput.text.length)
        }
    }

    private fun setupToolbar() {
        binding.toolbar.navigationIcon?.mutate()?.setTint(R.color.neutrals1.res2color())
        activity.setSupportActionBar(binding.toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setDisplayShowHomeEnabled(true)
        activity.title = R.string.send_to.res2String()
    }

    private fun verifyAmount(): Boolean {
        val number = binding.transferAmountInput.text.toString().toFloatOrNull()
        return number != null && number > 0
    }

    private fun getAmountConvert(): String {
        val amount = binding.transferAmountInput.text.ifBlank { "0" }.toString().toSafeFloat()
        val rate = balance()?.coinRate ?: 0f
        val convert = if (viewModel.convertCoin() == Coin.USD) amount * rate else amount / rate
        return convert.formatPrice()
    }
}