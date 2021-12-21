package io.outblock.lilico.manager.drive

import android.content.Intent
import androidx.annotation.WorkerThread
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.api.services.drive.Drive
import io.outblock.lilico.BuildConfig
import io.outblock.lilico.utils.Env
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.loge
import io.outblock.lilico.utils.secret.aesEncrypt
import io.outblock.lilico.wallet.getMnemonic


private const val TAG = "GoogleDriveUtils"

private const val FILE_NAME = "outblock_backup"

const val ACTION_GOOGLE_DRIVE_UPLOAD_FINISH = "ACTION_GOOGLE_DRIVE_UPLOAD_FINISH"
const val EXTRA_SUCCESS = "extra_success"

@WorkerThread
fun uploadMnemonicToGoogleDrive(driveService: Drive, password: String) {
    try {
        logd(TAG, "uploadMnemonicToGoogleDrive")
        val driveServiceHelper = DriveServerHelper(driveService)
        val fileId = driveServiceHelper.createFile()
        logd(TAG, "fileId:$fileId")

        driveServiceHelper.saveFile(fileId, FILE_NAME, aesEncrypt(password, message = getMnemonic()))

        if (BuildConfig.DEBUG) {
            val readText = driveServiceHelper.readFile(fileId)
            logd(TAG, "readText:$readText")
        }
        sendCallback(true)
    } catch (e: Exception) {
        loge(e)
        sendCallback(false)
    }
}

private fun sendCallback(isSuccess: Boolean) {
    LocalBroadcastManager.getInstance(Env.getApp()).sendBroadcast(Intent(ACTION_GOOGLE_DRIVE_UPLOAD_FINISH).apply {
        putExtra(EXTRA_SUCCESS, isSuccess)
    })
}