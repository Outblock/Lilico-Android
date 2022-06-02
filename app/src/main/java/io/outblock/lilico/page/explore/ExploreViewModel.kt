package io.outblock.lilico.page.explore

import android.annotation.SuppressLint
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import io.outblock.lilico.database.AppDataBase
import io.outblock.lilico.database.Bookmark
import io.outblock.lilico.database.WebviewRecord
import io.outblock.lilico.utils.cpuScope
import io.outblock.lilico.utils.isBookmarkPrepopulateFilled
import io.outblock.lilico.utils.setBookmarkPrepopulateFilled
import io.outblock.lilico.utils.viewModelIOScope

class ExploreViewModel : ViewModel() {
    val recentLiveData = MutableLiveData<List<WebviewRecord>>()
    val bookmarkLiveData = MutableLiveData<List<Bookmark>>()

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
            if (!isBookmarkPrepopulateFilled()) {
                val json = Firebase.remoteConfig.getString("dapp")
                Gson().fromJson<List<DApp>>(json, object : TypeToken<List<DApp>>() {}.type).forEach { dapp ->
                    AppDataBase.database().bookmarkDao()
                        .save(
                            Bookmark(
                                url = dapp.url,
                                title = dapp.name,
                                isFavourite = false,
                                createTime = System.currentTimeMillis(),
                            )
                        )
                }
                setBookmarkPrepopulateFilled(true)
            }

            val data = AppDataBase.database().bookmarkDao().findAll(limit = 10)
            bookmarkLiveData.postValue(data)
        }
    }
}

private data class DApp(
    @SerializedName("category")
    val category: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("logo")
    val logo: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("testnet_url")
    val testnetUrl: String,
    @SerializedName("url")
    val url: String
)