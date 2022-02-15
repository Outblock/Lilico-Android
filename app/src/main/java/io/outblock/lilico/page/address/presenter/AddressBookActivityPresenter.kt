package io.outblock.lilico.page.address.presenter

import android.view.inputmethod.EditorInfo
import androidx.lifecycle.ViewModelProvider
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.ActivityAddressBookBinding
import io.outblock.lilico.page.address.AddressBookActivity
import io.outblock.lilico.page.address.AddressBookViewModel
import io.outblock.lilico.page.address.model.AddressBookActivityModel
import io.outblock.lilico.utils.extensions.hideKeyboard
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.extensions.res2color

class AddressBookActivityPresenter(
    private val activity: AddressBookActivity,
    private val binding: ActivityAddressBookBinding,
) : BasePresenter<AddressBookActivityModel> {

    private val viewModel by lazy { ViewModelProvider(activity)[AddressBookViewModel::class.java] }

    init {
        setupToolbar()
        with(binding.editText) {
            setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideKeyboard()
                    if (text.isNotEmpty()) {
                        viewModel.search(text.toString())
                    }
                    clearFocus()
                }
                return@setOnEditorActionListener false
            }
        }
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