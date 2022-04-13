package io.outblock.lilico.page.nft.collectionlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import com.zackratos.ultimatebarx.ultimatebarx.UltimateBarX
import io.outblock.lilico.R
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.databinding.ActivityNftCollectionListBinding
import io.outblock.lilico.page.nft.collectionlist.model.NftCollectionListModel
import io.outblock.lilico.page.nft.collectionlist.presenter.NftCollectionListPresenter
import io.outblock.lilico.utils.isNightMode

class NftCollectionListActivity : BaseActivity() {

    private lateinit var presenter: NftCollectionListPresenter
    private lateinit var viewModel: NftCollectionListViewModel
    private lateinit var binding: ActivityNftCollectionListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNftCollectionListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        UltimateBarX.with(this).fitWindow(true).colorRes(R.color.background).light(!isNightMode(this)).applyStatusBar()

        presenter = NftCollectionListPresenter(this, binding)
        viewModel = ViewModelProvider(this)[NftCollectionListViewModel::class.java].apply {
            collectionListLiveData.observe(this@NftCollectionListActivity) { presenter.bind(NftCollectionListModel(data = it)) }
            load()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, NftCollectionListActivity::class.java))
        }
    }
}