package io.outblock.lilico.page.send.presenter

import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.ActivityTransactionSendBinding
import io.outblock.lilico.page.send.TransactionSendActivity
import io.outblock.lilico.page.send.adapter.TransactionSendPageAdapter
import io.outblock.lilico.page.send.model.TransactionSendModel
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.extensions.res2color

class TransactionSendPresenter(
    private val activity: TransactionSendActivity,
    private val binding: ActivityTransactionSendBinding,
) : BasePresenter<TransactionSendModel> {
    private val tabTitles by lazy { listOf(R.string.recent, R.string.address_book, R.string.my_accounts) }

    init {
        setupToolbar()
        with(binding) {
            with(binding.viewPager) {
                adapter = TransactionSendPageAdapter(activity.supportFragmentManager)
                offscreenPageLimit = 3
            }
            tabLayout.setupWithViewPager(viewPager)
            tabTitles.forEachIndexed { index, title ->
                val tab = tabLayout.getTabAt(index) ?: return@forEachIndexed
                tab.setText(title)
                when (title) {
                    R.string.recent -> tab.setIcon(R.drawable.ic_recent)
                    R.string.address_book -> tab.setIcon(R.drawable.ic_address_hashtag)
                    R.string.my_accounts -> tab.setIcon(R.drawable.ic_user)
                }
            }
        }
    }

    override fun bind(model: TransactionSendModel) {
    }

    private fun setupToolbar() {
        binding.toolbar.navigationIcon?.mutate()?.setTint(R.color.neutrals1.res2color())
        activity.setSupportActionBar(binding.toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setDisplayShowHomeEnabled(true)
        activity.title = R.string.send_to.res2String()
    }
}