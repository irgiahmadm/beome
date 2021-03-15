package com.beome.ui.authentication.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beome.MainActivity
import com.beome.R
import com.beome.databinding.ActivityLoginBinding
import com.beome.utilities.GlobalHelper
import com.beome.utilities.NetworkState

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    private lateinit var viewModel : LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        val viewModelFactory = LoginViewModelFactory(this)
        viewModel = ViewModelProvider(this, viewModelFactory).get(LoginViewModel(this)::class.java)
        binding.buttonLogin.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser(){
        val email = binding.editTextEmail.text.toString()
        val password = GlobalHelper.sha256(binding.editTextPassword.text.toString())
        if(email.isNotEmpty() || password.isNotEmpty()){
            viewModel.setUpLoginUser()
            viewModel.loginUser(email, password)
            getState()
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
                    startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
                }
                NetworkState.FAILED -> {

                }
                else -> {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
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