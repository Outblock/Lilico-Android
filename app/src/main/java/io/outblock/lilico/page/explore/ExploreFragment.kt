package io.outblock.lilico.page.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.journeyapps.barcodescanner.ScanOptions
import io.outblock.lilico.databinding.FragmentExploreBinding
import io.outblock.lilico.page.explore.model.ExploreModel
import io.outblock.lilico.page.explore.presenter.ExplorePresenter
import io.outblock.lilico.page.profile.subpage.claimdomain.checkMeowDomainClaimed
import io.outblock.lilico.page.scan.dispatchScanResult
import io.outblock.lilico.utils.launch
import io.outblock.lilico.utils.registerBarcodeLauncher

class ExploreFragment : Fragment() {

    private lateinit var binding: FragmentExploreBinding
    private lateinit var presenter: ExplorePresenter
    private lateinit var viewModel: ExploreViewModel

    private lateinit var barcodeLauncher: ActivityResultLauncher<ScanOptions>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        barcodeLauncher = registerBarcodeLauncher { result -> dispatchScanResult(requireContext(), result.orEmpty()) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentExploreBinding.inflate(inflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        checkMeowDomainClaimed()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.scanButton.setOnClickListener { barcodeLauncher.launch() }
        presenter = ExplorePresenter(this, binding)
        viewModel = ViewModelProvider(requireActivity())[ExploreViewModel::class.java].apply {
            bindActivity(requireActivity())
            recentLiveData.observe(viewLifecycleOwner) { presenter.bind(ExploreModel(recentList = it)) }
            bookmarkLiveData.observe(viewLifecycleOwner) { presenter.bind(ExploreModel(bookmarkList = it)) }
            dAppsLiveData.observe(viewLifecycleOwner) { presenter.bind(ExploreModel(dAppList = it)) }
            load()
        }
    }
}