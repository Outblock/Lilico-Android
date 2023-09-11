package io.outblock.lilico.page.profile.subpage.wallet.account

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import com.zackratos.ultimatebarx.ultimatebarx.UltimateBarX
import com.zackratos.ultimatebarx.ultimatebarx.addStatusBarTopPadding
import io.outblock.lilico.R
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.databinding.ActivityChildAccountsBinding
import io.outblock.lilico.page.profile.subpage.wallet.account.model.ChildAccountsModel
import io.outblock.lilico.page.profile.subpage.wallet.account.presenter.ChildAccountsPresenter
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.isNightMode

class ChildAccountsActivity : BaseActivity() {

    private lateinit var binding: ActivityChildAccountsBinding
    private lateinit var presenter: ChildAccountsPresenter
    private lateinit var viewModel: ChildAccountsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChildAccountsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        UltimateBarX.with(this).fitWindow(false).colorRes(R.color.background).light(!isNightMode(this)).applyStatusBar()
        binding.root.addStatusBarTopPadding()

        presenter = ChildAccountsPresenter(binding, this)

        viewModel = ViewModelProvider(this)[ChildAccountsViewModel::class.java].apply {
            accountsLiveData.observe(this@ChildAccountsActivity) { presenter.bind(ChildAccountsModel(accounts = it)) }
            load()
        }

        setupToolbar()
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
        title = R.string.linked_account.res2String()
    }

    companion object {

        fun launch(context: Context) {
            context.startActivity(Intent(context, ChildAccountsActivity::class.java))
        }
    }
}