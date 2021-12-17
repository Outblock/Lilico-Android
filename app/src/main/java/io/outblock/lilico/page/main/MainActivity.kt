package io.outblock.lilico.page.main

import android.os.Bundle
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.databinding.ActivityMainBinding
import io.outblock.lilico.page.main.presenter.MainContentPresenter

class MainActivity : BaseActivity() {

    private lateinit var contentPresenter: MainContentPresenter

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        UltimateBarX.with(this).light(!isNightMode(this)).fitWindow(true).applyStatusBar()

        contentPresenter = MainContentPresenter(this, binding)
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}