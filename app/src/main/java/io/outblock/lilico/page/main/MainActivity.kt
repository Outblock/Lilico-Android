package io.outblock.lilico.page.main

import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.outblock.lilico.R
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.firebase.auth.firebaseCustomLogin
import io.outblock.lilico.page.walletcreate.WalletCreateActivity
import io.outblock.lilico.utils.*

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.create_button).setOnClickListener { WalletCreateActivity.launch(this) }
//        testFirebaseAuth()

//        GoogleDriveAuthActivity.launch(this)
    }

    private fun testFirebaseAuth() {
        ioScope {
            firebaseCustomLogin(FIREBASE_TEST_TOKEN) { isSuccessful, exception ->
                if (isSuccessful) {
                    logw(TAG, "firebase uid:${FirebaseAuth.getInstance().currentUser?.uid}")
                    Firebase.auth.currentUser?.getIdToken(true)?.addOnCompleteListener { jwtTask ->
                        if (jwtTask.isSuccessful) {
                            val token = jwtTask.result.token.orEmpty()
                            saveJwtToken(token)
                            logd(TAG, "jwt token:$token")
                        }
                    }
                } else {
                    loge(TAG, "firebase fetch uid error:${exception}")
                }
            }
        }
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private val FIREBASE_TEST_TOKEN =
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJmaXJlYmFzZS1hZG1pbnNkay02ODR6MEBsaWxpY28tZGV2LmlhbS5nc2VydmljZWFjY291bnQuY29tIiwiYXVkIjoiaHR0cHM6Ly9pZGVudGl0eXRvb2xraXQuZ29vZ2xlYXBpcy5jb20vZ29vZ2xlLmlkZW50aXR5LmlkZW50aXR5dG9vbGtpdC52MS5JZGVudGl0eVRvb2xraXQiLCJleHAiOjE2Mzk0NzMyMjQsImlhdCI6MTYzOTQ2OTYyNCwic3ViIjoiZmlyZWJhc2UtYWRtaW5zZGstNjg0ejBAbGlsaWNvLWRldi5pYW0uZ3NlcnZpY2VhY2NvdW50LmNvbSIsInVpZCI6ImY2MzdmYzI4LTcwYzktNDE2Ny1hY2NmLTA1ZjU1ZDE4MTAxOCJ9.e3ALkc_hBkNNUWakHI2n_q-h5NfXePAhRTXjbZNPYoE0bLXnZFgrdEN39X_Xq4eKVvnznMVc_YTQWtZkdaWRlMRgFQmynExe6RK-Nn0PgsRLZGh-gvEWlnQOOD_RjpeKRCBnwU9qkPP23wxNLap2ANsOshBu1Wfr3OMw9coZFfLpFgp5J3BV1p9geaaJkgC0Pucyp08NmZ1fRqzoUzr7vUBTISaFSxl8oJDU9kLpBDmp21E0t-Jygp9FAsm158Q6l-4Robu6AeetD5MsEFbLQVIWD82Rn3HWg7zZD4A4QWk6c-HOkbMmb8EJ6mgrJM5Jc2N72Yt-ptSJKPCWAlegOQ"
    }
}