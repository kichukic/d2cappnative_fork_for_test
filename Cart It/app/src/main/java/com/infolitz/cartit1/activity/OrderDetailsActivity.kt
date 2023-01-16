package com.infolitz.cartit1.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.infolitz.cartit1.R
import com.infolitz.cartit1.adapters.RecyclerViewAdapterCart
import com.infolitz.cartit1.databinding.ActivityOrderDetailsBinding
import com.infolitz.cartit1.helper.ProductViewModal
import com.infolitz.cartit1.helper.UserSessionManager

class OrderDetailsActivity : AppCompatActivity() {

    lateinit var binding: ActivityOrderDetailsBinding
    lateinit var userSessionManager: UserSessionManager
    private lateinit var databaseReference: DatabaseReference

    lateinit var recyclerView: RecyclerView
    lateinit var data: ArrayList<ProductViewModal>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_order_details)
        binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        data = ArrayList<ProductViewModal>()

        initSharedPref()
        initializeDbRef()
        initAllProductID()

        initAddressCard()

        initClicks()
    }

    private fun initAddressCard() {

        binding.orderDetailsShippingAddLayout.shipDateValueTv.text = getDateShipping()

        Log.e("address)))",getCustAddress())
        binding.orderDetailsShippingAddLayout.shipAddValueTv.text = getCustAddress()
        binding.orderDetailsShippingAddLayout.shipCurrStatusValueTv.text= "In Cart"
    }

    private fun getCustAddress(): String {

       /* custFirstName: String,
        custLastName: String,
        custMobile: String,
        custStreetAddress:String,
        custStreetAddress2:String,
        custCity:String,
        custState:String,
        custPin:String*/

        Log.e("---first--",userSessionManager.getCustFirstName())
        Log.e("---last--",userSessionManager.getCustLastName())
        Log.e("---street--",userSessionManager.getCustStreetAddress())
        var ad=""+userSessionManager.getCustFirstName()+" "+
        userSessionManager.getCustLastName()+", "+
                userSessionManager.getCustStreetAddress()+" "+
                userSessionManager.getCustStreetAddress2()+", "+
                userSessionManager.getCustCity()+", "+
                userSessionManager.getCustState()+", "+
                userSessionManager.getCustPin()

        return ad
    }

    private fun getDateShipping(): String {

        return "December 01, 2022"
    }

    private fun initClicks() {
        binding.orderChangeStatusBtn.setOnClickListener {
            var intent = Intent(this, EditAddressActivity::class.java)
            intent.putExtra("custNumber",userSessionManager.getCustMobile())
            startActivity(intent)
        }
    }

    private fun initAllProductID() {//for getting the productids under the present user
        val lngRef =
            databaseReference.child("Agents").child(userSessionManager.getAgentUId()).child("cart")

        lngRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val productIdList = ArrayList<String>()
                val snapshot = task.result
                Log.e("TAG", "productid::" + snapshot.value)
                for (ds in snapshot.children) {
                    Log.e("TAG", "productid::" + ds.key.toString())
                    productIdList.add(ds.key.toString())
                }
                initRecyclerView(productIdList)

            } else {
                Log.e("TAG", task.exception!!.message!!) //Don't ignore potential errors!
            }
        }
    }

    //for loading the products in cart
    private fun initRecyclerView(productIdList: ArrayList<String>) {
        recyclerView = binding!!.orderDetailsProRecyclerView

        // this creates a vertical layout Manager
        recyclerView.layoutManager = LinearLayoutManager(this)

        // ArrayList of class ItemsViewModel

        for (product in productIdList) {
            val lngRef = databaseReference.child("Products").child(product)
            lngRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val snapshot = task.result

                    Log.e(
                        "TAG",
                        "productss___-----: ${snapshot.value}"
                    )// getting values of Products
                    /*  for (ds1 in snapshot.children) {*/
                    val productId = snapshot.key.toString()
                    Log.e("TAG", "product id ::" + productId) //got product id

                    val availPin = snapshot.child("availPin").getValue(Int::class.java)
                    Log.e("TAG", "availPin ::" + availPin) //got availPin

                    val description = snapshot.child("description").getValue(String::class.java)
                    Log.e("TAG", "description ::" + description) //got description
                    val imgUrl = snapshot.child("imgUrl").getValue(String::class.java)
                    Log.e("TAG", "imgUrl ::" + imgUrl) //got imgUrl
                    val name = snapshot.child("name").getValue(String::class.java)
                    Log.e("TAG", "name ::" + name) //got name
                    val price = snapshot.child("price").getValue(Double::class.java)
                    Log.e("TAG", "price ::" + price) //got price
                    val stockCount = snapshot.child("stockCount").getValue(Int::class.java)
                    Log.e("TAG", "stockCount ::" + stockCount) //got stockCount
                    val storeId = snapshot.child("storeId").getValue(String::class.java)
                    Log.e("TAG", "storeId ::" + storeId) //got storeId

                    data.add(
                        ProductViewModal(
                            name!!,
                            productId,
                            availPin!!,
                            description!!,
                            price!!,
                            stockCount!!,
                            storeId!!,
                            imgUrl!!
                        )
                    )

                    val adapter1 = RecyclerViewAdapterCart(this, data/*,quantity*/)
                    recyclerView.adapter =
                        adapter1   // Setting the Adapter with the recyclerview

                } else {
                    Log.e("TAG", task.exception!!.message!!) //Don't ignore potential errors!
                }
            }

        }


    }

    private fun initSharedPref() {
        userSessionManager = UserSessionManager(this)
    }

    //firebase.....
    private fun initializeDbRef() {
        databaseReference = Firebase.database.reference
    }
}