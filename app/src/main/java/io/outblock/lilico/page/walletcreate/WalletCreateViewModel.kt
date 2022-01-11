package io.outblock.lilico.page.walletcreate

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.firebase.auth.isAnonymousSignIn

class WalletCreateViewModel : ViewModel() {

    val onStepChangeLiveData = MutableLiveData<Int>()

    private var step = if (isAnonymousSignIn()) WALLET_CREATE_STEP_LEGAL else WALLET_CREATE_STEP_MNEMONIC

    init {
        changeStep(step)
    }

    fun nextStep() {
        step++
        onStepChangeLiveData.postValue(step)
    }

    fun changeStep(step: Int) {
        this.step = step
        onStepChangeLiveData.postValue(step)
    }

    fun handleBackPressed():Boolean{
        if(step == WALLET_CREATE_STEP_MNEMONIC_CHECK || step == WALLET_CREATE_STEP_CLOUD_PWD){
            changeStep(WALLET_CREATE_STEP_MNEMONIC)
            return true
        }
        return false
    }
}