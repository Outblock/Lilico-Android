package io.outblock.lilico.page.walletrestore

import androidx.annotation.WorkerThread
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import io.outblock.lilico.R
import io.outblock.lilico.firebase.auth.firebaseCustomLogin
import io.outblock.lilico.firebase.auth.getFirebaseJwt
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.retrofit
import io.outblock.lilico.utils.*
import io.outblock.lilico.utils.secret.aesEncrypt
import io.outblock.lilico.wallet.getMnemonicAesKey
import io.outblock.lilico.wallet.getPublicKey
import io.outblock.lilico.wallet.sign
import kotlinx.coroutines.runBlocking
import wallet.core.jni.HDWallet


const val WALLET_RESTORE_STEP_GUIDE = 0
const val WALLET_RESTORE_STEP_DRIVE_USERNAME = 1
const val WALLET_RESTORE_STEP_DRIVE_PASSWORD = 2
const val WALLET_RESTORE_STEP_MNEMONIC = 3

const val ERROR_CUSTOM_TOKEN = 1
const val ERROR_UID = 2
const val ERROR_FIREBASE_SIGN_IN = 3
const val ERROR_NETWORK = 4
const val ERROR_ACCOUNT_NOT_FOUND = 5

private const val TAG = "WalletRestoreUtils"

private val WALLET_STEP_ROOT_ID = mapOf(
    WALLET_RESTORE_STEP_GUIDE to R.id.fragment_wallet_restore_guide,
    WALLET_RESTORE_STEP_DRIVE_USERNAME to R.id.fragment_wallet_restore_drive_username,
    WALLET_RESTORE_STEP_DRIVE_PASSWORD to R.id.fragment_wallet_restore_drive_password,
    WALLET_RESTORE_STEP_MNEMONIC to R.id.fragment_wallet_restore_mnemonic,
)

fun getRootIdByStep(step: Int) = WALLET_STEP_ROOT_ID[step]!!

fun requestWalletRestoreLogin(mnemonic: String, callback: (isSuccess: Boolean, reason: Int?) -> Unit) {
    ioScope {
        val wallet = HDWallet(mnemonic, "")

        getFirebaseUid { uid ->
            if (uid.isNullOrBlank()) {
                callback.invoke(false, ERROR_UID)
            }
            runBlocking {
                val catching = runCatching {
                    val service = retrofit().create(ApiService::class.java)
                    val resp = service.login(mapOf("public_key" to wallet.getPublicKey(), "signature" to wallet.sign(uid!!)))
                    if (resp.data?.customToken.isNullOrBlank()) {
                        if (resp.status == 404) {
                            callback.invoke(false, ERROR_ACCOUNT_NOT_FOUND)
                        } else {
                            callback.invoke(false, ERROR_CUSTOM_TOKEN)
                        }
                    } else {
                        firebaseLogin(resp.data?.customToken!!) { isSuccess ->
                            if (isSuccess) {
                                saveMnemonic(aesEncrypt(getMnemonicAesKey(), message = mnemonic))
                                setRegistered()
                                callback.invoke(true, null)
                            } else {
                                callback.invoke(false, ERROR_FIREBASE_SIGN_IN)
                            }
                        }
                    }
                }

                if (catching.isFailure) {
                    loge(catching.exceptionOrNull())
                    callback.invoke(false, ERROR_NETWORK)
                }
            }
        }
    }
}

@WorkerThread
private fun firebaseLogin(customToken: String, callback: (isSuccess: Boolean) -> Unit) {
    clearJwtToken()

    logd(TAG, "start delete user")
    FirebaseMessaging.getInstance().deleteToken()
    Firebase.auth.currentUser?.delete()?.addOnCompleteListener {
        logd(TAG, "delete user finish exception:${it.exception}")
        if (it.isSuccessful) {
            firebaseCustomLogin(customToken) { isSuccessful, _ ->
                if (isSuccessful) {
                    callback(true)
                } else callback(false)
            }
        } else callback(false)
    }
}

private suspend fun getFirebaseUid(callback: (uid: String?) -> Unit) {
    val uid = Firebase.auth.currentUser?.uid
    if (!uid.isNullOrBlank()) {
        callback.invoke(uid)
        return
    }

    getFirebaseJwt(true)

    callback.invoke(Firebase.auth.currentUser?.uid)
}