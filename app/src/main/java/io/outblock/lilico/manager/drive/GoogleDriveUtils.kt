package io.outblock.lilico.manager.drive

import android.content.Intent
import androidx.annotation.WorkerThread
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.api.services.drive.Drive
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.outblock.lilico.BuildConfig
import io.outblock.lilico.utils.Env
import io.outblock.lilico.utils.getUsername
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.loge
import io.outblock.lilico.utils.secret.aesDecrypt
import io.outblock.lilico.utils.secret.aesEncrypt
import io.outblock.lilico.wallet.getMnemonic
import java.io.IOException


private const val TAG = "GoogleDriveUtils"

private const val FILE_NAME = "outblock_backup"

const val ACTION_GOOGLE_DRIVE_UPLOAD_FINISH = "ACTION_GOOGLE_DRIVE_UPLOAD_FINISH"
const val EXTRA_SUCCESS = "extra_success"

private const val AES_KEY = "4047b6b927bcff0c"

@WorkerThread
@Throws(IOException::class)
suspend fun uploadMnemonicToGoogleDrive(driveService: Drive, password: String) {
    try {
        logd(TAG, "uploadMnemonicToGoogleDrive")
        val driveServiceHelper = DriveServerHelper(driveService)
        val data = existingData(driveService).toMutableList()
        if (data.isEmpty()) {
            driveServiceHelper.createFile(FILE_NAME)
        }

        addData(data, password)

        driveServiceHelper.writeStringToFile(FILE_NAME, aesEncrypt(password, message = aesEncrypt(password, message = Gson().toJson(data))))

        if (BuildConfig.DEBUG) {
            val readText = driveServiceHelper.readFile(driveServiceHelper.getFileId(FILE_NAME)!!)
            logd(TAG, "readText:$readText")
        }
        sendCallback(true)
    } catch (e: Exception) {
        loge(e)
        sendCallback(false)
    }
}

private fun existingData(driveService: Drive): List<DriveItem> {
    val driveServiceHelper = DriveServerHelper(driveService)
    val fileId = driveServiceHelper.getFileId(FILE_NAME) ?: return emptyList()
    return try {
        val content = driveServiceHelper.readFile(fileId).second
        val json = aesDecrypt(AES_KEY, message = content)
        logd(TAG, "existingData:$json")
        Gson().fromJson(json, object : TypeToken<List<DriveItem>>() {}.type)
    } catch (e: Exception) {
        loge(e)
        emptyList()
    }
}

private suspend fun addData(data: MutableList<DriveItem>, password: String) {
    val exist = data.firstOrNull { it.username == getUsername() }
    if (exist == null) {
        val dataStr = Gson().toJson(DriveData(version = BuildConfig.VERSION_NAME, data = getMnemonic(), address = ""))
        data.add(DriveItem(getUsername(), aesEncrypt(password, message = dataStr)))
    } else {
        exist.data = aesEncrypt(password, message = Gson().toJson(DriveData(version = BuildConfig.VERSION_NAME, data = getMnemonic(), address = "")))
    }
}

private fun sendCallback(isSuccess: Boolean) {
    LocalBroadcastManager.getInstance(Env.getApp()).sendBroadcast(Intent(ACTION_GOOGLE_DRIVE_UPLOAD_FINISH).apply {
        putExtra(EXTRA_SUCCESS, isSuccess)
    })
}