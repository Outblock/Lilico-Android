package io.outblock.lilico.page.inbox.presenter

import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.ActivityInboxBinding
import io.outblock.lilico.page.inbox.InboxActivity
import io.outblock.lilico.page.inbox.adapter.InboxPageAdapter
import io.outblock.lilico.page.inbox.model.InboxPageModel
import io.outblock.lilico.widgets.ProgressDialog

class InboxPagePresenter(
    private val binding: ActivityInboxBinding,
    private val activity: InboxActivity,
) : BasePresenter<InboxPageModel> {

    private val titles = listOf(R.string.token_with_count, R.string.nft_with_count)

    private var progressDialog: ProgressDialog? = null

    init {
        setupViewPager()
        setupTabLayout()
    }

    override fun bind(model: InboxPageModel) {
        model.tokenList?.let { updateTabTitle(0, it.size) }
        model.nftList?.let { updateTabTitle(1, it.size) }
        model.claimExecuting?.let { if (it) showProgressDialog() else dismissProgressDialog() }
    }

    private fun updateTabTitle(index: Int, size: Int) {
        binding.tabLayout.getTabAt(index)?.text = activity.getString(titles[index], size)
    }

    private fun setupViewPager() {
        with(binding.viewPager) {
            adapter = InboxPageAdapter(activity.supportFragmentManager)
        }
    }

    private fun showProgressDialog() {
        progressDialog?.dismiss()
        progressDialog = ProgressDialog(activity).apply { show() }
    }

    private fun dismissProgressDialog() {
        progressDialog?.dismiss()
    }

    private fun setupTabLayout() {
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        for (i in 0 until binding.tabLayout.tabCount) {
            binding.tabLayout.getTabAt(i)?.text = activity.getString(titles[i], 0)
        }
    }
}