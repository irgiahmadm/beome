package com.beome.ui.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beome.R
import com.beome.databinding.FragmentReportedFeedbackBinding

class ReportedFeedbackFragment : Fragment() {
    private lateinit var binding : FragmentReportedFeedbackBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentReportedFeedbackBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

}