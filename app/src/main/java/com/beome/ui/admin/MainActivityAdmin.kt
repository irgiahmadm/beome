package com.beome.ui.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import com.beome.R
import com.beome.adapter.SectionsPagerAdapterAdmin
import com.beome.constant.ConstantAuth
import com.beome.databinding.ActivityMainAdminBinding
import com.beome.ui.authentication.login.LoginActivity
import com.beome.utilities.SharedPrefUtil

class MainActivityAdmin : AppCompatActivity() {
    private lateinit var sharedPrefUtil: SharedPrefUtil
    private lateinit var binding : ActivityMainAdminBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPrefUtil = SharedPrefUtil()
        sharedPrefUtil.start(this, ConstantAuth.CONSTANT_PREFERENCE)
        val sectionPagerAdapter = SectionsPagerAdapterAdmin(this, supportFragmentManager)
        binding.viewPager.adapter = sectionPagerAdapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.logout_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_logout){
            showConfirmLogoutDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showConfirmLogoutDialog() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            setCancelable(true)
            setTitle(getString(R.string.logout_confirmation))
            setMessage(getString(R.string.logout_message))
            setPositiveButton(
                getString(R.string.logout)
            ) { _, _ ->
                sharedPrefUtil.clear()
                finish()
                startActivity(
                    Intent(this@MainActivityAdmin, LoginActivity::class.java).addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    )
                )
            }
            setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
        }
        alertDialog.show()
    }
}