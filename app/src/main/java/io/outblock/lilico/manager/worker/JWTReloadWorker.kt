package io.outblock.lilico.manager.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import io.outblock.lilico.firebase.auth.firebaseJwt
import io.outblock.lilico.utils.logd

/**
 * Refresh Firebase jwt token periodically
 */
class JWTReloadWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    override fun doWork(): Result {
        logd(TAG, "doWork")
        firebaseJwt()
        return Result.success()
    }

    companion object {
        private val TAG = JWTReloadWorker::class.java.simpleName
    }
}
