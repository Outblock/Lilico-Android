package io.outblock.lilico.page.collection

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import com.zackratos.ultimatebarx.ultimatebarx.UltimateBarX
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.databinding.ActivityCollectionBinding
import io.outblock.lilico.page.collection.model.CollectionContentModel
import io.outblock.lilico.page.collection.presenter.CollectionContentPresenter
import io.outblock.lilico.utils.isNightMode

class CollectionActivity : BaseActivity() {

    private lateinit var presenter: CollectionContentPresenter
    private lateinit var viewModel: CollectionViewModel
    private lateinit var binding: ActivityCollectionBinding

    private val address by lazy { intent.getStringExtra(EXTRA_ADDRESS).orEmpty() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCollectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        UltimateBarX.with(this).fitWindow(false).light(!isNightMode(this)).applyStatusBar()

        presenter = CollectionContentPresenter(this, binding)
        viewModel = ViewModelProvider(this)[CollectionViewModel::class.java].apply {
            dataLiveData.observe(this@CollectionActivity) { presenter.bind(CollectionContentModel(data = it)) }
            load(address)
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
        private const val EXTRA_ADDRESS = "extra_address"

        fun launch(context: Context, address: String) {
            context.startActivity(Intent(context, CollectionActivity::class.java).apply {
                putExtra(EXTRA_ADDRESS, address)
            })
        }
    }
}