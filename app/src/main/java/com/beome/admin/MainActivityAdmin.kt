package com.beome.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.beome.adapter.SectionsPagerAdapterAdmin
import com.beome.databinding.ActivityMainAdminBinding

class MainActivityAdmin : AppCompatActivity() {
    private lateinit var binding : ActivityMainAdminBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sectionPagerAdapter = SectionsPagerAdapterAdmin(this, supportFragmentManager)
        binding.viewPager.adapter = sectionPagerAdapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }
}