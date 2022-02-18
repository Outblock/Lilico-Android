package io.outblock.lilico.page.send

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import io.outblock.lilico.R
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.databinding.ActivityTransactionSendBinding
import io.outblock.lilico.page.address.AddressBookFragment
import io.outblock.lilico.page.send.presenter.TransactionSendPresenter

class TransactionSendActivity : BaseActivity() {

    private lateinit var binding: ActivityTransactionSendBinding
    private lateinit var presenter: TransactionSendPresenter
    private lateinit var viewModel: TransactionSendViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionSendBinding.inflate(layoutInflater)
        setContentView(binding.root)


        supportFragmentManager.beginTransaction().replace(R.id.search_container, AddressBookFragment()).commit()

        presenter = TransactionSendPresenter(this, binding)
        viewModel = ViewModelProvider(this)[TransactionSendViewModel::class.java].apply {

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, TransactionSendActivity::class.java))
        }
    }
}