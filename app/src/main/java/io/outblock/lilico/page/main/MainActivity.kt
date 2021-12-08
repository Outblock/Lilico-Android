package io.outblock.lilico.page.main

import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import io.outblock.lilico.R
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.utils.ioScope

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        testFirebaseAuth()
    }

    private fun testFirebaseAuth() {
        ioScope {
            if (FirebaseAuth.getInstance().currentUser == null) {
                FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.w(TAG, "firebase anonymously id:${FirebaseAuth.getInstance().currentUser?.uid}")
                    } else {
                        Log.e(TAG, "firebase fetch anonymously id error:${it.exception}")
                    }
                }
            } else {
                Log.w(TAG, "firebase anonymously id:${FirebaseAuth.getInstance().currentUser?.uid}")
            }
        }
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}