package io.outblock.lilico.manager.drive

import android.content.Intent
import androidx.annotation.WorkerThread
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.api.services.drive.Drive
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.outblock.lilico.BuildConfig
import io.outblock.lilico.cache.userInfoCache
import io.outblock.lilico.utils.Env
import io.outblock.lilico.utils.getUsername
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.loge
import io.outblock.lilico.utils.secret.aesDecrypt
import io.outblock.lilico.utils.secret.aesEncrypt
import io.outblock.lilico.wallet.getMnemonic


private const val TAG = "GoogleDriveUtils"

private const val FILE_NAME = "outblock_backup"

const val ACTION_GOOGLE_DRIVE_UPLOAD_FINISH = "ACTION_GOOGLE_DRIVE_UPLOAD_FINISH"
const val ACTION_GOOGLE_DRIVE_DELETE_FINISH = "ACTION_GOOGLE_DRIVE_DELETE_FINISH"
const val ACTION_GOOGLE_DRIVE_RESTORE_FINISH = "ACTION_GOOGLE_DRIVE_RESTORE_FINISH"
const val EXTRA_SUCCESS = "extra_success"
const val EXTRA_CONTENT = "extra_content"

private const val AES_KEY = "4047b6b927bcff0c"

@WorkerThread
suspend fun uploadMnemonicToGoogleDrive(driveService: Drive, password: String) {
    try {
        logd(TAG, "uploadMnemonicToGoogleDrive")
        val driveServiceHelper = DriveServerHelper(driveService)
        val data = existingData(driveService).toMutableList()
        if (data.isEmpty()) {
            driveServiceHelper.createFile(FILE_NAME)
        }

        addData(data, password)

        driveServiceHelper.writeStringToFile(FILE_NAME, aesEncrypt(AES_KEY, message = Gson().toJson(data)))

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

@WorkerThread
fun restoreMnemonicFromGoogleDrive(driveService: Drive) {
    try {
        logd(TAG, "uploadMnemonicToGoogleDrive")
        val data = existingData(driveService)
        LocalBroadcastManager.getInstance(Env.getApp()).sendBroadcast(Intent(ACTION_GOOGLE_DRIVE_RESTORE_FINISH).apply {
            putParcelableArrayListExtra(EXTRA_CONTENT, data.toCollection(ArrayList()))
        })
    } catch (e: Exception) {
        loge(e)
        sendCallback(false)
    }
}

@WorkerThread
fun deleteMnemonicFromGoogleDrive(driveService: Drive) {
    try {
        logd(TAG, "deleteMnemonicFromGoogleDrive")
        val driveServiceHelper = DriveServerHelper(driveService)
        val data = existingData(driveService).toMutableList()
        if (data.isNotEmpty()) {
            val username = userInfoCache().read()?.username
            data.removeIf { it.username == username }

            driveServiceHelper.writeStringToFile(FILE_NAME, aesEncrypt(AES_KEY, message = Gson().toJson(data)))

            if (BuildConfig.DEBUG) {
                val readText = driveServiceHelper.readFile(driveServiceHelper.getFileId(FILE_NAME)!!)
                logd(TAG, "readText:$readText")
            }
        }
        sendDeleteCallback(true)
    } catch (e: Exception) {
        loge(e)
        sendDeleteCallback(false)
    }
}

private fun existingData(driveService: Drive): List<DriveItem> {
    val driveServiceHelper = DriveServerHelper(driveService)
    val fileId = driveServiceHelper.getFileId(FILE_NAME) ?: return emptyList()

    if (BuildConfig.DEBUG) {
        driveServiceHelper.fileList()?.files?.map {
            logd(TAG, "file list:${it.name}")
        }
    }

    return try {
        logd(TAG, "existingData fileId:$fileId")
        val content = driveServiceHelper.readFile(fileId).second
        logd(TAG, "existingData content:$content")
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
        data.add(DriveItem(getUsername(), version = BuildConfig.VERSION_NAME, data = aesEncrypt(password, message = getMnemonic())))
    } else {
        exist.version = BuildConfig.VERSION_NAME
        exist.data = aesEncrypt(password, message = getMnemonic())
    }
}

private fun sendCallback(isSuccess: Boolean) {
    LocalBroadcastManager.getInstance(Env.getApp()).sendBroadcast(Intent(ACTION_GOOGLE_DRIVE_UPLOAD_FINISH).apply {
        putExtra(EXTRA_SUCCESS, isSuccess)
    })
}

private fun sendDeleteCallback(isSuccess: Boolean) {
    LocalBroadcastManager.getInstance(Env.getApp()).sendBroadcast(Intent(ACTION_GOOGLE_DRIVE_DELETE_FINISH).apply {
        putExtra(EXTRA_SUCCESS, isSuccess)
    })
}