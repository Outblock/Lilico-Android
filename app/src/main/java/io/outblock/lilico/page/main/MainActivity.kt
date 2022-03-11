package io.outblock.lilico.page.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.zackratos.ultimatebarx.ultimatebarx.UltimateBarX
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.databinding.ActivityMainBinding
import io.outblock.lilico.page.bubble.sendstate.SendStateBubble
import io.outblock.lilico.page.main.presenter.MainContentPresenter
import io.outblock.lilico.utils.isNightMode
import io.outblock.lilico.utils.isRegistered
import io.outblock.lilico.utils.uiScope
import kotlinx.coroutines.delay

class MainActivity : BaseActivity() {

    private lateinit var contentPresenter: MainContentPresenter

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel

    private var isRegistered = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        INSTANCE = this
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        UltimateBarX.with(this).fitWindow(false).light(!isNightMode(this)).applyStatusBar()
        UltimateBarX.with(this).fitWindow(true).light(!isNightMode(this)).applyNavigationBar()

        contentPresenter = MainContentPresenter(this, binding)
        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        uiScope { isRegistered = isRegistered() }
    }

    override fun onRestart() {
        super.onRestart()
        uiScope {
            if (isRegistered != isRegistered()) {
                viewModel.walletRegisterSuccessLiveData.value = isRegistered()
            }
        }
    }

    override fun onDestroy() {
        INSTANCE = null
        super.onDestroy()
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName

        private var INSTANCE: MainActivity? = null
        fun launch(context: Context) {
            context.startActivity(Intent(context, MainActivity::class.java))
        }

        fun getInstance() = INSTANCE
    }
}