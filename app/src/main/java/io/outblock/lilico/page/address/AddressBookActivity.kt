package io.outblock.lilico.page.address

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import io.outblock.lilico.R
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.databinding.ActivityAddressBookBinding
import io.outblock.lilico.page.address.model.AddressBookActivityModel
import io.outblock.lilico.page.address.presenter.AddressBookActivityPresenter
import io.outblock.lilico.page.addressadd.AddressAddActivity

class AddressBookActivity : BaseActivity() {

    private lateinit var binding: ActivityAddressBookBinding
    private lateinit var presenter: AddressBookActivityPresenter
    private lateinit var viewModel: AddressBookViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter = AddressBookActivityPresenter(this, binding)
        viewModel = ViewModelProvider(this)[AddressBookViewModel::class.java].apply {
            clearEditTextFocusLiveData.observe(this@AddressBookActivity) { presenter.bind(AddressBookActivityModel(isClearInputFocus = it)) }
        }

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, AddressBookFragment()).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.address_book, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.action_add -> AddressAddActivity.launch(this)
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, AddressBookActivity::class.java))
        }
    }
}