package io.outblock.lilico.manager.drive

import com.google.api.services.drive.Drive
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.loge


private const val TAG = "GoogleDriveUtils"

fun testGoogleDrive(driveService: Drive) {
    ioScope {
        runCatching {
            try {
                logd(TAG, "testGoogleDrive")
                val driveServiceHelper = DriveServerHelper(driveService)
                val fileId = driveServiceHelper.createFile()
                logd(TAG, "fileId:$fileId")

                driveServiceHelper.saveFile(fileId, "outblock.test.txt", "Hello World!")

                val readText = driveServiceHelper.readFile(fileId)
                logd(TAG, "readText:$readText")
            } catch (e: Exception) {
                loge(e)
            }
        }
    }
}