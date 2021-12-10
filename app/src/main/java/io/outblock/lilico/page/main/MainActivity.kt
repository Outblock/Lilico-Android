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

//        testFirebaseAuth()

//        GoogleDriveAuthActivity.launch(this)
    }

    private fun testFirebaseAuth() {
        ioScope {
            firebaseCustomLogin(FIREBASE_TEST_TOKEN) { isSuccessful, exception ->
                if (isSuccessful) {
                    logw(TAG, "firebase uid:${FirebaseAuth.getInstance().currentUser?.uid}")
                } else {
                    loge(TAG, "firebase fetch uid error:${exception}")
                }
            }
        }
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private val FIREBASE_TEST_TOKEN =
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJmaXJlYmFzZS1hZG1pbnNkay02ODR6MEBsaWxpY28tZGV2LmlhbS5nc2VydmljZWFjY291bnQuY29tIiwiYXVkIjoiaHR0cHM6Ly9pZGVudGl0eXRvb2xraXQuZ29vZ2xlYXBpcy5jb20vZ29vZ2xlLmlkZW50aXR5LmlkZW50aXR5dG9vbGtpdC52MS5JZGVudGl0eVRvb2xraXQiLCJleHAiOjE2MzkwMjU3NTMsImlhdCI6MTYzOTAyMjE1Mywic3ViIjoiZmlyZWJhc2UtYWRtaW5zZGstNjg0ejBAbGlsaWNvLWRldi5pYW0uZ3NlcnZpY2VhY2NvdW50LmNvbSIsInVpZCI6IjkwYmQ2ZGY0LTVjYzEtNDY3ZC1iYzk1LTQzZjc3OTUyNGMyZCJ9.wwGJu9VakhGz1J-h5cSFKVdU-9WelKquzBLcGXmwU5rq9JfEn1voeG6pfLQ9380DGPujygjjwKwP6r3j1t6Dc0-qfDuk_HHzY5rdpaSioe8BRYrRk-r09M9xxJVHYKKFJ0-IRqtbYSdy_7SE18IvdlnmUxZWvwhproTiHUKnfJjgnig5LZnktRSfbNWxrTjKbU-dBdANTP65Q4FwHo2N5ijVqeT_3kGQ06wb7QhDxKsh93jjeByIJXn4RlxiIa9-7eDI3N6ccfu4QQjcRb3P3anepcFB7VO5GpkgPG0lNuBcShFpZ3zOEy8jQ8rFu4R-P1dUl8VWvKyVfSSgmXSRFQ"
    }
}