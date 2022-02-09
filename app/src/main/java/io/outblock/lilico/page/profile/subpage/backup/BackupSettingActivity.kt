package io.outblock.lilico.page.profile.subpage.backup

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.databinding.ActivityBackupSettingBinding
import io.outblock.lilico.manager.drive.ACTION_GOOGLE_DRIVE_DELETE_FINISH
import io.outblock.lilico.manager.drive.EXTRA_SUCCESS
import io.outblock.lilico.page.security.recovery.SecurityRecoveryActivity
import io.outblock.lilico.page.security.recovery.SecurityRecoveryActivity.Companion.TYPE_PHRASES
import io.outblock.lilico.utils.*
import io.outblock.lilico.widgets.ProgressDialog
import kotlinx.coroutines.delay

class BackupSettingActivity : BaseActivity() {

    private lateinit var binding: ActivityBackupSettingBinding

    private val driveDeleteReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            val isSuccess = intent?.getBooleanExtra(EXTRA_SUCCESS, false) ?: return
            onUploadCallback(isSuccess)
        }
    }

    private val progressDialog by lazy { ProgressDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBackupSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        with(binding) {
            drivePreference.setOnClickListener {
                uiScope {
                    if (isBackupGoogleDrive()) {
                        GoogleDriveDeleteDialog(this@BackupSettingActivity) {
                            progressDialog.show()
                        }.show()
                    } else {
                        BackupGoogleDriveActivity.launch(this@BackupSettingActivity)
                    }
                }
            }
            manuallyPreference.setOnClickListener {
                SecurityRecoveryActivity.launch(this@BackupSettingActivity, TYPE_PHRASES)
                setBackupManually()
            }
        }

        LocalBroadcastManager.getInstance(Env.getApp()).registerReceiver(driveDeleteReceiver, IntentFilter(ACTION_GOOGLE_DRIVE_DELETE_FINISH))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        updateState()
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(Env.getApp()).unregisterReceiver(driveDeleteReceiver)
        super.onDestroy()
    }

    private fun onUploadCallback(isSuccess: Boolean) {
        progressDialog.dismiss()
        if (isSuccess) {
            setBackupGoogleDrive(false)
            ioScope {
                delay(300)
                updateState()
            }
        } else {
            Toast.makeText(this, "Auth error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateState() {
        uiScope {
            with(binding) {
                drivePreference.setStateVisible(isBackupGoogleDrive())
                manuallyPreference.setStateVisible(isBackupManually())
            }
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, BackupSettingActivity::class.java))
        }
    }
}