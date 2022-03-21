package io.outblock.lilico.page.addtoken

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.outblock.lilico.databinding.DialogAddTokenConfirmBinding
import io.outblock.lilico.manager.coin.FlowCoin
import io.outblock.lilico.utils.uiScope
import kotlinx.coroutines.delay

class AddTokenConfirmDialog : BottomSheetDialogFragment() {

    private val coin by lazy { arguments?.getParcelable<FlowCoin>(EXTRA_TOKEN)!! }

    private lateinit var binding: DialogAddTokenConfirmBinding

    private lateinit var viewModel: AddTokenViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogAddTokenConfirmBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[AddTokenViewModel::class.java]

        binding.closeButton.setOnClickListener { dismiss() }
        with(binding) {
            tokenNameView.text = coin.name
            Glide.with(iconView).load(coin.icon).into(iconView)
            actionButton.setOnProcessing {
                uiScope {
                    viewModel.addToken(coin)
                    delay(1000)
                    dismiss()
                }
            }
        }
    }

    companion object {
        private const val EXTRA_TOKEN = "extra_token"

        fun show(fragmentManager: FragmentManager, coin: FlowCoin) {
            AddTokenConfirmDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(EXTRA_TOKEN, coin)
                }
            }.show(fragmentManager, "")
        }
    }
}