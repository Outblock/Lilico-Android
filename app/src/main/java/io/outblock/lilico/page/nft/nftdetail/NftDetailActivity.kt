package io.outblock.lilico.page.nft.nftdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import com.zackratos.ultimatebarx.ultimatebarx.UltimateBarX
import io.outblock.lilico.R
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.databinding.ActivityNftDetailBinding
import io.outblock.lilico.page.ar.ArActivity
import io.outblock.lilico.page.nft.nftdetail.model.NftDetailModel
import io.outblock.lilico.page.nft.nftdetail.presenter.NftDetailPresenter
import io.outblock.lilico.page.nft.nftlist.cover
import io.outblock.lilico.page.nft.nftlist.video
import io.outblock.lilico.utils.isNightMode

class NftDetailActivity : BaseActivity() {

    private val uniqueId by lazy { intent.getStringExtra(EXTRA_NFT_UNIQUE_ID)!! }
    private lateinit var binding: ActivityNftDetailBinding
    private lateinit var presenter: NftDetailPresenter
    private lateinit var viewModel: NftDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNftDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        UltimateBarX.with(this).fitWindow(false).light(!isNightMode(this)).applyStatusBar()

        presenter = NftDetailPresenter(this, binding)
        viewModel = ViewModelProvider(this)[NftDetailViewModel::class.java].apply {
            nftLiveData.observe(this@NftDetailActivity) { presenter.bind(NftDetailModel(nft = it)) }
            load(uniqueId)
        }
    }

    override fun onPause() {
        super.onPause()
        presenter.bind(NftDetailModel(onPause = true))
    }

    override fun onRestart() {
        super.onRestart()
        presenter.bind(NftDetailModel(onRestart = true))
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.bind(NftDetailModel(onDestroy = true))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.nft_detail, menu)
        menu.findItem(R.id.view_in_ar).actionView.setOnClickListener { openInAr() }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.view_in_ar -> openInAr()
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun openInAr() {
        val nft = viewModel.nftLiveData.value ?: return
        ArActivity.launch(this, nft.cover(), nft.video())
    }

    companion object {
        private const val EXTRA_NFT_UNIQUE_ID = "extra_nft_unique_id"
        private const val EXTRA_WALLET_ADDRESS = "extra_wallet_address"

        fun launch(context: Context, uniqueId: String) {
            val intent = Intent(context, NftDetailActivity::class.java)
            intent.putExtra(EXTRA_NFT_UNIQUE_ID, uniqueId)
            context.startActivity(intent)
        }
    }
}