package com.beome.ui.profile

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.beome.constant.ConstantAuth
import com.beome.databinding.ActivityChangePasswordBinding
import com.beome.utilities.GlobalHelper
import com.beome.utilities.NetworkState

class ChangePasswordActivity : AppCompatActivity() {
    private val viewModel: ProfileViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(ProfileViewModel::class.java)
    }
    private lateinit var authKey : String
    private lateinit var binding : ActivityChangePasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbarProperties()
        if(intent.hasExtra(ConstantAuth.CONSTANT_AUTH_KEY)){
            authKey = intent.getStringExtra(ConstantAuth.CONSTANT_AUTH_KEY) as String
        }
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Change Password"
        initUI()
        GlobalHelper.hideShowPassword(binding.editTextNewPassword, binding.imageViewToggleNewPassword)
        GlobalHelper.hideShowPassword(binding.editTextOldPassword, binding.imageViewToggleOldPassword)
    }

    private fun initUI(){
        viewModel.setUpChangePassword()
        viewModel.changePasswordState.observe(this, {
            when(it){
                NetworkState.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                NetworkState.SUCCESS -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Your password has been changed", Toast.LENGTH_SHORT).show()
                    finish()
                }
                NetworkState.FAILED -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Failed, something went wrong", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
            }
        })
        viewModel.getOldPasswordState.observe(this,{
            when(it){
                NetworkState.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                NetworkState.SUCCESS -> {
                    binding.progressBar.visibility = View.GONE
                    viewModel.changePassword(binding.editTextNewPassword.text.toString(), authKey)
                }
                NetworkState.NOT_FOUND -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Old password is not matching with our data", Toast.LENGTH_SHORT).show()
                }
                NetworkState.FAILED -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Failed, something went wrong", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
            }
        })
        binding.buttonSubmit.setOnClickListener {
            val oldPassword = binding.editTextOldPassword.text.toString()
            val newPassword = binding.editTextNewPassword.text.toString()

            when {
                oldPassword.isEmpty() -> {
                    binding.editTextOldPassword.apply {
                        error = "Old password can not be empty"
                        requestFocus()
                    }
                }
                oldPassword.length < 6 -> {
                    binding.editTextOldPassword.apply {
                        error = "Password must be longer than 6 characters"
                        requestFocus()
                    }
                }
                oldPassword.length > 32 -> {
                    binding.editTextOldPassword.apply {
                        error = "Password characters up to 32 characters"
                        requestFocus()
                    }
                }
                newPassword.isEmpty() -> {
                    binding.editTextNewPassword.apply {
                        error = "New password can not be empty"
                        requestFocus()
                    }
                }
                newPassword.length < 6 -> {
                    binding.editTextNewPassword.apply {
                        error = "Password must be longer than 6 characters"
                        requestFocus()
                    }
                }
                newPassword.length > 32 -> {
                    binding.editTextNewPassword.apply {
                        error = "Password characters up to 32 characters"
                        requestFocus()
                    }
                }
                oldPassword == newPassword -> {
                    Toast.makeText(this, "Old password can not be same with new password", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    viewModel.getOldPassword(oldPassword, authKey)
                }
            }
        }

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setToolbarProperties() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> finish()
        }
        return true
    }
}