package io.outblock.lilico.page.nft

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.R
import io.outblock.lilico.cache.NFTListCache
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.retrofit
import io.outblock.lilico.page.nft.model.NFTTitleModel
import io.outblock.lilico.page.nft.model.GridHeaderPlaceholderModel
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.extensions.res2color
import io.outblock.lilico.utils.viewModelIOScope

class NFTFragmentViewModel : ViewModel() {

    val dataLiveData = MutableLiveData<List<Any>>()

    private var isGridMode = true

    fun load() {
        viewModelIOScope(this) {
            NFTListCache.read()?.nfts?.let { dataLiveData.postValue(it.addHeader()) }
            val service = retrofit().create(ApiService::class.java)
            val resp = service.nftList()
            NFTListCache.cache(resp.data)
            dataLiveData.postValue(resp.data.nfts.addHeader())
        }
    }

    private fun List<Any>.addHeader(): List<Any> {
        if (size == 0) {
            return this
        }
        val list = this.toMutableList()
        if (isGridMode) {
            list.add(
                0,
                NFTTitleModel(
                    icon = R.drawable.ic_collection_star,
                    iconTint = R.color.text.res2color(),
                    text = R.string.top_selection.res2String(),
                )
            )
            list.add(0, GridHeaderPlaceholderModel())
        }
        return list
    }
}