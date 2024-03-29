package com.beome.ui.authentication.signup

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.beome.MainActivity
import com.beome.constant.ConstantAuth
import com.beome.databinding.ActivitySignUpBinding
import com.beome.model.User
import com.beome.ui.admin.MainActivityAdmin
import com.beome.ui.authentication.login.LoginActivity
import com.beome.ui.authentication.login.LoginViewModel
import com.beome.ui.authentication.login.LoginViewModelFactory
import com.beome.ui.guideline.CommunityGuidelineActivity
import com.beome.utilities.GlobalHelper
import com.beome.utilities.NetworkState
import com.beome.utilities.SharedPrefUtil
import java.util.*

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySignUpBinding
    private val viewModel: SignupViewModel by lazy {
        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(SignupViewModel::class.java)
    }
    private lateinit var viewModelLogin : LoginViewModel
    private lateinit var sharedPrefUtil: SharedPrefUtil
    private lateinit var user : User

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val viewModelFactory = LoginViewModelFactory(this)
        viewModelLogin = ViewModelProvider(this, viewModelFactory).get(LoginViewModel(this)::class.java)
        sharedPrefUtil = SharedPrefUtil()
        sharedPrefUtil.start(this,ConstantAuth.CONSTANT_PREFERENCE)
        viewModel.setUpRepoRegister()
        viewModel.setUpRegisterUser()
        viewModel.setupIsUsernameExist()
        viewModel.setupIsEmailExist()
        viewModelLogin.setUpLoginUser()

        getStateRegister()
        getStateLogin()
        getStateEmailExist()
        getStateUsernameExist()
        binding.buttonSignup.setOnClickListener {
            registerUser()
        }
        binding.textViewSignIn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        GlobalHelper.hideShowPassword(binding.editTextPassword, binding.imageViewTogglePassword)
        binding.textViewCommunityGuideline.setOnClickListener {
            startActivity(Intent(this, CommunityGuidelineActivity::class.java))
        }
    }

    private fun registerUser(){
        val username = binding.editTextUsername.text.toString()
        val email = binding.editTextEmail.text.toString()
        val password = binding.editTextPassword.text.toString()
        val fullname = binding.editTextName.text.toString()

        when {
            username.isEmpty() -> {
                binding.editTextUsername.apply {
                    error = "Username can not be empty"
                    requestFocus()
                }
            }
            username.length < 6 -> {
                binding.editTextUsername.apply {
                    error = "Username must be longer than 6 characters"
                    requestFocus()
                }
            }
            username.length > 16 -> {
                binding.editTextUsername.apply {
                    error = "Username characters up to 16 characters"
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
            password.length < 6 -> {
                binding.editTextPassword.apply {
                    error = "Password must be longer than 6 characters"
                    requestFocus()
                }
            }
            password.length > 32 -> {
                binding.editTextPassword.apply {
                    error = "Password characters up to 32 characters"
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
                viewModel.isUsernameExist(username.toLowerCase(Locale.getDefault()))
            }
        }
    }

    private fun getStateUsernameExist(){
        viewModel.isUsernameExist.observe(this, {
            if (it) {
                binding.editTextUsername.apply {
                    error = "Username is used"
                    requestFocus()
                }
            } else {
                val email = binding.editTextEmail.text.toString()
                viewModel.isEmailExist(email)
            }
        })
    }

    private fun getStateEmailExist(){
        viewModel.isEmailExist.observe(this,{
            if(it){
                binding.editTextEmail.apply {
                    error = "Email is used"
                    requestFocus()
                }
            }else{
                val username = binding.editTextUsername.text.toString()
                val password = binding.editTextPassword.text.toString()
                val email = binding.editTextEmail.text.toString()
                val birthDate = ""
                val fullname = binding.editTextName.text.toString()
                val authKey = "${GlobalHelper.getRandomString(12)}${System.currentTimeMillis()}"
                val createdAt = Date()
                val updatedAt = Date()
                user = User(
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
                    2,
                    createdAt,
                    updatedAt,
                    ""
                )
                viewModel.registerUser(user)
            }
        })
    }

    private fun getStateLogin(){
        viewModelLogin.loginState.observe(this,{
            Log.d("login_state", it.toString())
            when(it){
                NetworkState.LOADING -> {

                }
                NetworkState.SUCCESS -> {
                    finish()
                    if(sharedPrefUtil.get(ConstantAuth.CONSTANT_AUTH_ROLE)?.toInt() == 2){
                        startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                    }else{
                        startActivity(Intent(this, MainActivityAdmin::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
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

    private fun getStateRegister(){
        viewModel.registerState.observe(this,{
            when(it){
                NetworkState.SUCCESS -> {
                    val email = binding.editTextEmail.text.toString()
                    val password = GlobalHelper.sha256(binding.editTextPassword.text.toString())
                    viewModelLogin.loginUser(email, password)
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

}