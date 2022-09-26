package io.outblock.lilico.page.swap.dialog.confirm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.outblock.lilico.R
import io.outblock.lilico.databinding.DialogSwapTokenConfirmBinding
import io.outblock.lilico.page.swap.SwapViewModel
import io.outblock.lilico.page.swap.fromAmount
import io.outblock.lilico.page.swap.swapPageBinding
import io.outblock.lilico.page.swap.toAmount
import io.outblock.lilico.utils.formatPrice

class SwapTokenConfirmDialog : BottomSheetDialogFragment() {

    private lateinit var binding: DialogSwapTokenConfirmBinding

    private val viewModel by lazy { ViewModelProvider(requireActivity())[SwapViewModel::class.java] }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogSwapTokenConfirmBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.swapTransactionStateLiveData.observe(viewLifecycleOwner) {
            if (it) requireActivity().finish() else if (isResumed) {
                dismiss()
            }
        }
        binding.bindHeader()
        binding.bindEstimate()
        binding.sendButton.setOnProcessing { viewModel.swap() }
    }

    private fun DialogSwapTokenConfirmBinding.bindHeader() {
        val fromCoin = viewModel.fromCoin() ?: return
        val toCoin = viewModel.toCoin() ?: return
        Glide.with(fromAvatarView).load(fromCoin.icon).into(fromAvatarView)
        Glide.with(toAvatarView).load(toCoin.icon).into(toAvatarView)

        fromNameView.text = fromCoin.symbol.uppercase()
        toNameView.text = fromCoin.symbol.uppercase()

        val pageBinding = swapPageBinding() ?: return
        fromAddressView.text = "${pageBinding.fromAmount().formatPrice()} ${fromCoin.symbol.uppercase()}"
        toAddressView.text = "${pageBinding.toAmount().formatPrice()} ${toCoin.symbol.uppercase()}"
    }

    private fun DialogSwapTokenConfirmBinding.bindEstimate() {
        val data = viewModel.estimateLiveData.value ?: return
        val amountIn = data.routes.firstOrNull()?.routeAmountIn ?: return
        val amountOut = data.routes.firstOrNull()?.routeAmountOut ?: return
        val fromCoin = viewModel.fromCoin() ?: return
        val toCoin = viewModel.toCoin() ?: return

        bestPriceView.text = "1 ${fromCoin.symbol.uppercase()} ≈ ${(amountOut / amountIn).formatPrice()} ${toCoin.symbol.uppercase()}"

        providerIconView.setImageResource(R.drawable.ic_increment_fi)
        providerView.text = "Increment.fi"

        priceImpactView.text = data.priceImpact.formatPrice(4)

        estimatedFeesView.text = data.priceImpact.formatPrice(4)
    }


    companion object {
        private const val EXTRA_DATA = "extra_data"

        fun show(fragmentManager: FragmentManager) {
            SwapTokenConfirmDialog().apply {
            }.show(fragmentManager, "")
        }
    }
}