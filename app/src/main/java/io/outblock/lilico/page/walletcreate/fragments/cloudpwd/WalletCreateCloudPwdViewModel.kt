package io.outblock.lilico.page.walletcreate.fragments.cloudpwd

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import io.outblock.lilico.manager.drive.ACTION_GOOGLE_DRIVE_UPLOAD_FINISH
import io.outblock.lilico.manager.drive.EXTRA_SUCCESS
import io.outblock.lilico.manager.drive.GoogleDriveAuthActivity
import io.outblock.lilico.utils.Env

class WalletCreateCloudPwdViewModel : ViewModel() {

    val backupCallbackLiveData = MutableLiveData<Boolean>()

    private val uploadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            val isSuccess = intent?.getBooleanExtra(EXTRA_SUCCESS, false) ?: return
            backupCallbackLiveData.postValue(isSuccess)
        }
    }

    init {
        LocalBroadcastManager.getInstance(Env.getApp()).registerReceiver(uploadReceiver, IntentFilter(ACTION_GOOGLE_DRIVE_UPLOAD_FINISH))
    }


    fun backup(context: Context, pwd: String) {
        GoogleDriveAuthActivity.launch(context, pwd)
    }
}