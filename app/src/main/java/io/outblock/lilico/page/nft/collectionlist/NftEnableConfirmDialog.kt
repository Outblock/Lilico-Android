package io.outblock.lilico.page.nft.collectionlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.outblock.lilico.databinding.DialogAddCollectionConfirmBinding
import io.outblock.lilico.manager.config.NftCollection
import io.outblock.lilico.utils.uiScope
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.coroutines.delay

class NftEnableConfirmDialog : BottomSheetDialogFragment() {

    private val collection by lazy { arguments?.getParcelable<NftCollection>(EXTRA_TOKEN)!! }

    private lateinit var binding: DialogAddCollectionConfirmBinding

    private lateinit var viewModel: NftCollectionListViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogAddCollectionConfirmBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[NftCollectionListViewModel::class.java]

        binding.closeButton.setOnClickListener { dismiss() }
        with(binding) {
            nameView.text = collection.name
            descView.text = collection.description
            Glide.with(coverView).load(collection.banner).transform(BlurTransformation(2, 5)).into(coverView)
            actionButton.setOnClickListener {
                uiScope {
                    actionButton.setProgressVisible(true)
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