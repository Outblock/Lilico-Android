package io.outblock.lilico.page.transaction.record.presenter

import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.ActivityTransactionRecordBinding
import io.outblock.lilico.page.transaction.record.TransactionRecordActivity
import io.outblock.lilico.page.transaction.record.adapter.TransactionRecordPageAdapter
import io.outblock.lilico.page.transaction.record.model.TransactionRecordPageModel
import io.outblock.lilico.utils.extensions.res2color

class TransactionRecordPresenter(
    private val binding: ActivityTransactionRecordBinding,
    private val activity: TransactionRecordActivity,
) : BasePresenter<TransactionRecordPageModel> {

    private val titles = listOf(R.string.transfer_with_count, R.string.transaction_with_count)

    init {
        binding.refreshLayout.isEnabled = false
        binding.refreshLayout.setColorSchemeColors(R.color.salmon_primary.res2color())
//        binding.refreshLayout.post { binding.refreshLayout.isRefreshing = true }
        setupViewPager()
        setupTabLayout()
    }

    override fun bind(model: TransactionRecordPageModel) {
        model.transactionCount?.let { updateTabTitle(0, it) }
        model.transferCount?.let { updateTabTitle(1, it) }
    }

    private fun setupViewPager() {
        with(binding.viewPager) {
            adapter = TransactionRecordPageAdapter(activity.supportFragmentManager)
        }
    }

    private fun setupTabLayout() {
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        for (i in 0 until binding.tabLayout.tabCount) {
            binding.tabLayout.getTabAt(i)?.text = activity.getString(titles[i], 0)
        }
    }

    private fun updateTabTitle(index: Int, size: Int) {
        binding.tabLayout.getTabAt(index)?.text = activity.getString(titles[index], size)
    }
}