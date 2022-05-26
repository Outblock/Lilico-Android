package io.outblock.lilico.page.walletcreate.fragments.username

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.R
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.retrofit
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.usernameVerify
import io.outblock.lilico.utils.viewModelIOScope

class WalletCreateUsernameViewModel : ViewModel() {

    val usernameStateLiveData = MutableLiveData<Pair<Boolean, String>>()

    private val handler = Handler(Looper.getMainLooper())

    private val usernameCheckTask = Runnable { verifyUsernameRemote() }

    private var username: String = ""

    fun verifyUsername(username: String) {
        this.username = username
        val verifyMsg = usernameVerify(username)
        if (!verifyMsg.isNullOrEmpty()) {
            usernameStateLiveData.postValue(Pair(false, verifyMsg))
            return
        }

        handler.removeCallbacks(usernameCheckTask)
        handler.postDelayed(usernameCheckTask, 500)
    }

    private fun verifyUsernameRemote() {
        viewModelIOScope(this) {
            val service = retrofit().create(ApiService::class.java)
            val resp = service.checkUsername(username)
            logd(TAG, "verifyUsernameRemote resp:$resp")
            logd(TAG, "verifyUsernameRemote resp data:${resp.data}")
            logd(TAG, "verifyUsernameRemote resp username:${resp.data.username}")
            if (resp.data.username.lowercase() == username.lowercase()) {
                val msg = if (resp.data.unique) R.string.username_success.res2String() else R.string.username_exist.res2String()
                usernameStateLiveData.postValue(Pair(resp.data.unique, msg))
            }
        }
    }

    companion object {
        private const val TAG = "WalletCreateUsername"
    }
}