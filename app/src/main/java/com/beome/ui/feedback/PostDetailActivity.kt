package com.beome.ui.feedback

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.beome.R
import com.beome.databinding.ActivityPostDetailBinding
import com.beome.databinding.ActivityReportBinding

class PostDetailActivity : AppCompatActivity() {
    private lateinit var binding : ActivityPostDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}