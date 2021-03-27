package com.beome.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.beome.R
import com.beome.adapter.SectionsPagerAdapterSearch
import com.beome.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {

    private lateinit var dashboardViewModel: SearchViewModel
    private lateinit var binding : FragmentSearchBinding
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
                ViewModelProvider(this).get(SearchViewModel::class.java)
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        val sectionPagerAdapter = SectionsPagerAdapterSearch(requireContext(), childFragmentManager)
        binding.viewPager.adapter = sectionPagerAdapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        return binding.root
    }
}