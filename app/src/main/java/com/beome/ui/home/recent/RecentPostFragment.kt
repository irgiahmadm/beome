package com.beome.ui.home.recent

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beome.R
import com.beome.databinding.FragmentRecentPostBinding
import com.bumptech.glide.Glide

class RecentPostFragment : Fragment() {

    private lateinit var binding : FragmentRecentPostBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRecentPostBinding.inflate(layoutInflater, container, false)
        Glide.with(this).load(R.drawable.ic_empty_view_post).into(binding.emptyViewRecentPost)
        return binding.root
    }

}