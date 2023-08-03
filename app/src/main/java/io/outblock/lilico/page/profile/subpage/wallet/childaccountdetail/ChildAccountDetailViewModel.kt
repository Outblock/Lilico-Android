package io.outblock.lilico.page.profile.subpage.wallet.childaccountdetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.manager.childaccount.ChildAccount
import io.outblock.lilico.manager.config.NftCollection
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.viewModelIOScope

class ChildAccountDetailViewModel : ViewModel() {
    val nftCollectionsLiveData = MutableLiveData<List<NftCollection>>()

    fun queryCollection(account: ChildAccount) {
        viewModelIOScope(this) {
            queryNft(account)
        }
    }

    private fun queryNft(account: ChildAccount) {
        viewModelIOScope(this) {
            val collections = queryChildAccountNftCollections(account)
            logd("ChildAccountDetailViewModel", collections)
        }
    }

}

data class CollectionInfo(
    val collectionId: String,
    val displayId: String,
    val name: String,
    val icon: String
)