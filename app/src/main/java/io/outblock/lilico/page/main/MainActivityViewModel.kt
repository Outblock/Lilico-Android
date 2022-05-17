package io.outblock.lilico.page.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {

    val walletRegisterSuccessLiveData = MutableLiveData<Boolean>()

    internal val changeTabLiveData = MutableLiveData<HomeTab>()

    fun changeTab(tab: HomeTab) {
        changeTabLiveData.postValue(tab)
    }
}