package io.outblock.lilico.page.main

import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import io.outblock.lilico.R
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.firebase.auth.firebaseCustomLogin
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.loge
import io.outblock.lilico.utils.logw
class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        testFirebaseAuth()
    }

    private fun testFirebaseAuth() {
        ioScope {
            firebaseCustomLogin(FIREBASE_TEST_TOKEN) { isSuccessful, exception ->
                if (isSuccessful) {
                    logw(TAG, "firebase anonymously id:${FirebaseAuth.getInstance().currentUser?.uid}")
                } else {
                    loge(TAG, "firebase fetch anonymously id error:${exception}")
                }
            }
        }
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private val FIREBASE_TEST_TOKEN =
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJmaXJlYmFzZS1hZG1pbnNkay02ODR6MEBsaWxpY28tZGV2LmlhbS5nc2VydmljZWFjY291bnQuY29tIiwiYXVkIjoiaHR0cHM6Ly9pZGVudGl0eXRvb2xraXQuZ29vZ2xlYXBpcy5jb20vZ29vZ2xlLmlkZW50aXR5LmlkZW50aXR5dG9vbGtpdC52MS5JZGVudGl0eVRvb2xraXQiLCJleHAiOjE2Mzg5Njg5MjIsImlhdCI6MTYzODk2NTMyMiwic3ViIjoiZmlyZWJhc2UtYWRtaW5zZGstNjg0ejBAbGlsaWNvLWRldi5pYW0uZ3NlcnZpY2VhY2NvdW50LmNvbSIsInVpZCI6InNvbWUtdWlkIn0.oVZTKMiIK9d2UWU07xuWOiXFD-D4Da1Ua65HcgvpDk6CqLgkXhvNfXph5uGeoxNQZkSPnOox-7_uzgjDkvRjKXOU09lSPunanfNNhJJphLOhKJ2E-NFQgvpPJO_fGsly0JNKCFsiFXyaxcNHU3BR3yFDNOtwxqSm0vVTzagzy8_p-T_qibzYHkQ-Ug_pp7JQbIuafEoBOzLSqB0WDRN0v7m_Qasbl8utobbvezzhesWXanD4l76hJBx1qQXW4ftdt-NmL0eWtAfB2OrmTieMs7_qQhxts3Q_j5fjXrfPGSR86bgjQcm4sFoeWt350uQLLZ1oXMTwZKlcUOH4PTev1A"
    }
}