package io.outblock.lilico.manager.drive

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.zackratos.ultimatebarx.ultimatebarx.UltimateBarX
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.logd
import java.util.*


class GoogleDriveAuthActivity : AppCompatActivity() {

    private val password by lazy { intent.getStringExtra(EXTRA_PASSWORD)!! }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(View(this))
        UltimateBarX.with(this).color(Color.TRANSPARENT).fitWindow(false).light(false).applyStatusBar()

        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(DriveScopes.DRIVE_FILE))
            .build()
        val client = GoogleSignIn.getClient(this, signInOptions)

        logd(TAG, "startActivityForResult")
        // The result of the sign-in Intent is handled in onActivityResult.
        startActivityForResult(client.signInIntent, REQUEST_CODE_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        logd(TAG, "onActivityResult")
        if (resultCode != RESULT_OK) {
            return
        }

        if (requestCode == REQUEST_CODE_SIGN_IN) {
            handleSignInResult(data)
        }
        finish()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    private fun handleSignInResult(data: Intent?) {
        data ?: return
        logd(TAG, "handleSignInResult")
        GoogleSignIn.getSignedInAccountFromIntent(data).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val acctount = task.result
                logd(TAG, "Signed in as " + acctount.email)
                // Use the authenticated account to sign in to the Drive service.
                val credential: GoogleAccountCredential = GoogleAccountCredential.usingOAuth2(
                    this, Collections.singleton(DriveScopes.DRIVE_FILE)
                )
                credential.selectedAccount = acctount.account
                val googleDriveService: Drive = Drive.Builder(
                    NetHttpTransport(),
                    JacksonFactory.getDefaultInstance(),
                    credential
                ).setApplicationName("Drive API Migration").build()

                ioScope {
                    uploadMnemonicToGoogleDrive(googleDriveService, password)
                    finish()
                }
            } else {
                logd(TAG, "signIn fail ${task.exception}")
            }
        }
    }

    companion object {
        private val TAG = GoogleDriveAuthActivity::class.java.simpleName
        private const val REQUEST_CODE_SIGN_IN = 1
        private const val EXTRA_PASSWORD = "extra_password"

        fun launch(context: Context, password: String) {
            context.startActivity(Intent(context, GoogleDriveAuthActivity::class.java).apply {
                putExtra(EXTRA_PASSWORD, password)
            })
        }
    }
}