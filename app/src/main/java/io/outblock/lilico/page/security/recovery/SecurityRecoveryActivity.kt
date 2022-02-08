package io.outblock.lilico.page.security.recovery

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.GridLayoutManager
import io.outblock.lilico.R
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.databinding.ActivitySecurityRecoveryBinding
import io.outblock.lilico.page.walletcreate.fragments.mnemonic.MnemonicAdapter
import io.outblock.lilico.page.walletcreate.fragments.mnemonic.MnemonicModel
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.wallet.getMnemonic
import io.outblock.lilico.wallet.getPrivateKey
import io.outblock.lilico.widgets.itemdecoration.GridSpaceItemDecoration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SecurityRecoveryActivity : BaseActivity() {

    private lateinit var binding: ActivitySecurityRecoveryBinding

    private val type by lazy { intent.getIntExtra(EXTRA_TYPE, TYPE_PHRASES) }
    private val adapter by lazy { MnemonicAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecurityRecoveryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        if (type == TYPE_PRIVATE_KEY) {
            initPrivateKey()
        } else {
            initPhrases()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun initPhrases() {
        with(binding.mnemonicContainer) {
            setVisible()
            adapter = this@SecurityRecoveryActivity.adapter
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
            addItemDecoration(GridSpaceItemDecoration(vertical = 16.0))
        }
        loadMnemonic()
        binding.stringContainer.setVisible(false)
    }

    private fun initPrivateKey() {
        with(binding.stringContainer) {
            setVisible()
            text = getPrivateKey()
        }

        binding.mnemonicContainer.setVisible(false)
    }

    private fun loadMnemonic() {
        ioScope {
            val str = getMnemonic()
            withContext(Dispatchers.Main) {
                val list = str.split(" ").mapIndexed { index, s -> MnemonicModel(index + 1, s) }
                val result = mutableListOf<MnemonicModel>()
                (0 until list.size / 2).forEach { i ->
                    result.add(list[i])
                    result.add(list[i + list.size / 2])
                }
                adapter.setNewDiffData(result)
            }
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        title = (if (type == TYPE_PRIVATE_KEY) R.string.private_key else R.string.recovery_phrase).res2String()
    }

    companion object {
        const val TYPE_PRIVATE_KEY = 1
        const val TYPE_PHRASES = 2

        private const val EXTRA_TYPE = "EXTRA_TYPE"

        fun launch(context: Context, type: Int) {
            context.startActivity(Intent(context, SecurityRecoveryActivity::class.java).apply {
                putExtra(EXTRA_TYPE, type)
            })
        }
    }
}