package io.outblock.lilico.page.send.nft.confirm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.outblock.lilico.R
import io.outblock.lilico.databinding.DialogSendConfirmBinding
import io.outblock.lilico.page.send.nft.NftSendModel
import io.outblock.lilico.page.send.nft.confirm.model.NftSendConfirmDialogModel
import io.outblock.lilico.page.send.nft.confirm.presenter.NftSendConfirmPresenter

class NftSendConfirmDialog : BottomSheetDialogFragment() {

    private val nft by lazy { arguments?.getParcelable<NftSendModel>(EXTRA_NFT)!! }

    private lateinit var binding: DialogSendConfirmBinding
    private lateinit var presenter: NftSendConfirmPresenter
    private lateinit var viewModel: NftSendConfirmViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogSendConfirmBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter = NftSendConfirmPresenter(this, binding)
        viewModel = ViewModelProvider(this)[NftSendConfirmViewModel::class.java].apply {
            bindSendModel(this@NftSendConfirmDialog.nft)
            userInfoLiveData.observe(this@NftSendConfirmDialog) { presenter.bind(NftSendConfirmDialogModel(userInfo = it)) }
            resultLiveData.observe(this@NftSendConfirmDialog) { isSuccess ->
                presenter.bind(NftSendConfirmDialogModel(isSendSuccess = isSuccess))
                if (!isSuccess) {
                    Toast.makeText(requireContext(), R.string.common_error_hint, Toast.LENGTH_LONG).show()
                    dismiss()
                }
            }
            load()
        }
        binding.closeButton.setOnClickListener { dismiss() }
    }

    companion object {
        private const val EXTRA_NFT = "extra_nft"

        fun newInstance(nft: NftSendModel): NftSendConfirmDialog {
            return NftSendConfirmDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(EXTRA_NFT, nft)
                }
            }
        }
    }
}