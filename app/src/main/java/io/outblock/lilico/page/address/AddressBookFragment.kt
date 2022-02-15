package io.outblock.lilico.page.address

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import io.outblock.lilico.databinding.FragmentAddressBookBinding
import io.outblock.lilico.page.address.model.AddressBookFragmentModel
import io.outblock.lilico.page.address.presenter.AddressBookFragmentPresenter

class AddressBookFragment : Fragment() {
    private lateinit var binding: FragmentAddressBookBinding
    private lateinit var viewModel: AddressBookViewModel
    private lateinit var presenter: AddressBookFragmentPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAddressBookBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter = AddressBookFragmentPresenter(this, binding)
        viewModel = ViewModelProvider(requireActivity())[AddressBookViewModel::class.java].apply {
            addressBookLiveData.observe(viewLifecycleOwner) { presenter.bind(AddressBookFragmentModel(data = it)) }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadAddressBook()
    }
}