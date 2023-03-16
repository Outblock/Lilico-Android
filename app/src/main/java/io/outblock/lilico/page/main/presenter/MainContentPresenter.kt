package io.outblock.lilico.page.main.presenter

import androidx.viewpager.widget.ViewPager
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.ActivityMainBinding
import io.outblock.lilico.page.main.MainActivity
import io.outblock.lilico.page.main.activeColor
import io.outblock.lilico.page.main.adapter.MainPageAdapter
import io.outblock.lilico.page.main.model.MainContentModel
import io.outblock.lilico.page.main.setLottieDrawable

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
        setupListener()
        binding.viewPager.offscreenPageLimit = 4
        binding.viewPager.adapter = MainPageAdapter(activity)
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
                R.id.bottom_navigation_home -> onNavigationItemSelected(0)
                R.id.bottom_navigation_nft -> onNavigationItemSelected(1)
                R.id.bottom_navigation_explore -> onNavigationItemSelected(2)
                R.id.bottom_navigation_profile -> onNavigationItemSelected(3)
            }
            true
        }

        binding.navigationView.post {
            with(binding.navigationView.menu) {
                (0 until size()).forEach { binding.navigationView.setLottieDrawable(it, it == 0) }
            }
        }
    }

    private fun onNavigationItemSelected(index: Int) {
        val prvIndex = binding.viewPager.currentItem
        binding.viewPager.setCurrentItem(index, false)
        binding.navigationView.updateIndicatorColor(binding.navigationView.activeColor(index))

        if (prvIndex != index) {
            binding.navigationView.setLottieDrawable(prvIndex, false)
        }

        binding.navigationView.setLottieDrawable(index, true, prvIndex != index)
    }
}