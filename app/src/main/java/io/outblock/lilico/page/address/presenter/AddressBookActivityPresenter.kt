package io.outblock.lilico.page.address.presenter

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
import io.outblock.lilico.databinding.ActivityAddressBookBinding
import io.outblock.lilico.page.address.AddressBookActivity
import io.outblock.lilico.page.address.AddressBookViewModel
import io.outblock.lilico.page.address.model.AddressBookActivityModel
import io.outblock.lilico.utils.extensions.*

class AddressBookActivityPresenter(
    private val activity: AddressBookActivity,
    private val binding: ActivityAddressBookBinding,
) : BasePresenter<AddressBookActivityModel> {

    private val viewModel by lazy { ViewModelProvider(activity)[AddressBookViewModel::class.java] }

    init {
        setupToolbar()
        with(binding.editText) {
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideKeyboard()
                    viewModel.search(text.toString().trim())
                    clearFocus()
                }
                return@setOnEditorActionListener false
            }
            doOnTextChanged { text, _, _, _ -> if (isAutoSearch(text)) viewModel.search(text.toString().trim()) }
            onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus -> onSearchFocusChange(hasFocus) }
        }
        binding.cancelButton.setOnClickListener {
            onSearchFocusChange(false)
            binding.editText.setText("")
            binding.editText.clearFocus()
            viewModel.clearSearch()
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

    private fun isAutoSearch(text: CharSequence?): Boolean {
        if (text.isNullOrBlank()) {
            return false
        }
        return !text.trim().contains(" ") && (text.endsWith(".find") || text.endsWith(".fns"))
    }

    override fun bind(model: AddressBookActivityModel) {

    }

    private fun setupToolbar() {
        binding.toolbar.navigationIcon?.mutate()?.setTint(R.color.neutrals1.res2color())
        activity.setSupportActionBar(binding.toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setDisplayShowHomeEnabled(true)
        activity.title = R.string.address_book.res2String()
    }
}