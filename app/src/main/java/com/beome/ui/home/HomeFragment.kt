package com.beome.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.beome.R
import com.beome.adapter.SectionsPagerAdapterHome
import com.beome.databinding.FragmentHomeBinding
import com.beome.notify.createChannel

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val sectionPagerAdapter = SectionsPagerAdapterHome(requireContext(), childFragmentManager)
        binding.viewPager.adapter = sectionPagerAdapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        createChannel(requireContext(), R.string.notif_channel_id, R.string.notif_channel_name, R.string.notif_channel_desc)
        return binding.root
    }
}