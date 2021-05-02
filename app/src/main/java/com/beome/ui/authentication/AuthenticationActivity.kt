package com.beome.ui.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.beome.MainActivity
import com.beome.R
import com.beome.constant.ConstantAuth
import com.beome.ui.admin.MainActivityAdmin
import com.beome.ui.authentication.login.LoginActivity
import com.beome.utilities.SharedPrefUtil

class AuthenticationActivity : AppCompatActivity() {
    private lateinit var sharedPrefUtil: SharedPrefUtil
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        sharedPrefUtil = SharedPrefUtil()
        sharedPrefUtil.start(this, ConstantAuth.CONSTANT_PREFERENCE)
        if (sharedPrefUtil.get(ConstantAuth.CONSTANT_AUTH_KEY).isNullOrEmpty()) {
            finish()
            startActivity(Intent(this, LoginActivity::class.java))
        }else{
            if(sharedPrefUtil.get(ConstantAuth.CONSTANT_AUTH_ROLE)?.toInt() == 2){
                finish()
                startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK))
            }else{
                finish()
                startActivity(Intent(this, MainActivityAdmin::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK))
            }
        }
    }
}