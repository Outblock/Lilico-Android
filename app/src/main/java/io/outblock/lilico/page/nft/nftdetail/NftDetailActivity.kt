package io.outblock.lilico.page.nft.nftdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import com.zackratos.ultimatebarx.ultimatebarx.UltimateBarX
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.databinding.ActivityNftDetailBinding
import io.outblock.lilico.page.nft.nftdetail.model.NftDetailModel
import io.outblock.lilico.page.nft.nftdetail.presenter.NftDetailPresenter
import io.outblock.lilico.utils.isNightMode

class NftDetailActivity : BaseActivity() {

    private val nftAddress by lazy { intent.getStringExtra(EXTRA_NFT_ADDRESS)!! }
    private val walletAddress by lazy { intent.getStringExtra(EXTRA_WALLET_ADDRESS)!! }
    private val tokenId by lazy { intent.getStringExtra(EXTRA_TOKEN_ID)!! }
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
            load(walletAddress, nftAddress, tokenId)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    companion object {
        private const val EXTRA_NFT_ADDRESS = "extra_nft_address"
        private const val EXTRA_WALLET_ADDRESS = "extra_wallet_address"
        private const val EXTRA_TOKEN_ID = "extra_token_id"

        fun launch(
            context: Context,
            walletAddress: String,
            address: String,
            tokenId: String,
        ) {
            val intent = Intent(context, NftDetailActivity::class.java)
            intent.putExtra(EXTRA_NFT_ADDRESS, address)
            intent.putExtra(EXTRA_WALLET_ADDRESS, walletAddress)
            intent.putExtra(EXTRA_TOKEN_ID, tokenId)
            context.startActivity(intent)
        }
    }
}