package io.outblock.lilico.page.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import io.outblock.lilico.databinding.FragmentProfileBinding
import io.outblock.lilico.page.profile.model.ProfileFragmentModel
import io.outblock.lilico.page.profile.presenter.ProfileFragmentPresenter

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var presenter: ProfileFragmentPresenter
    private lateinit var viewModel: ProfileFragmentViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter = ProfileFragmentPresenter(this, binding)
        viewModel = ViewModelProvider(this)[ProfileFragmentViewModel::class.java].apply {
            profileLiveData.observe(viewLifecycleOwner) { presenter.bind(ProfileFragmentModel(userInfo = it)) }
            load()
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.bind(ProfileFragmentModel(onResume = true))
    }
}