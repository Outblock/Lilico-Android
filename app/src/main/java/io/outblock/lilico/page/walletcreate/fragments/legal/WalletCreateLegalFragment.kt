package io.outblock.lilico.page.walletcreate.fragments.legal

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import io.outblock.lilico.R
import io.outblock.lilico.databinding.FragmentWalletCreateLegalBinding
import io.outblock.lilico.page.walletcreate.WalletCreateViewModel
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.extensions.res2color

class WalletCreateLegalFragment : Fragment() {

    private lateinit var binding: FragmentWalletCreateLegalBinding

    private val pageViewModel by lazy { ViewModelProvider(requireActivity())[WalletCreateViewModel::class.java] }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentWalletCreateLegalBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {
            title1.text = SpannableString(R.string.legal_information.res2String()).apply {
                val protection = R.string.legal.res2String()
                val index = indexOf(protection)
                setSpan(ForegroundColorSpan(R.color.colorSecondary.res2color()), index, index + protection.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            termsButton.setOnClickListener { openWebUrl("https://lilico.app/terms-of-conditions.html") }
            privacyButton.setOnClickListener { openWebUrl("https://lilico.app/privacy-policy.html") }

            nextButton.setOnClickListener { pageViewModel.nextStep() }
        }
    }

    private fun openWebUrl(url: String) {
        requireActivity().startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }
}