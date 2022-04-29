package io.outblock.lilico.page.explore.subpage

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.database.AppDataBase
import io.outblock.lilico.database.WebviewRecord
import io.outblock.lilico.utils.viewModelIOScope

class RecentHistoryViewModel : ViewModel() {
    val recentLiveData = MutableLiveData<List<WebviewRecord>>()

    @SuppressLint("StaticFieldLeak")
    private lateinit var fragment: Fragment

    fun bindFragment(fragment: Fragment) {
        this.fragment = fragment
        registerObserve()
    }

    fun load() {
        viewModelIOScope(this) {
            recentLiveData.postValue(AppDataBase.database().webviewRecordDao().findAll(limit = 100))
        }
    }

    private fun registerObserve() {
        AppDataBase.database().webviewRecordDao().findAllLive(limit = 40).observe(fragment) { recentLiveData.postValue(it) }
    }
}