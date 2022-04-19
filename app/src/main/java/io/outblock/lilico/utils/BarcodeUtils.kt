package io.outblock.lilico.utils

import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import io.outblock.lilico.page.scan.ScanBarcodeActivity


fun Fragment.registerBarcodeLauncher(callback: (result: String?) -> Unit): ActivityResultLauncher<ScanOptions> {
    return registerForActivityResult(ScanContract()) { result -> callback.invoke(result.contents) }
}

fun FragmentActivity.registerBarcodeLauncher(callback: (result: String?) -> Unit): ActivityResultLauncher<ScanOptions> {
    return registerForActivityResult(ScanContract()) { result -> callback.invoke(result.contents) }
}

fun ActivityResultLauncher<ScanOptions>.launch() {
    launch(ScanOptions().apply {
        setOrientationLocked(true)
        setBeepEnabled(false)
        captureActivity = ScanBarcodeActivity::class.java
    })
}