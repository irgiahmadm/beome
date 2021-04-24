package com.beome.ui.authentication.signup

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.beome.databinding.ActivitySignUpBinding
import com.beome.model.User
import com.beome.ui.authentication.login.LoginActivity
import com.beome.utilities.GlobalHelper
import com.beome.utilities.NetworkState
import java.util.*

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySignUpBinding
    private val viewModel: SignupViewModel by lazy {
        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(SignupViewModel::class.java)
    }

    private val c = Calendar.getInstance()
    private val year = c.get(Calendar.YEAR)
    private val month = c.get(Calendar.MONTH)
    private val day = c.get(Calendar.DAY_OF_MONTH)

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        viewModel.setUpRegisterUser()
        getStateRegister()
        binding.buttonSignup.setOnClickListener {
            registerUser()
        }
        binding.textViewSignIn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
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
        GlobalHelper.hideShowPassword(binding.editTextPassword, binding.imageViewTogglePassword)
    }

    private fun registerUser(){
        val username = binding.editTextUsername.text.toString()
        val email = binding.editTextEmail.text.toString()
        val password = binding.editTextPassword.text.toString()
        val birthDate = binding.editTextBirthDate.text.toString()
        val fullname = binding.editTextName.text.toString()
        val authKey = "${GlobalHelper.getRandomString(12)}${System.currentTimeMillis()}"
        val createdAt = Date()
        val updatedAt = Date()

        when {
            username.isNotEmpty() -> {
                binding.editTextUsername.apply {
                    error = "Username can not be empty"
                    requestFocus()
                }
            }
            email.isEmpty() -> {
                binding.editTextEmail.apply {
                    error = "Email can not be empty"
                    requestFocus()
                }
            }
            password.isEmpty() -> {
                binding.editTextPassword.apply {
                    error = "Password can not be empty"
                    requestFocus()
                }
            }
            birthDate.isEmpty() -> {
                binding.editTextBirthDate.apply {
                    error = "Pick your birthdate"
                    requestFocus()
                }
            }
            fullname.isEmpty() -> {
                binding.editTextName.apply {
                    error = "Fullname can not be empty"
                    requestFocus()
                }
            }
            !Patterns.EMAIL_ADDRESS.matcher(binding.editTextEmail.text.toString()).matches() -> {
                binding.editTextEmail.apply {
                    error = "Invalid Email Address"
                    requestFocus()
                }
            }
            username != username.toLowerCase(Locale.ROOT) -> {
                binding.editTextEmail.apply {
                    error = "Username can not contain capital letters"
                    requestFocus()
                }
            }
            username.contains(" ") -> {
                binding.editTextUsername.apply {
                    error = "Username can not contain whitespace"
                    requestFocus()
                }
            }
            else -> {
                val user = User(
                    "",
                    fullname,
                    username,
                    email,
                    GlobalHelper.sha256(password),
                    birthDate,
                    0,
                    0,
                    authKey,
                    1,
                    createdAt,
                    updatedAt
                )
                viewModel.registerUser(user)
                Toast.makeText(this, "Fill all form", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getStateRegister(){
        viewModel.registerState.observe(this,{
            when(it){
                NetworkState.SUCCESS -> {
                    finish()
                }
                NetworkState.LOADING -> {

                }
                NetworkState.FAILED -> {

                }
                NetworkState.NOT_FOUND -> {

                }
                else -> {

                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}