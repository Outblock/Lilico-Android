package io.outblock.lilico.page.browser

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WebviewViewModel : ViewModel() {

    val urlLiveData = MutableLiveData<String>()
}