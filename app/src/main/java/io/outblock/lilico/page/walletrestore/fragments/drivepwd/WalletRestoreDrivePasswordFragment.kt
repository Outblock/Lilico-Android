package io.outblock.lilico.page.walletrestore.fragments.drivepwd

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import io.outblock.lilico.R
import io.outblock.lilico.databinding.FragmentWalletRestoreDrivePasswordBinding
import io.outblock.lilico.manager.drive.DriveData
import io.outblock.lilico.manager.drive.DriveItem
import io.outblock.lilico.page.main.MainActivity
import io.outblock.lilico.page.walletrestore.requestWalletRestoreLogin
import io.outblock.lilico.utils.*
import io.outblock.lilico.utils.extensions.res2color
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.utils.listeners.SimpleTextWatcher
import io.outblock.lilico.utils.secret.aesDecrypt
import io.outblock.lilico.utils.secret.aesEncrypt
import io.outblock.lilico.wallet.getMnemonic
import io.outblock.lilico.wallet.getMnemonicAesKey
import kotlinx.coroutines.delay

class WalletRestoreDrivePasswordFragment : Fragment() {

    private lateinit var binding: FragmentWalletRestoreDrivePasswordBinding

    private val data by lazy { arguments?.getParcelable<DriveItem>(EXTRA_DATA)!! }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentWalletRestoreDrivePasswordBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.pwdText.addTextChangedListener(object : SimpleTextWatcher() {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                checkPassword(s.toString())
            }
        })
        binding.nextButton.setOnClickListener { login() }
    }

    private fun checkPassword(pwd: String) {
        val verifyStr = verifyPassword(pwd)
        updateTips(verifyStr.orEmpty())
    }

    private fun updateTips(str: String) {
        with(binding) {
            if (str.isNotEmpty()) {
                stateText.text = str
                stateText.setTextColor(R.color.error.res2color())
                stateIcon.setVisible(true)
            } else {
                nextButton.isEnabled = true
                stateIcon.setVisible(false)
                stateText.setTextColor(R.color.text_sub.res2color())
                stateText.setText(R.string.password_verify_format)
            }
        }
    }

    private fun login() {
        try {
            binding.nextButton.setProgressVisible(true)
            val str = aesDecrypt(binding.pwdText.text.toString(), message = data.data)
            logd("data", str)
            val driveData = Gson().fromJson(str, DriveData::class.java)
            val mnemonic = driveData.data
            ioScope {
                requestWalletRestoreLogin(mnemonic) { isSuccess, _ ->
                    uiScope {
                        if (!isSuccess) {
                            binding.nextButton.setProgressVisible(false)
                            Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show()
                        } else {
                            delay(200)
                            MainActivity.launch(requireContext())
                        }
                    }
                }
            }
        } catch (e: Exception) {
            updateTips(getString(R.string.wrong_password))
            loge(e)
        }
    }

    companion object {
        private const val EXTRA_DATA = "EXTRA_DATA"

        fun instance(argument: DriveItem?): WalletRestoreDrivePasswordFragment {
            return WalletRestoreDrivePasswordFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(EXTRA_DATA, argument)
                }
            }
        }
    }
}