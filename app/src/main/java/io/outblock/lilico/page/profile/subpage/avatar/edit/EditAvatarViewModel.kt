package io.outblock.lilico.page.profile.subpage.avatar.edit

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.cache.nftListCache
import io.outblock.lilico.cache.userInfoCache
import io.outblock.lilico.firebase.storage.uploadAvatarToFirebase
import io.outblock.lilico.manager.wallet.WalletManager
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.model.UserInfoData
import io.outblock.lilico.network.retrofit
import io.outblock.lilico.page.nft.nftlist.cover
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.viewModelIOScope
import kotlinx.coroutines.delay

class EditAvatarViewModel : ViewModel() {
    val avatarListLiveData = MutableLiveData<List<Any>>()

    val selectedAvatarLiveData = MutableLiveData<Any>()

    val uploadResultLiveData = MutableLiveData<Boolean>()

    private var userInfo: UserInfoData? = null

    fun loadNft(userInfoData: UserInfoData) {
        this.userInfo = userInfoData
        viewModelIOScope(this) {
            val nftList = nftListCache(getNftAddress()).read()?.nfts.orEmpty().filter { !it.cover().isNullOrEmpty() }
            logd("xxx", "nftList: $nftList")
            avatarListLiveData.postValue(mutableListOf<Any>().apply {
                add(userInfoData.avatar)
                addAll(nftList)
            })
        }
    }

    fun selectAvatar(avatar: Any) {
        if (avatar != selectedAvatarLiveData.value) {
            selectedAvatarLiveData.postValue(avatar)
        }
    }

    fun uploadAvatar(bitmap: Bitmap) {
        viewModelIOScope(this) {
            if (selectedAvatarLiveData.value == null || selectedAvatarLiveData.value == userInfo?.avatar) {
                uploadResultLiveData.postValue(true)
                return@viewModelIOScope
            }
            try {
                uploadAvatarToFirebase(bitmap) { avatarUrl ->
                    logd("upload avatar url", avatarUrl)
                    viewModelIOScope(this) {
                        if (avatarUrl.isNullOrEmpty()) {
                            uploadResultLiveData.postValue(false)
                        }
                        val userInfo = userInfoCache().read()!!
                        val service = retrofit().create(ApiService::class.java)
                        val resp = service.updateProfile(mapOf("nickname" to userInfo.nickname, "avatar" to avatarUrl!!))
                        if (resp.status == 200) {
                            userInfo.avatar = avatarUrl
                            userInfoCache().cache(userInfo)
                            delay(200)
                        }
                        uploadResultLiveData.postValue(resp.status == 200)
                    }
                }
            } catch (e: Exception) {
                uploadResultLiveData.postValue(false)
            }
        }
    }

    private fun getNftAddress(): String? {
        return WalletManager.selectedWalletAddress()
    }
}