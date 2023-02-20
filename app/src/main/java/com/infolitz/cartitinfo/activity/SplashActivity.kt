package com.infolitz.cartitinfo.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.infolitz.cartitinfo.R
import com.infolitz.cartitinfo.helper.UserSessionManager

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        initDoAction()
    }

    private fun initDoAction() {
        Handler().postDelayed({
            val sharedPref = UserSessionManager(this@SplashActivity)
            Log.e("getIsUserLoggedIn", sharedPref.getIsAgentLoggedIn().toString())
            Log.e("setFirstLogin", sharedPref.setFirstLogin().toString())
            if (!sharedPref.getFirstLogin() && sharedPref.getIsAgentLoggedIn()) {
                sharedPref.setIsAgentLoggedIn(true)
                startActivity(Intent(applicationContext, MainActivity::class.java))
            } else {
                sharedPref.setFirstLogin()
                startActivity(Intent(applicationContext, LoginActivity::class.java))
            }
            finish()
        }, 2000)
    }

}