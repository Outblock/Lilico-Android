package io.outblock.lilico.page.staking.guide

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import io.outblock.lilico.R
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.manager.staking.StakingManager
import io.outblock.lilico.page.staking.providers.StakingProviderActivity

class StakeGuideActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stake_guide)
        findViewById<View>(R.id.stake_button).setOnClickListener {
            if (StakingManager.hasBeenSetup()) {
                StakingProviderActivity.launch(this)
            } else {
                StakingSetupDialog.show(this)
            }
        }
        setupToolbar()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onStop() {
        super.onStop()
        finish()
    }

    private fun setupToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, StakeGuideActivity::class.java))
        }
    }
}