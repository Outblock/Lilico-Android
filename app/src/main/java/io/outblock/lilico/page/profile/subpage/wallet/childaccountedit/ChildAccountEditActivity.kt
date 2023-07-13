package io.outblock.lilico.page.profile.subpage.wallet.childaccountedit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.zackratos.ultimatebarx.ultimatebarx.UltimateBarX
import com.zackratos.ultimatebarx.ultimatebarx.addStatusBarTopPadding
import io.outblock.lilico.R
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.databinding.ActivityChildAccountEditBinding
import io.outblock.lilico.manager.childaccount.ChildAccount
import io.outblock.lilico.page.profile.subpage.wallet.childaccountedit.model.ChildAccountEditModel
import io.outblock.lilico.page.profile.subpage.wallet.childaccountedit.presenter.ChildAccountEditPresenter
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.isNightMode

class ChildAccountEditActivity : BaseActivity() {

    private val account by lazy { intent.getParcelableExtra<ChildAccount>(EXTRA_DATA) }
    private lateinit var binding: ActivityChildAccountEditBinding
    private lateinit var presenter: ChildAccountEditPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (account == null) {
            finish()
            return
        }
        binding = ActivityChildAccountEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        UltimateBarX.with(this).fitWindow(false).colorRes(R.color.background).light(!isNightMode(this)).applyStatusBar()
        binding.root.addStatusBarTopPadding()

        presenter = ChildAccountEditPresenter(binding, this)
        account?.let { presenter.bind(ChildAccountEditModel(account = account)) }

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
        title = R.string.edit.res2String()
    }

    companion object {
        private const val EXTRA_DATA = "extra_data"


        fun launch(context: Context, account: ChildAccount) {
            context.startActivity(Intent(context, ChildAccountEditActivity::class.java).apply {
                putExtra(EXTRA_DATA, account)
            })
        }
    }
}