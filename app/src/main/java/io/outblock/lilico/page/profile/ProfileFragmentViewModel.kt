package io.outblock.lilico.page.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.cache.userInfoCache
import io.outblock.lilico.manager.app.isTestnet
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.OtherHostService
import io.outblock.lilico.network.model.UserInfoData
import io.outblock.lilico.network.retrofit
import io.outblock.lilico.network.retrofitWithHost
import io.outblock.lilico.page.inbox.countUnreadInbox
import io.outblock.lilico.utils.loge
import io.outblock.lilico.utils.meowDomain
import io.outblock.lilico.utils.viewModelIOScope

class ProfileFragmentViewModel : ViewModel() {

    val profileLiveData = MutableLiveData<UserInfoData>()
    val inboxCountLiveData = MutableLiveData<Int>()

    fun load() {
        viewModelIOScope(this) {
            requestUserInfo()
            requestInboxCount()
        }
    }

    private suspend fun requestUserInfo() {
        userInfoCache().read()?.let { profileLiveData.postValue(it) }

        try {
            val service = retrofit().create(ApiService::class.java)
            val data = service.userInfo().data
            if (data != profileLiveData.value) {
                profileLiveData.postValue(data)
                userInfoCache().cache(data)
            }
        } catch (e: Exception) {
            loge(e)
        }
    }

    private suspend fun requestInboxCount() {
        val domain = meowDomain() ?: return
        val service = retrofitWithHost(if (isTestnet()) "https://testnet.flowns.io/" else "https://flowns.io").create(OtherHostService::class.java)
        val response = service.queryInbox(domain)
        val count = countUnreadInbox(response)
        inboxCountLiveData.postValue(count)
    }

}