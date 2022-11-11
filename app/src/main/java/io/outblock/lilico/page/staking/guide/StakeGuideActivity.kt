package io.outblock.lilico.page.staking.guide

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import io.outblock.lilico.R
import io.outblock.lilico.base.activity.BaseActivity

class StakeGuideActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stake_guide)
        findViewById<View>(R.id.stake_button).setOnClickListener { }
    }

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, StakeGuideActivity::class.java))
        }
    }
}