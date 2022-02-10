package io.outblock.lilico.page.profile.subpage.nickname

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.cache.userInfoCache
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.model.UserInfoData
import io.outblock.lilico.network.retrofit
import io.outblock.lilico.utils.viewModelIOScope
import kotlinx.coroutines.delay

class EditNicknameViewModel : ViewModel() {
    val userInfoLiveData = MutableLiveData<UserInfoData>()

    val resultLiveData = MutableLiveData<Boolean>()

    fun load() {
        viewModelIOScope(this) {
            userInfoCache().read()?.let { userInfoLiveData.postValue(it) }
        }
    }

    fun save(name: String) {
        viewModelIOScope(this) {
            val userInfo = userInfoCache().read()!!
            val service = retrofit().create(ApiService::class.java)
            val resp = service.updateProfile(mapOf("nickname" to name, "avatar" to userInfo.avatar))
            if (resp.status == 200) {
                userInfo.nickname = name
                userInfoCache().cache(userInfo)
                delay(200)
            }
            resultLiveData.postValue(resp.status == 200)
        }
    }
}