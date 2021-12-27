package io.outblock.lilico.page.main.presenter

import androidx.viewpager2.widget.ViewPager2
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.ActivityMainBinding
import io.outblock.lilico.page.main.MainActivity
import io.outblock.lilico.page.main.adapter.MainPageAdapter
import io.outblock.lilico.page.main.model.MainContentModel

class MainContentPresenter(
    private val activity: MainActivity,
    private val binding: ActivityMainBinding,
) : BasePresenter<MainContentModel> {

    private val menuId by lazy {
        listOf(
            R.id.bottom_navigation_home,
            R.id.bottom_navigation_explore,
            R.id.bottom_navigation_profile
        )
    }

    init {
        binding.viewPager.adapter = MainPageAdapter(activity)
        setupListener()
    }

    override fun bind(model: MainContentModel) {

    }

    private fun setupListener() {
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.navigationView.selectedItemId = menuId[position]
            }
        })
        binding.navigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.bottom_navigation_home -> binding.viewPager.currentItem = 0
                R.id.bottom_navigation_explore -> binding.viewPager.currentItem = 1
                R.id.bottom_navigation_profile -> binding.viewPager.currentItem = 2
            }
            true
        }
    }
}