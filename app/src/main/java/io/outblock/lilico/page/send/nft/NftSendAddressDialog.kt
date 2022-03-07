package io.outblock.lilico.page.send.nft

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.outblock.lilico.R
import io.outblock.lilico.databinding.DialogSendNftAddressBinding
import io.outblock.lilico.network.model.Nft
import io.outblock.lilico.page.address.AddressBookFragment
import io.outblock.lilico.page.send.transaction.TransactionSendViewModel
import io.outblock.lilico.page.send.transaction.presenter.TransactionSendPresenter

class NftSendAddressDialog : BottomSheetDialogFragment() {
    private val nft by lazy { arguments?.getParcelable<Nft>(EXTRA_NFT)!! }

    private lateinit var binding: DialogSendNftAddressBinding
    private lateinit var presenter: TransactionSendPresenter
    private lateinit var viewModel: TransactionSendViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogSendNftAddressBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        childFragmentManager.beginTransaction().replace(R.id.search_container, AddressBookFragment()).commit()

        presenter = TransactionSendPresenter(childFragmentManager, binding.addressContent)
        viewModel = ViewModelProvider(requireActivity())[TransactionSendViewModel::class.java].apply {

        }
        binding.closeButton.setOnClickListener { dismiss() }
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