package io.outblock.lilico.page.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.cache.userInfoCache
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.model.UserInfoData
import io.outblock.lilico.network.retrofit
import io.outblock.lilico.utils.viewModelIOScope

class ProfileFragmentViewModel : ViewModel() {

    val profileLiveData = MutableLiveData<UserInfoData>()

    fun load() {
        viewModelIOScope(this) {
            userInfoCache().read()?.let { profileLiveData.postValue(it) }

            val service = retrofit().create(ApiService::class.java)
            val data = service.userInfo().data
            if (data != profileLiveData.value) {
                profileLiveData.postValue(data)
                userInfoCache().cache(data)
            }
        }
    }

}