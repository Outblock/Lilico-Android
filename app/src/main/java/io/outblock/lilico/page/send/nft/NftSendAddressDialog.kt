package io.outblock.lilico.page.send.nft

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.journeyapps.barcodescanner.ScanOptions
import io.outblock.lilico.R
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.databinding.DialogSendNftAddressBinding
import io.outblock.lilico.network.model.AddressBookContact
import io.outblock.lilico.network.model.Nft
import io.outblock.lilico.page.address.AddressBookFragment
import io.outblock.lilico.page.address.AddressBookViewModel
import io.outblock.lilico.page.send.nft.confirm.NftSendConfirmDialog
import io.outblock.lilico.page.send.transaction.SelectSendAddressViewModel
import io.outblock.lilico.page.send.transaction.model.TransactionSendModel
import io.outblock.lilico.page.send.transaction.presenter.TransactionSendPresenter
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.launch
import io.outblock.lilico.utils.registerBarcodeLauncher
import io.outblock.lilico.utils.uiScope

class NftSendAddressDialog : BottomSheetDialogFragment() {
    private val nft by lazy { arguments?.getParcelable<Nft>(EXTRA_NFT)!! }

    private lateinit var binding: DialogSendNftAddressBinding
    private lateinit var presenter: TransactionSendPresenter
    private lateinit var viewModel: SelectSendAddressViewModel
    private lateinit var barcodeLauncher: ActivityResultLauncher<ScanOptions>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        barcodeLauncher = registerBarcodeLauncher { presenter.bind(TransactionSendModel(qrcode = it)) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogSendNftAddressBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        childFragmentManager.beginTransaction().replace(R.id.search_container, AddressBookFragment()).commit()

        presenter = TransactionSendPresenter(childFragmentManager, binding.addressContent)
        viewModel = ViewModelProvider(requireActivity())[SelectSendAddressViewModel::class.java].apply {
            onAddressSelectedLiveData.observe(viewLifecycleOwner) { onAddressSelected(it) }
        }
        ViewModelProvider(requireActivity())[AddressBookViewModel::class.java].apply {
            clearEditTextFocusLiveData.observe(this@NftSendAddressDialog) { presenter.bind(TransactionSendModel(isClearInputFocus = it)) }
        }
        binding.closeButton.setOnClickListener { dismiss() }
        binding.scanButton.setOnClickListener { barcodeLauncher.launch() }
    }

    private fun onAddressSelected(contact: AddressBookContact?) {
        contact ?: return
        ioScope {
            val wallet = walletCache().read() ?: return@ioScope
            uiScope {
                val activity = requireActivity()
                dismiss()

                NftSendConfirmDialog.newInstance(
                    NftSendModel(
                        nft = nft,
                        target = contact,
                        fromAddress = wallet.wallets?.first()?.blockchain?.first()?.address.orEmpty(),
                    )
                ).show(activity.supportFragmentManager, "")
            }
        }
    }

    companion object {
        private const val EXTRA_NFT = "extra_nft"

        fun newInstance(nft: Nft): NftSendAddressDialog {
            return NftSendAddressDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(EXTRA_NFT, nft)
                }
            }
        }
    }
}