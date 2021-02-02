package com.beome.ui.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.beome.R
import com.beome.databinding.ActivityProfilePreviewBinding
import com.bumptech.glide.Glide

class ProfilePreviewActivity : AppCompatActivity() {
    private lateinit var binding : ActivityProfilePreviewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilePreviewBinding.inflate(layoutInflater)
        Glide.with(this).load(R.drawable.auth_image).circleCrop().into(binding.imageViewUserProfile)
        setContentView(binding.root)
    }
}