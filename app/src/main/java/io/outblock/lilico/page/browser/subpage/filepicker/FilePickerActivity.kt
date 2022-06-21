package io.outblock.lilico.page.browser.subpage.filepicker

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebChromeClient
import androidx.activity.result.contract.ActivityResultContracts
import io.outblock.lilico.base.activity.BaseActivity

class FilePickerActivity : BaseActivity() {

    private val picker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        onWebviewFilePicked(uri)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val param = webviewFileChooserParams()

        if (param == null) {
            onWebviewFilePicked(null)
            finish()
            return
        }

        listOf(FilePickerActivity::class.java).forEach { it }

        picker.launch(param.acceptType())
    }

    private fun WebChromeClient.FileChooserParams.acceptType(): String {
        return acceptTypes.getOrElse(0) { "*/*" }.split("/").first() + "/*"
    }

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, FilePickerActivity::class.java))
        }
    }
}