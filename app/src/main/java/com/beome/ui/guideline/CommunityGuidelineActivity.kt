package com.beome.ui.guideline

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.beome.databinding.ActivityCommunityGuidelineBinding

class CommunityGuidelineActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCommunityGuidelineBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommunityGuidelineBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Community Guideline"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            finish()
        }
        return true
    }
}