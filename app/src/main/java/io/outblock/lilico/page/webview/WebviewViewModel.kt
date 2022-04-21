package io.outblock.lilico.page.webview

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WebviewViewModel : ViewModel() {

    val urlLiveData = MutableLiveData<String>()
}