package com.infolitz.cartitinfo.activity

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.infolitz.cartitinfo.R
import com.infolitz.cartitinfo.databinding.ActivityEditAddressBinding
import com.infolitz.cartitinfo.helper.UserSessionManager

class EditAddressActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditAddressBinding
    lateinit var databaseReference:DatabaseReference
    lateinit var userSessionManager:UserSessionManager
    lateinit var custNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_edit_address)
        binding = ActivityEditAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (supportActionBar != null) {
            supportActionBar?.hide(); //to hide actionbar
        }

        initPrevCustNumber()
        initSharedPref()
        initializeDbRef()

        initClicks()
    }
    private fun initPrevCustNumber(){
        custNumber = intent.getStringExtra("custNumber")!!
        binding.addressPhoneEditText.setText(custNumber)
    }

    private fun getCustomerAddressHistory() {
        val lngRef =
            databaseReference.child("Customers").child("ct"+binding.addressPhoneEditText.text)

        lngRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                if(snapshot.value !=null) { //check if the key customerCount Exist
                    Log.e("TAG","customer Address"+snapshot.value)
                    Log.e("TAG","customer first name"+snapshot.child("firstName").value.toString())
                    Log.e("TAG","customer uLastName"+snapshot.child("lastName").value.toString())
                    Log.e("TAG","customer uMobile"+snapshot.child("mobile").value.toString())
                    Log.e("TAG","customer uStreetAddress"+snapshot.child("streetAddress").value.toString())
                    Log.e("TAG","customer uStreetAddress2"+snapshot.child("streetAddress2").value.toString())
                    Log.e("TAG","customer uCity"+snapshot.child("city").value.toString())
                    Log.e("TAG","customer uState"+snapshot.child("state").value.toString())
                    Log.e("TAG","customer uPin"+snapshot.child("pin").value.toString())


                    binding.fetchUserData.text="Data found!!"
                    binding.fetchUserData.isClickable=false
                    binding.fetchUserData.setTextColor(ContextCompat.getColor(this,R.color.green))


                    binding.addressFirstNameEditText.setText(snapshot.child("firstName").value.toString())
                    binding.addressLastNameEditText.setText(snapshot.child("lastName").value.toString())
                    binding.addressPhoneEditText.setText(snapshot.child("mobile").value.toString())
                    binding.addressStreetAddEditText.setText(snapshot.child("streetAddress").value.toString())
                    binding.addressStreetAdd2EditText.setText(snapshot.child("streetAddress2").value.toString())
                    binding.addressCityEditText.setText(snapshot.child("city").value.toString())
                    binding.addressStateEditText.setText(snapshot.child("state").value.toString())
                    binding.addressPincodeEditText.setText(snapshot.child("pin").value.toString())


                    binding.loaderLayout.loaderFrameLayout.visibility=View.GONE
                    binding.loaderLayout.loaderFrameLayout.visibility=View.INVISIBLE
                    /*for (ds in snapshot.children) {
                        Log.e("TAG", "customer ::" + ds.key.toString())
//                    productIdList.add(ds.key.toString())
                        /* if (ds.key.toString() == "quantity") {
                         holder.quantity.text = "${ds.getValue(Int::class.java)!!}"
                         count = ds.getValue(Int::class.java)!!
                     }*/
                    }*/
                }else{
                     //say no data found
                    Log.e("TAG","customer Address"+snapshot.value)
                    binding.loaderLayout.loaderFrameLayout.visibility=View.GONE
                    binding.loaderLayout.loaderFrameLayout.visibility=View.INVISIBLE

                    binding.fetchUserData.text="No Data found!!"
                    binding.fetchUserData.isClickable=false
                    binding.fetchUserData.setTextColor(ContextCompat.getColor(this,R.color.red))
                }

            } else {
                Log.e("TAG customer", task.exception!!.message!!) //Don't ignore potential errors!

            }
        }
    }

    private fun initClicks() {
        binding.addAddressSaveBtn.setOnClickListener {

            if (checkAllDetails()) {
                Log.e("TAG", "all good to go")
                setDataToDB()


                Toast.makeText(applicationContext,"Details Updates successfully!!",Toast.LENGTH_LONG).show()

                var intent =Intent(this,OrderDetailsActivity::class.java)
                startActivity(intent) //intent to order address
            }

        }
        binding.fetchUserData.setOnClickListener{
            binding.loaderLayout.loaderFrameLayout.visibility=View.VISIBLE
            getCustomerAddressHistory()
        }
    }

    private fun setDataToDB() {
        binding.loaderLayout.loaderFrameLayout.visibility=View.VISIBLE
            userSessionManager.setCustomerCount(userSessionManager.getCustomerCount() + 1)

        var agentReference = databaseReference.child("Agents").child(userSessionManager.getAgentUId())
        var userReference = databaseReference.child("Customers").child("ct"+binding.addressPhoneEditText.text)

        agentReference.child("customerCount").setValue(userSessionManager.getCustomerCount()) //increment customercount in agent db


        userReference.child("firstName").setValue(binding.addressFirstNameEditText.text.toString())
        userReference.child("lastName").setValue(binding.addressLastNameEditText.text.toString())
        userReference.child("mobile").setValue(binding.addressPhoneEditText.text.toString())
        userReference.child("streetAddress").setValue(binding.addressStreetAddEditText.text.toString())
        userReference.child("streetAddress2").setValue(binding.addressStreetAdd2EditText.text.toString())
        userReference.child("city").setValue(binding.addressCityEditText.text.toString())
        userReference.child("state").setValue(binding.addressStateEditText.text.toString())
        userReference.child("pin").setValue(binding.addressPincodeEditText.text.toString())

        userReference.child("referredAgentId").setValue(userSessionManager.getAgentUId())



        //saving the current customer address to shared preference
        userSessionManager.saveCurrentCustomerData(binding.addressFirstNameEditText.text.toString(),
            binding.addressLastNameEditText.text.toString(),
            binding.addressPhoneEditText.text.toString(),
            binding.addressStreetAddEditText.text.toString(),
            binding.addressStreetAdd2EditText.text.toString(),
            binding.addressCityEditText.text.toString(),
            binding.addressStateEditText.text.toString(),
            binding.addressPincodeEditText.text.toString())
    }



    private fun checkAllDetails(): Boolean {
        var done = false

        if (!(binding.addressFirstNameEditText.text!!.isBlank() ||
                    binding.addressLastNameEditText.text!!.isBlank() ||
                    binding.addressStreetAddEditText.text!!.isBlank() ||
                    binding.addressCityEditText.text!!.isBlank() ||
                    binding.addressStateEditText.text!!.isBlank() ||
                    binding.addressPincodeEditText.text!!.isBlank() ||
                    binding.addressPhoneEditText.text!!.isBlank())) {

            done = true
            binding.warningAddAllAddressErrorTv.visibility = View.INVISIBLE
            binding.warningAddAllAddressErrorTv.visibility = View.GONE
        } else {
            binding.warningAddAllAddressErrorTv.visibility = View.VISIBLE
            binding.warningAddAllAddressErrorTv.requestFocus()
            Toast.makeText(this,"Fill all the details!",Toast.LENGTH_LONG).show()

        }

        return done
    }

    private fun initSharedPref() {
        userSessionManager = UserSessionManager(this)
    }
    //for firebase
    private fun initializeDbRef() {
        databaseReference = Firebase.database.reference
    }
}