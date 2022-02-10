package io.outblock.lilico.page.profile.subpage.nickname

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import io.outblock.lilico.R
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.databinding.ActivityEditNicknameBinding
import io.outblock.lilico.network.model.UserInfoData
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.widgets.ProgressDialog


class EditNicknameActivity : BaseActivity() {

    private lateinit var binding: ActivityEditNicknameBinding

    private lateinit var viewModel: EditNicknameViewModel

    private val progressDialog by lazy { ProgressDialog(this) }

    private var userInfo: UserInfoData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditNicknameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()

        viewModel = ViewModelProvider(this)[EditNicknameViewModel::class.java].apply {
            userInfoLiveData.observe(this@EditNicknameActivity) { updateUserInfo(it) }
            resultLiveData.observe(this@EditNicknameActivity) { onSaveResult(it) }
            load()
        }

        binding.saveButton.setOnClickListener {
            progressDialog.show()
            hideKeyboard()
            viewModel.save(binding.editText.text.toString())
        }
        binding.editText.doOnTextChanged { text, _, _, _ ->
            binding.saveButton.isEnabled = !(text.isNullOrEmpty() || text.toString() == userInfo?.nickname)
        }
    }

    private fun onSaveResult(isSuccess: Boolean) {
        progressDialog.dismiss()
        if (isSuccess) {
            finish()
        } else {
            Toast.makeText(this, "Save fail", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUserInfo(userInfo: UserInfoData) {
        this.userInfo = userInfo
        binding.editText.setText(userInfo.nickname)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.editText.windowToken, 0)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        title = R.string.edit_nickname.res2String()
    }

    companion object {
        private const val EXTRA_TYPE = "EXTRA_TYPE"

        fun launch(context: Context) {
            context.startActivity(Intent(context, EditNicknameActivity::class.java))
        }
    }
}