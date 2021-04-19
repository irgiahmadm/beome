package com.beome.ui.profile

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import androidx.lifecycle.ViewModelProvider
import com.beome.R
import com.beome.constant.ConstantAuth
import com.beome.databinding.ActivityEditProfileBinding
import com.bumptech.glide.Glide
import java.util.*

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding : ActivityEditProfileBinding
    private lateinit var authKey : String
    private val viewModel: ProfileViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(ProfileViewModel::class.java)
    }
    private val c = Calendar.getInstance()
    private val year = c.get(Calendar.YEAR)
    private val month = c.get(Calendar.MONTH)
    private val day = c.get(Calendar.DAY_OF_MONTH)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if(intent.hasExtra(ConstantAuth.CONSTANT_AUTH_KEY)){
            authKey = intent.getStringExtra(ConstantAuth.CONSTANT_AUTH_KEY) as String
        }
        binding.editTextBirthDate.inputType = InputType.TYPE_NULL
        binding.editTextBirthDate.setOnClickListener {
            val dpd = DatePickerDialog(this, { _, year, month, dayOfMonth ->
                val monthStr = if (month.toString().length > 1){
                    month.toString()
                }else{
                    "0${(month+1)}"
                }
                val dayStr = if(dayOfMonth.toString().length > 1){
                    dayOfMonth.toString()
                }else{
                    "0$dayOfMonth"
                }
                binding.editTextBirthDate.setText("$dayStr-$monthStr-$year")
            }, year, month, day)
            dpd.show()
        }
        getUserData()
    }

    private fun getUserData(){
        viewModel.getProfileUser(authKey).observe(this,{
            if (it.photoProfile.isEmpty() || it.photoProfile == "null") {
                Glide.with(this).load(R.drawable.ic_profile)
                    .into(binding.imageViewUser)
            } else {
                Glide.with(this).load(it.photoProfile).circleCrop()
                    .into(binding.imageViewUser)
            }
            binding.editTextUsername.setText(it.username)
            binding.editTextFullname.setText(it.fullName)
            binding.editTextEmail.setText(it.email)
            binding.editTextBirthDate.setText(it.birthDate)
        })
    }
}