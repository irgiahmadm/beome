package com.beome

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.beome.constant.ConstantAuth
import com.beome.ui.authentication.login.LoginActivity
import com.beome.utilities.SharedPrefUtil

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPrefUtil: SharedPrefUtil
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPrefUtil = SharedPrefUtil()
        sharedPrefUtil.start(this, ConstantAuth.CONSTANT_PREFERENCE)
        if (sharedPrefUtil.get(ConstantAuth.CONSTANT_AUTH_KEY).isNullOrEmpty() || sharedPrefUtil.get(ConstantAuth.CONSTANT_AUTH_STATUS).toString().toInt() == 2) {
            Log.d("loginStatus", sharedPrefUtil.get(ConstantAuth.CONSTANT_AUTH_STATUS).toString())
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            setContentView(R.layout.activity_main)
            val navView: BottomNavigationView = findViewById(R.id.nav_view)

            val navController = findNavController(R.id.nav_host_fragment)
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            /*val appBarConfiguration = AppBarConfiguration(setOf(
                    R.id.navigation_home, R.id.navigation_search, R.id.navigation_add_post, R.id.navigation_like, R.id.navigation_profile))
            setupActionBarWithNavController(navController, appBarConfiguration)*/
            navView.setupWithNavController(navController)
        }
    }
}