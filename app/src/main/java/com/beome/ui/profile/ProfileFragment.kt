package com.beome.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beome.R
import com.beome.databinding.FragmentProfileBinding
import com.bumptech.glide.Glide

class ProfileFragment : Fragment() {

    private lateinit var binding : FragmentProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        Glide.with(this).load(R.drawable.auth_image).circleCrop().into(binding.imageViewUserProfile)
        binding.textViewFollowerCount.text = getString(R.string.follower_count, 0)
        binding.textViewPostCount.text = getString(R.string.post_count, 0)
        return binding.root
    }

}