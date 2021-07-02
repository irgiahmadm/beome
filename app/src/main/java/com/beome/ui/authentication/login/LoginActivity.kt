package com.beome.ui.authentication.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.beome.MainActivity
import com.beome.constant.ConstantAuth
import com.beome.databinding.ActivityLoginBinding
import com.beome.ui.admin.MainActivityAdmin
import com.beome.ui.authentication.signup.SignUpActivity
import com.beome.utilities.GlobalHelper
import com.beome.utilities.NetworkState
import com.beome.utilities.SharedPrefUtil

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    private lateinit var viewModel : LoginViewModel
    private lateinit var sharedPrefUtil: SharedPrefUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val viewModelFactory = LoginViewModelFactory(this)
        viewModel = ViewModelProvider(this, viewModelFactory).get(LoginViewModel(this)::class.java)
        sharedPrefUtil = SharedPrefUtil()
        sharedPrefUtil.start(this,ConstantAuth.CONSTANT_PREFERENCE)
        viewModel.setUpLoginUser()
        getState()
        binding.buttonLogin.setOnClickListener {
            loginUser()
        }
        binding.textViewRegiter.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }
        GlobalHelper.hideShowPassword(binding.editTextPassword, binding.imageViewTogglePassword)
    }

    private fun loginUser(){
        val email = binding.editTextEmail.text.toString()
        val password = GlobalHelper.sha256(binding.editTextPassword.text.toString())
        if(email.isNotEmpty() || password.isNotEmpty()){
            viewModel.loginUser(email, password)
        }else{
            Toast.makeText(this,"Fill all form", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getState(){
        viewModel.loginState.observe(this,{
            Log.d("login_state", it.toString())
            when(it){
                NetworkState.LOADING -> {

                }
                NetworkState.SUCCESS -> {
                    finish()
                    //check status
                    if(sharedPrefUtil.get(ConstantAuth.CONSTANT_AUTH_STATUS)?.toInt() == 1){
                        Log.d("loginStatus", sharedPrefUtil.get(ConstantAuth.CONSTANT_AUTH_STATUS).toString())
                        if(sharedPrefUtil.get(ConstantAuth.CONSTANT_AUTH_ROLE)?.toInt() == 2){
                            startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                        }else{
                            startActivity(Intent(this, MainActivityAdmin::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                        }
                    }else{
                        Toast.makeText(this, "Your account is banned by admin", Toast.LENGTH_LONG).show()
                    }

                }
                NetworkState.FAILED -> {

                }
                NetworkState.NOT_FOUND ->{
                    Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
            }

        })
    }

}