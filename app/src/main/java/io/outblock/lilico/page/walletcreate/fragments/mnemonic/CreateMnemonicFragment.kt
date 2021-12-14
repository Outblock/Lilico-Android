package io.outblock.lilico.page.walletcreate.fragments.mnemonic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import io.outblock.lilico.databinding.FragmentCreateMnemonicBinding
import io.outblock.lilico.utils.extensions.dp2px
import io.outblock.lilico.widgets.itemdecoration.GridSpaceItemDecoration

class CreateMnemonicFragment : Fragment() {

    private lateinit var binding: FragmentCreateMnemonicBinding

    private lateinit var viewModel: CreateMnemonicViewModel

    private val adapter = MnemonicAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCreateMnemonicBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding.mnemonicContainer) {
            adapter = this@CreateMnemonicFragment.adapter
            layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            addItemDecoration(GridSpaceItemDecoration(horizontal = 6.dp2px().toDouble(), vertical = 8.dp2px().toDouble()))
        }

        viewModel = ViewModelProvider(this)[CreateMnemonicViewModel::class.java].apply {
            mnemonicList.observe(viewLifecycleOwner, { adapter.setNewDiffData(it) })
            loadMnemonic()
        }
    }
}