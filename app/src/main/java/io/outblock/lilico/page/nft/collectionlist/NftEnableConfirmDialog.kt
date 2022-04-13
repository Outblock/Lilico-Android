package io.outblock.lilico.page.nft.collectionlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.outblock.lilico.databinding.DialogAddTokenConfirmBinding
import io.outblock.lilico.manager.config.NftCollection
import io.outblock.lilico.utils.uiScope
import kotlinx.coroutines.delay

class NftEnableConfirmDialog : BottomSheetDialogFragment() {

    private val collection by lazy { arguments?.getParcelable<NftCollection>(EXTRA_TOKEN)!! }

    private lateinit var binding: DialogAddTokenConfirmBinding

    private lateinit var viewModel: NftCollectionListViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogAddTokenConfirmBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[NftCollectionListViewModel::class.java]

        binding.closeButton.setOnClickListener { dismiss() }
        with(binding) {
            tokenNameView.text = collection.name
            Glide.with(iconView).load(collection.logo).circleCrop().into(iconView)
            actionButton.setOnProcessing {
                uiScope {
                    viewModel.addToken(collection)
                    delay(1000)
                    dismiss()
                }
            }
        }
    }

    companion object {
        private const val EXTRA_TOKEN = "extra_token"

        fun show(fragmentManager: FragmentManager, collection: NftCollection) {
            NftEnableConfirmDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(EXTRA_TOKEN, collection)
                }
            }.show(fragmentManager, "")
        }
    }
}