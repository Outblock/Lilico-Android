package io.outblock.lilico.page.guide

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.TransitionManager
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.zackratos.ultimatebarx.ultimatebarx.UltimateBarX
import com.zackratos.ultimatebarx.ultimatebarx.addNavigationBarBottomPadding
import com.zackratos.ultimatebarx.ultimatebarx.addStatusBarTopPadding
import io.outblock.lilico.R
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.databinding.ActivityGuideBinding
import io.outblock.lilico.page.guide.adapter.GuideItemAdapter
import io.outblock.lilico.page.guide.model.GuideItemModel
import io.outblock.lilico.page.main.MainActivity
import io.outblock.lilico.utils.extensions.isVisible
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.utils.setGuidePageShown

class GuideActivity : BaseActivity() {

    private lateinit var binding: ActivityGuideBinding
    private val adapter by lazy { GuideItemAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)
        UltimateBarX.with(this).fitWindow(false).light(false).applyStatusBar()
        UltimateBarX.with(this).fitWindow(false).light(false).applyNavigationBar()
        binding.contentWrapper.addStatusBarTopPadding()
        binding.contentWrapper.addNavigationBarBottomPadding()
        setupViewPager()
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ -> }.attach()
        setupData()
        setupButtons()

        setGuidePageShown()
    }

    private fun setupViewPager() {
        with(binding.viewPager) {
            adapter = this@GuideActivity.adapter
            (getChildAt(0) as RecyclerView).overScrollMode = View.OVER_SCROLL_NEVER
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    val visible = position == this@GuideActivity.adapter.getData().size - 1
                    val change = binding.startTextView.isVisible() != visible
                    binding.startTextView.setVisible(visible)
                    if (change) TransitionManager.beginDelayedTransition(binding.nextButton)
                    super.onPageSelected(position)
                }
            })
        }
    }

    private fun setupButtons() {
        binding.nextButton.setOnClickListener {
            if (binding.viewPager.currentItem == adapter.getData().size - 1) {
                MainActivity.launch(this)
            } else {
                binding.viewPager.setCurrentItem(binding.viewPager.currentItem + 1, true)
            }
        }
        binding.skipButton.setOnClickListener { MainActivity.launch(this) }
    }

    private fun setupData() {
        adapter.setNewDiffData(
            listOf(
                GuideItemModel(R.drawable.img_guide_1, R.string.guide_title_1, R.string.guide_desc_1),
                GuideItemModel(R.drawable.img_guide_2, R.string.guide_title_2, R.string.guide_desc_2),
                GuideItemModel(R.drawable.img_guide_3, R.string.guide_title_3, R.string.guide_desc_3),
                GuideItemModel(R.drawable.img_guide_4, R.string.guide_title_4, R.string.guide_desc_4),
            )
        )
    }

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, GuideActivity::class.java))
            (context as? Activity)?.overridePendingTransition(0, 0)
        }
    }
}