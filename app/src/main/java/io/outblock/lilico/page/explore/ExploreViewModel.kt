package io.outblock.lilico.page.explore

import android.annotation.SuppressLint
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.database.AppDataBase
import io.outblock.lilico.database.Bookmark
import io.outblock.lilico.database.WebviewRecord
import io.outblock.lilico.utils.viewModelIOScope

class ExploreViewModel : ViewModel() {
    val recentLiveData = MutableLiveData<List<WebviewRecord>>()
    val bookmarkLiveData = MutableLiveData<List<Bookmark>>()

    @SuppressLint("StaticFieldLeak")
    private lateinit var activity: FragmentActivity

    fun bindActivity(activity: FragmentActivity) {
        this.activity = activity
        registerObserve()
    }

    fun load() {
        viewModelIOScope(this) {
            recentLiveData.postValue(AppDataBase.database().webviewRecordDao().findAll(limit = 40))
        }
    }

    private fun registerObserve() {
        AppDataBase.database().webviewRecordDao().findAllLive(limit = 40).observe(activity) { recentLiveData.postValue(it) }
    }
}