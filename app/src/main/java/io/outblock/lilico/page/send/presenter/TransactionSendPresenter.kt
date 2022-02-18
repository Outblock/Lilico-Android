package io.outblock.lilico.page.send.presenter

import android.transition.Scene
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.ActivityTransactionSendBinding
import io.outblock.lilico.manager.flowjvm.FlowJvmHelper
import io.outblock.lilico.network.model.AddressBookContact
import io.outblock.lilico.page.address.AddressBookViewModel
import io.outblock.lilico.page.address.isAddressBookAutoSearch
import io.outblock.lilico.page.send.TransactionSendActivity
import io.outblock.lilico.page.send.adapter.TransactionSendPageAdapter
import io.outblock.lilico.page.send.model.TransactionSendModel
import io.outblock.lilico.page.send.subpage.amount.SendAmountActivity
import io.outblock.lilico.utils.addressPattern
import io.outblock.lilico.utils.extensions.*
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.uiScope

class TransactionSendPresenter(
    private val activity: TransactionSendActivity,
    private val binding: ActivityTransactionSendBinding,
) : BasePresenter<TransactionSendModel> {
    private val searchViewModel by lazy { ViewModelProvider(activity)[AddressBookViewModel::class.java] }

    private val tabTitles by lazy {
        listOf(
            R.string.recent, R.string.address_book,
            //R.string.my_accounts
        )
    }

    init {
        setupToolbar()
        with(binding) {
            with(binding.viewPager) {
                adapter = TransactionSendPageAdapter(activity.supportFragmentManager)
                offscreenPageLimit = 3
            }
            tabLayout.setupWithViewPager(viewPager)
            tabTitles.forEachIndexed { index, title ->
                val tab = tabLayout.getTabAt(index) ?: return@forEachIndexed
                tab.setText(title)
                when (title) {
                    R.string.recent -> tab.setIcon(R.drawable.ic_recent)
                    R.string.address_book -> tab.setIcon(R.drawable.ic_address_hashtag)
                    R.string.my_accounts -> tab.setIcon(R.drawable.ic_user)
                }
            }
        }

        setupSearchBox()
    }

    override fun bind(model: TransactionSendModel) {
    }

    private fun setupSearchBox() {
        with(binding.editText) {
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideKeyboard()
                    searchViewModel.searchLocal(text.toString().trim())
                    clearFocus()
                }
                return@setOnEditorActionListener false
            }
            doOnTextChanged { input, _, _, _ ->
                val text = input.toString().trim()
                if (isAddressBookAutoSearch(text)) {
                    searchViewModel.searchRemote(text, true)
                } else {
                    searchViewModel.searchLocal(text)
                }

                binding.searchContainer.setVisible(text.isNotBlank())

                binding.progressBar.setVisible(false)
                binding.searchIconView.setVisible(true)
                checkAddressAutoJump(text)
            }
            onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus -> onSearchFocusChange(hasFocus) }
        }
        binding.cancelButton.setOnClickListener {
            onSearchFocusChange(false)
            binding.editText.hideKeyboard()
            binding.editText.setText("")
            binding.editText.clearFocus()
            searchViewModel.clearSearch()
        }
    }

    private fun checkAddressAutoJump(text: String) {
        val isMatched = addressPattern.matches(text)
        if (isMatched) {
            binding.progressBar.setVisible()
            binding.searchIconView.setVisible(false, invisible = true)
            ioScope {
                val isVerified = FlowJvmHelper().addressVerify(text)
                uiScope {
                    if (text == binding.editText.text.toString()) {
                        binding.progressBar.setVisible(false)
                        binding.searchIconView.setVisible()
                        if (isVerified) {
                            SendAmountActivity.launch(activity, AddressBookContact(address = text))
                        }
                    }
                }
            }
        }
    }

    private fun onSearchFocusChange(hasFocus: Boolean) {
        val isVisible = hasFocus || !binding.editText.text.isNullOrBlank()
        val isVisibleChange = isVisible != binding.cancelButton.isVisible()

        if (isVisibleChange) {
            TransitionManager.go(Scene(binding.root as ViewGroup), Slide(Gravity.END).apply { duration = 150 })
            binding.cancelButton.setVisible(isVisible)
        }
    }

    private fun setupToolbar() {
        binding.toolbar.navigationIcon?.mutate()?.setTint(R.color.neutrals1.res2color())
        activity.setSupportActionBar(binding.toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setDisplayShowHomeEnabled(true)
        activity.title = R.string.send_to.res2String()
    }
}