package io.outblock.lilico.page.profile.subpage.wallet.childaccountdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.zackratos.ultimatebarx.ultimatebarx.UltimateBarX
import com.zackratos.ultimatebarx.ultimatebarx.addStatusBarTopPadding
import io.outblock.lilico.R
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.databinding.ActivityChildAccountDetailBinding
import io.outblock.lilico.manager.childaccount.ChildAccount
import io.outblock.lilico.page.profile.subpage.wallet.childaccountdetail.model.ChildAccountDetailModel
import io.outblock.lilico.page.profile.subpage.wallet.childaccountdetail.presenter.ChildAccountDetailPresenter
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.isNightMode

class ChildAccountDetailActivity : BaseActivity() {

    private val account by lazy { intent.getParcelableExtra<ChildAccount>(EXTRA_DATA) }
    private lateinit var binding: ActivityChildAccountDetailBinding
    private lateinit var presenter: ChildAccountDetailPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (account == null) {
            finish()
            return
        }
        binding = ActivityChildAccountDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        UltimateBarX.with(this).fitWindow(false).colorRes(R.color.background).light(!isNightMode(this)).applyStatusBar()
        binding.root.addStatusBarTopPadding()

        presenter = ChildAccountDetailPresenter(binding, this)
        account?.let { presenter.bind(ChildAccountDetailModel(account = account)) }

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
        title = R.string.wallet.res2String()
    }

    companion object {
        private const val EXTRA_DATA = "extra_data"


        fun launch(context: Context, account: ChildAccount) {
            context.startActivity(Intent(context, ChildAccountDetailActivity::class.java).apply {
                putExtra(EXTRA_DATA, account)
            })
        }
    }
}