package io.outblock.lilico.page.profile.subpage.about

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.MenuItem
import com.zackratos.ultimatebarx.ultimatebarx.UltimateBarX
import io.outblock.lilico.BuildConfig
import io.outblock.lilico.R
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.databinding.ActivityAboutBinding
import io.outblock.lilico.utils.extensions.openInSystemBrowser
import io.outblock.lilico.utils.isNightMode
import io.outblock.lilico.utils.sendEmail

class AboutActivity : BaseActivity() {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        UltimateBarX.with(this).fitWindow(true).colorRes(R.color.background).light(!isNightMode(this)).applyStatusBar()

        with(binding) {
            versionView.text = getString(R.string.about_version, BuildConfig.VERSION_NAME)
            discordButton.setOnClickListener { "https://discord.gg/2jUaEQ8MQk".openInSystemBrowser(this@AboutActivity) }
            twitterButton.setOnClickListener { "https://twitter.com/lilico_app".openInSystemBrowser(this@AboutActivity) }
            emailButton.setOnClickListener { sendEmail(this@AboutActivity, email = "hi@lilico.app") }
            madeByView.movementMethod = LinkMovementMethod.getInstance()
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

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, AboutActivity::class.java))
        }
    }
}