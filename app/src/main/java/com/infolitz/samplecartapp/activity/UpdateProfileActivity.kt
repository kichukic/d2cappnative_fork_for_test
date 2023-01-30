package com.infolitz.samplecartapp.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.infolitz.samplecartapp.databinding.ActivityUpdateProfileBinding
import com.infolitz.samplecartapp.helper.RetrofitService
import com.infolitz.samplecartapp.helper.UserSessionManager
import com.infolitz.samplecartapp.modals.agentSignUP.ModalAgentRegister
import com.infolitz.samplecartapp.repo.AgentRegisterRepository
import com.infolitz.samplecartapp.viewModel.agentSignUP.AgentRegisterViewModel
import com.infolitz.samplecartapp.viewModel.agentSignUP.AgentRegisterViewModelFactory

class UpdateProfileActivity : AppCompatActivity() {
    lateinit var updateProfileBinding: ActivityUpdateProfileBinding
    private lateinit var agentMobile: String
    //for shared pref
    private lateinit var userSessionManager: UserSessionManager
    //for firebase
    private lateinit var databaseReference: DatabaseReference
    //for retrofit
    private val retrofitService = RetrofitService.getInstance()

    lateinit var viewModel: AgentRegisterViewModel
    var registerReq = mutableListOf<ModalAgentRegister>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateProfileBinding= ActivityUpdateProfileBinding.inflate(layoutInflater)
        setContentView(updateProfileBinding.root)

//        agentMobile = intent.getStringExtra("mobile_number").toString()

//        initNumber()
        initSharedPref()
        initAccessToken()
        initializeDbRef()

        initClicks()
    }

    private fun initClicks() {
        updateProfileBinding.btUpdateProfile.setOnClickListener{

            if (!isnull(updateProfileBinding.etUserName.text.toString(),
                    updateProfileBinding.etMailId.text.toString(),
                    updateProfileBinding.etPassword.text.toString(),
                    updateProfileBinding.etPhoneNumber.text.toString(),
                    updateProfileBinding.etPinCode.text.toString(),
                    updateProfileBinding.etAgentAddress.text.toString())) {

                val number= updateProfileBinding.etPhoneNumber.text.toString().toInt()

                observeResponse(updateProfileBinding.etUserName.text.toString(),
                    updateProfileBinding.etAgentAddress.text.toString(),
                    updateProfileBinding.etPinCode.text.toString().toInt(),
                    updateProfileBinding.etMailId.text.toString(),
                    updateProfileBinding.etPassword.text.toString(),
                    number

                )

                updateProfileBinding.loaderLayout.loaderFrameLayout.visibility= View.VISIBLE //loader disabling




               /* val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)*/
            }
        }
    }

    /*private fun initNumber() {
        updateProfileBinding.tvMobileNumber.text=""+agentMobile
    }*/
    /*private fun writeDataToFirebase() {
        var userReference = databaseReference.child("Agents").child(userSessionManager.getAgentUId())
        userReference.child("agentName").setValue(userSessionManager.getAgentName())
        userReference.child("agentEmail").setValue(userSessionManager.getAgentEmail())
        userReference.child("agentPinCode").setValue(userSessionManager.getAgentPinCode())
    }*/

    private fun initSharedPref() {
        userSessionManager = UserSessionManager(this)
    }
    private fun isnull(
        userName: String?,
        userEmail: String?,
        password: String?,
        phoneNumber: String?,
        pinCode: String?,
        agentAddress: String?
    ): Boolean {

        var valueNull = true// true means null
        //check if the EditText have values or not
        if ((userName?.trim()?.length!! > 0) &&
            (userEmail?.trim()?.length!! > 0) &&
            (password?.trim()?.length!! > 0) &&
            (phoneNumber?.trim()?.length!! > 0) &&
            (agentAddress?.trim()?.length!! > 0) &&
            (pinCode?.trim()?.length!! > 0)) {
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


    private fun initAccessToken() {
        viewModel = ViewModelProvider(
            this,
            AgentRegisterViewModelFactory(AgentRegisterRepository(retrofitService))
        ).get(
            AgentRegisterViewModel::class.java
        )
    }

    private fun observeResponse(username: String?,
                                address: String?,
                                pincode: Int?,
                                email: String?,
                                password: String?,
                                phone: Int?) {
        viewModel.dataList.observe(this) {
            Log.e("RESPONSE", "DATA : $it")
            if (it.response == "registered") {
                Log.e("User Registered token::",it.token.toString())
                saveDataUserSession()
                gotoHomeMain()
            } else {
                Log.e("response","not registered")
            }
        }
        viewModel.errorMessage.observe(this) {
            Log.e("ERROR RESPONSE..", it.toString())
            // have to show error layout
            /*MyDialogSheet(this).apply {
                setTitle(getString(R.string.oops))
                setMessage(getString(R.string.server_error))
                setPositiveButton(getString(R.string.retry)) {
                    viewModel.getProfile(profileReq)
                }
                setNegativeButton(getString(R.string.cancel), null)
            }.show()*/
        }
        registerReq.add(
            ModalAgentRegister(
                username!!,
                address!!,
                pincode!!,
                email!!,
                password!!,
                phone!!,
            )
        )
        viewModel.getAgentRegister(registerReq)
    }

    private fun gotoHomeMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun saveDataUserSession() {
        userSessionManager.setAgentName(updateProfileBinding.etUserName.text.toString())
        userSessionManager.setAgentEmail(updateProfileBinding.etMailId.text.toString())
        userSessionManager.setAgentPinCode(updateProfileBinding.etPinCode.text.toString().toInt())

        userSessionManager.setAgentAddress(updateProfileBinding.etAgentAddress.text.toString())//agent address
        userSessionManager.setMobileNumber(updateProfileBinding.etPhoneNumber.text.toString())//agent phone
        Log.e("Update Profile",updateProfileBinding.etPinCode.text.toString())
    }
}