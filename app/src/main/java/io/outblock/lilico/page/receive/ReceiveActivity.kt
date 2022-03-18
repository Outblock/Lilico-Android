package io.outblock.lilico.page.receive

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.databinding.ActivityReceiveBinding
import io.outblock.lilico.page.receive.model.ReceiveModel
import io.outblock.lilico.page.receive.presenter.ReceivePresenter

class ReceiveActivity : BaseActivity() {

    private lateinit var presenter: ReceivePresenter
    private lateinit var viewModel: ReceiveViewModel
    private lateinit var binding: ActivityReceiveBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiveBinding.inflate(layoutInflater)
        setContentView(binding.root)
        presenter = ReceivePresenter(this, binding)
        viewModel = ViewModelProvider(this)[ReceiveViewModel::class.java].apply {
            walletLiveData.observe(this@ReceiveActivity) { presenter.bind(ReceiveModel(data = it)) }
            qrcodeLiveData.observe(this@ReceiveActivity) { presenter.bind(ReceiveModel(qrcode = it)) }
            load()
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
            context.startActivity(Intent(context, ReceiveActivity::class.java).apply {
            })
        }
    }
}