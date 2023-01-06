package com.infolitz.cartit1.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.infolitz.cartit1.R
import com.infolitz.cartit1.databinding.ActivityUpdateProfileBinding
import com.infolitz.cartit1.helper.UserSessionManager

class UpdateProfileActivity : AppCompatActivity() {
    lateinit var updateProfileBinding: ActivityUpdateProfileBinding
    private lateinit var userMobile: String
    //for shared pref
    private lateinit var userSessionManager: UserSessionManager
    //for firebase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateProfileBinding= ActivityUpdateProfileBinding.inflate(layoutInflater)
        setContentView(updateProfileBinding.root)

        userMobile = intent.getStringExtra("mobile_number").toString()

        initNumber()
        initSharedPref()
        initializeDbRef()

        initClicks()
    }

    private fun initClicks() {
        updateProfileBinding.btUpdateProfile.setOnClickListener{

            if (!isnull(updateProfileBinding.etUserName.text.toString(),updateProfileBinding.etMailId.text.toString())) {
                userSessionManager.setUserName(updateProfileBinding.etUserName.text.toString())
                userSessionManager.setUserEmail(updateProfileBinding.etMailId.text.toString())
                writeDataToFirebase()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun initNumber() {
        updateProfileBinding.tvMobileNumber.text=userMobile
    }
    private fun writeDataToFirebase() {
        var userReference = databaseReference.child("Users").child(userSessionManager.getUserUId())
        userReference.child("userName").setValue(userSessionManager.getUserName())
        userReference.child("userEmail").setValue(userSessionManager.getUserEmail())
    }

    private fun initSharedPref() {
        userSessionManager = UserSessionManager(this)
    }
    private fun isnull(
        userName: String?,
        userEmail: String?,
    ): Boolean {

        var valueNull = true// true means null
        //check if the EditText have values or not
        if ((userName?.trim()?.length!! > 0) && (userEmail?.trim()?.length!! > 0)) {
            valueNull = false// false means not null
        } else {
            Toast.makeText(applicationContext, "Please enter all data's! ", Toast.LENGTH_SHORT).show()
        }
        return valueNull
    }

    //for firebase
    private fun initializeDbRef() {
        databaseReference = Firebase.database.reference
    }
}