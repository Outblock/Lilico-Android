package io.outblock.lilico.page.main.presenter

import androidx.viewpager.widget.ViewPager
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.ActivityMainBinding
import io.outblock.lilico.page.main.MainActivity
import io.outblock.lilico.page.main.adapter.MainPageAdapter
import io.outblock.lilico.page.main.model.MainContentModel
import io.outblock.lilico.utils.extensions.colorStateList

class MainContentPresenter(
    private val activity: MainActivity,
    private val binding: ActivityMainBinding,
) : BasePresenter<MainContentModel> {

    private val menuId by lazy {
        listOf(
            R.id.bottom_navigation_home,
            R.id.bottom_navigation_nft,
            R.id.bottom_navigation_explore,
            R.id.bottom_navigation_profile,
        )
    }

    init {
        binding.viewPager.adapter = MainPageAdapter(activity)
        binding.viewPager.offscreenPageLimit = 4
        setupListener()
    }

    override fun bind(model: MainContentModel) {
        model.onChangeTab?.let { binding.viewPager.setCurrentItem(it.index, false) }
    }

    private fun setupListener() {
        binding.viewPager.setOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                binding.navigationView.selectedItemId = menuId[position]
            }
        })
        binding.navigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.bottom_navigation_home -> onNavigationItemSelected(0, R.color.bottom_navigation_color_wallet)
                R.id.bottom_navigation_nft -> onNavigationItemSelected(1, R.color.bottom_navigation_color_nft)
                R.id.bottom_navigation_explore -> onNavigationItemSelected(2, R.color.bottom_navigation_color_explore)
                R.id.bottom_navigation_profile -> onNavigationItemSelected(3, R.color.bottom_navigation_color_profile)
            }
            true
        }
    }

    private fun onNavigationItemSelected(index: Int, color: Int) {
        binding.viewPager.setCurrentItem(index, false)
        binding.navigationView.itemIconTintList = color.colorStateList(activity)?.apply {
            binding.navigationView.updateIndicatorColor(getColorForState(intArrayOf(android.R.attr.state_checked), 0))
        }
    }
}