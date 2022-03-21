package io.outblock.lilico.page.addtoken

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.databinding.ActivityAddTokenBinding
import io.outblock.lilico.page.addtoken.model.AddTokenModel
import io.outblock.lilico.page.addtoken.presenter.AddTokenPresenter

class AddTokenActivity : BaseActivity() {

    private lateinit var presenter: AddTokenPresenter
    private lateinit var viewModel: AddTokenViewModel
    private lateinit var binding: ActivityAddTokenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTokenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter = AddTokenPresenter(this, binding)
        viewModel = ViewModelProvider(this)[AddTokenViewModel::class.java].apply {
            tokenListLiveData.observe(this@AddTokenActivity) { presenter.bind(AddTokenModel(data = it)) }
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
            context.startActivity(Intent(context, AddTokenActivity::class.java))
        }
    }
}