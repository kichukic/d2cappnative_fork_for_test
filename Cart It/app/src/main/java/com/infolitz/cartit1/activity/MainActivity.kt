package com.infolitz.cartit1.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.infolitz.cartit1.R
import com.infolitz.cartit1.databinding.ActivityMainBinding
import com.infolitz.cartit1.helper.UserSessionManager

class MainActivity : AppCompatActivity() {

    lateinit var userSessionManager: UserSessionManager
    lateinit var activityMainBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        initSharedPref()
        initUserValues()
    }

    private fun initSharedPref() {
        userSessionManager = UserSessionManager(this)
    }


    private fun initUserValues() {
        Log.e("my_fire","uid:-"+userSessionManager.getUserUId())
    }
}