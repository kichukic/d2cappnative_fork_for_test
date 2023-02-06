package com.infolitz.cartitinfo.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.infolitz.cartitinfo.R
import com.infolitz.cartitinfo.databinding.ActivityUpdateProfileBinding
import com.infolitz.cartitinfo.helper.UserSessionManager

class UpdateProfileActivity : AppCompatActivity() {
    lateinit var updateProfileBinding: ActivityUpdateProfileBinding
    private lateinit var agentMobile: String
    //for shared pref
    private lateinit var userSessionManager: UserSessionManager
    //for firebase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateProfileBinding= ActivityUpdateProfileBinding.inflate(layoutInflater)
        setContentView(updateProfileBinding.root)

        agentMobile = intent.getStringExtra("mobile_number").toString()

        if (supportActionBar != null) {
            supportActionBar?.hide(); //to hide actionbar
        }

        initNumber()
        initSharedPref()
        initializeDbRef()

        initClicks()
    }

    private fun initClicks() {
        updateProfileBinding.btUpdateProfile.setOnClickListener{

            if (!isnull(updateProfileBinding.etUserName.text.toString(),updateProfileBinding.etMailId.text.toString(),updateProfileBinding.etPinCode.text.toString())) {

                updateProfileBinding.loaderLayout.loaderFrameLayout.visibility= View.VISIBLE //loader disabling

                userSessionManager.setAgentName(updateProfileBinding.etUserName.text.toString())
                userSessionManager.setAgentEmail(updateProfileBinding.etMailId.text.toString())
                userSessionManager.setAgentPinCode(updateProfileBinding.etPinCode.text.toString().toInt())
                Log.e("taggg",updateProfileBinding.etPinCode.text.toString())
                writeDataToFirebase()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun initNumber() {
        updateProfileBinding.tvMobileNumber.text=""+agentMobile
    }
    private fun writeDataToFirebase() {
        var userReference = databaseReference.child("Agents").child(userSessionManager.getAgentUId())
        userReference.child("agentName").setValue(userSessionManager.getAgentName())
        userReference.child("agentEmail").setValue(userSessionManager.getAgentEmail())
        userReference.child("agentPinCode").setValue(userSessionManager.getAgentPinCode())
    }

    private fun initSharedPref() {
        userSessionManager = UserSessionManager(this)
    }
    private fun isnull(
        userName: String?,
        userEmail: String?,
        pinCode: String?
    ): Boolean {

        var valueNull = true// true means null
        //check if the EditText have values or not
        if ((userName?.trim()?.length!! > 0) && (userEmail?.trim()?.length!! > 0) && (pinCode?.trim()?.length!! > 0)) {
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