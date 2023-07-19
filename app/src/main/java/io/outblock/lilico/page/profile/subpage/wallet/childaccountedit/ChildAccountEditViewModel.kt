package io.outblock.lilico.page.profile.subpage.wallet.childaccountedit

import android.graphics.BitmapFactory
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.nftco.flow.sdk.FlowTransactionStatus
import io.outblock.lilico.R
import io.outblock.lilico.firebase.storage.uploadAvatarToFirebase
import io.outblock.lilico.manager.childaccount.ChildAccount
import io.outblock.lilico.manager.flowjvm.CADENCE_EDIT_CHILD_ACCOUNT
import io.outblock.lilico.manager.flowjvm.transactionByMainWallet
import io.outblock.lilico.manager.transaction.TransactionState
import io.outblock.lilico.manager.transaction.TransactionStateManager
import io.outblock.lilico.page.window.bubble.tools.pushBubbleStack
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.loge
import io.outblock.lilico.utils.toast
import io.outblock.lilico.utils.viewModelIOScope
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ChildAccountEditViewModel : ViewModel() {

    val progressDialogVisibleLiveData = MutableLiveData<Boolean>()
    val transactionFinishLiveData = MutableLiveData<Boolean>()

    private var avatarFilePath: String? = null
    private lateinit var childAccount: ChildAccount

    fun bindAccount(account: ChildAccount) {
        childAccount = account
    }

    fun updateAvatar(filePath: String) {
        this.avatarFilePath = filePath
    }

    fun save(name: String, description: String) {
        progressDialogVisibleLiveData.postValue(true)
        viewModelIOScope(this) {
            var avatarUrl: String? = childAccount.icon
            if (avatarFilePath != null && File(avatarFilePath!!).exists()) {
                avatarUrl = uploadAvatar()
                if (avatarUrl.isNullOrEmpty()) {
                    progressDialogVisibleLiveData.postValue(false)
                    return@viewModelIOScope
                }
            }

            val txid = CADENCE_EDIT_CHILD_ACCOUNT.transactionByMainWallet {
                arg { address(childAccount.address) }
                arg { string(name) }
                arg { string(description) }
                arg { string(avatarUrl.orEmpty()) }
            }

            if (txid.isNullOrBlank()) {
                toast(R.string.edit_fail)
            } else {
                val transactionState = TransactionState(
                    transactionId = txid,
                    time = System.currentTimeMillis(),
                    state = FlowTransactionStatus.PENDING.num,
                    type = TransactionState.TYPE_TRANSACTION_DEFAULT,
                    data = Gson().toJson(childAccount),
                )
                TransactionStateManager.newTransaction(transactionState)
                pushBubbleStack(transactionState)
                transactionFinishLiveData.postValue(true)
            }
            progressDialogVisibleLiveData.postValue(false)
        }
    }

    private suspend fun uploadAvatar() = suspendCoroutine { continuation ->
        runBlocking {
            try {
                val bitmap = BitmapFactory.decodeFile(avatarFilePath!!)
                uploadAvatarToFirebase(bitmap) { avatarUrl ->
                    logd("upload avatar url", avatarUrl)
                    viewModelIOScope(this@ChildAccountEditViewModel) {
                        continuation.resume(avatarUrl)
                    }
                }
            } catch (e: Exception) {
                loge(e)
                continuation.resume(null)
            }
        }
    }

}