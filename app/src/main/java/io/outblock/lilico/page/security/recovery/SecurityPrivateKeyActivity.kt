package io.outblock.lilico.page.security.recovery

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import io.outblock.lilico.R
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.databinding.ActivitySecurityPrivateKeyBinding
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.textToClipboard
import io.outblock.lilico.utils.toast
import io.outblock.lilico.wallet.getPrivateKey
import io.outblock.lilico.wallet.getPublicKey

class SecurityPrivateKeyActivity : BaseActivity() {

    private lateinit var binding: ActivitySecurityPrivateKeyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecurityPrivateKeyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        initPrivateKey()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun initPrivateKey() {
        with(binding) {
            privateKeyView.text = getPrivateKey()
            publicKeyView.text = getPublicKey()

            privateKeyCopyButton.setOnClickListener { copyToClipboard(getPrivateKey()) }
            publicKeyCopyButton.setOnClickListener { copyToClipboard(getPublicKey()) }

            hashAlgorithm.text = getString(R.string.hash_algorithm, "Sha2_256")
            signAlgorithm.text = getString(R.string.sign_algorithm, "ECDSA_secp256k1")
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        title = R.string.private_key.res2String()
    }

    private fun copyToClipboard(text: String) {
        textToClipboard(text)
        toast(msgRes = R.string.copied_to_clipboard)
    }

    companion object {
        fun launch(context: Context) {
            context.startActivity(launchIntent(context))
        }

        fun launchIntent(context: Context): Intent = Intent(context, SecurityPrivateKeyActivity::class.java)
    }
}