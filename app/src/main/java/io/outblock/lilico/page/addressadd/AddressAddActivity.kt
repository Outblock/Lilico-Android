package io.outblock.lilico.page.addressadd

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import io.outblock.lilico.R
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.databinding.ActivityAddAddressBinding
import io.outblock.lilico.utils.extensions.hideKeyboard
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.widgets.ProgressDialog

class AddressAddActivity : BaseActivity() {

    private lateinit var binding: ActivityAddAddressBinding

    private lateinit var viewModel: AddressAddViewModel

    private val progressDialog by lazy { ProgressDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()

        viewModel = ViewModelProvider(this)[AddressAddViewModel::class.java].apply {
            resultLiveData.observe(this@AddressAddActivity) { onSaveResult(it) }
        }

        binding.saveButton.setOnClickListener {
            progressDialog.show()
            binding.nameText.hideKeyboard()
            viewModel.save(binding.nameText.text.toString(), binding.addressText.text.toString())
        }
        binding.nameText.doOnTextChanged { text, _, _, _ -> onEditTextChange() }
        binding.addressText.doOnTextChanged { text, _, _, _ -> onEditTextChange() }
    }

    private fun onEditTextChange() {
        with(binding) {
            val isNameVerified = !nameText.text.isNullOrEmpty()
            val isAddressVerified = addressText.text.length == 16
            binding.saveButton.isEnabled = isNameVerified && isAddressVerified
        }
    }

    private fun onSaveResult(isSuccess: Boolean) {
        progressDialog.dismiss()
        if (isSuccess) {
            finish()
        } else {
            Toast.makeText(this, "Save fail", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        title = R.string.add_address.res2String()
    }

    companion object {
        private const val EXTRA_TYPE = "EXTRA_TYPE"

        fun launch(context: Context) {
            context.startActivity(Intent(context, AddressAddActivity::class.java))
        }
    }
}