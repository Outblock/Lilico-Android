package io.outblock.lilico.page.explore

import android.annotation.SuppressLint
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.outblock.lilico.database.AppDataBase
import io.outblock.lilico.database.Bookmark
import io.outblock.lilico.database.WebviewRecord
import io.outblock.lilico.manager.app.isTestnet
import io.outblock.lilico.page.explore.model.DAppModel
import io.outblock.lilico.utils.cpuScope
import io.outblock.lilico.utils.viewModelIOScope

class ExploreViewModel : ViewModel() {
    val recentLiveData = MutableLiveData<List<WebviewRecord>>()
    val bookmarkLiveData = MutableLiveData<List<Bookmark>>()
    val dAppsLiveData = MutableLiveData<List<DAppModel>>()

    val onDAppClickLiveData = MutableLiveData<String>()

    @SuppressLint("StaticFieldLeak")
    private lateinit var activity: FragmentActivity

    fun bindActivity(activity: FragmentActivity) {
        this.activity = activity
        registerObserve()
    }

    fun load() {
        viewModelIOScope(this) {
            recentLiveData.postValue(AppDataBase.database().webviewRecordDao().findAll(limit = 4))
            refreshBookmark()
            refreshDApps()
        }
    }

    fun onDAppClick(url: String) {
        onDAppClickLiveData.postValue(url)
    }

    private fun registerObserve() {
        AppDataBase.database().webviewRecordDao().findAllLive(limit = 4).observe(activity) { recentLiveData.postValue(it) }
        AppDataBase.database().bookmarkDao().findAllLive(limit = 10).observe(activity) { refreshBookmark() }
    }

    private fun refreshBookmark() {
        cpuScope {
            val data = AppDataBase.database().bookmarkDao().findAll(limit = 10)
            bookmarkLiveData.postValue(data)
        }
    }

    private fun refreshDApps() {
        val json = Firebase.remoteConfig.getString("dapp")
        val dApps = Gson().fromJson<List<DAppModel>>(json, object : TypeToken<List<DAppModel>>() {}.type).filter {
            if (isTestnet()) !it.testnetUrl.isNullOrBlank() else !it.url.isNullOrBlank()
        }
        dAppsLiveData.postValue(dApps)
    }
}