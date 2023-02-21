package com.infolitz.cartitinfo.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.infolitz.cartitinfo.R
import com.infolitz.cartitinfo.adapters.RecyclerViewOrdersAdapter
import com.infolitz.cartitinfo.databinding.ActivityCustomerOrderDetailsBinding
import com.infolitz.cartitinfo.databinding.ActivityLoginBinding
import com.infolitz.cartitinfo.helper.UserSessionManager
import com.infolitz.cartitinfo.modals.OrderProductModal

class CustomerOrderDetailsActivity : AppCompatActivity() {

    lateinit var userSessionManager: UserSessionManager
    private lateinit var databaseReference: DatabaseReference
    lateinit var customerId: String
    lateinit var binding: ActivityCustomerOrderDetailsBinding
    lateinit var recyclerView: RecyclerView
    lateinit var mLayoutManager: RecyclerView.LayoutManager
    lateinit var adapter: RecyclerViewOrdersAdapter
    lateinit var itemList: List<OrderProductModal>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setContentView(R.layout.activity_customer_order_details)

        customerId = intent.getStringExtra("customer_id")!!
        Log.e("the key is", customerId)

        initSharedPref()
        initializeDbRef()

        setContentForRecyclerView()
    }

    private fun setContentForRecyclerView() {
        val lngRef =databaseReference.child("Customers").child(customerId).child("order")


        lngRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val orderIdList = ArrayList<String>()
                val snapshot = task.result
                Log.e("TAG","orderid::"+snapshot.value)

                if (snapshot.value.toString()=="null"){
                    Toast.makeText(this, "No Items in order...", Toast.LENGTH_LONG)
                        .show()
                }
                binding.loaderLayout.loaderFrameLayout.visibility= View.GONE
                binding.ordersEmptyTextView.visibility= View.GONE




                for (ds in snapshot.children) {
                    Log.e("TAG","orderid::"+ds.key.toString())

                    val productName = ds.child("productName").getValue(String::class.java)
                    Log.e("TAG", "productName ::" + productName)
                    val orderStatus = ds.child("orderStatus").getValue(String::class.java)
                    Log.e("TAG", "orderStatus ::" + orderStatus)
                    val totalPrice = ds.child("totalPrice").getValue(Double::class.java)
                    Log.e("TAG", "totalPrice ::" + totalPrice)
                    val placedDate = ds.child("placedDate").getValue(String::class.java)
                    Log.e("TAG", "placedDate ::" + placedDate)
                    val productId = ds.child("productId").getValue(String::class.java)
                    Log.e("TAG", "productId ::" + productId)

                    Log.e("TAG", "customerId ::" + customerId)
                    val quantity = ds.child("quantity").getValue(String::class.java)
                    Log.e("TAG", "quantity ::" + quantity)

                    val customerMobile = ds.child("customerMobile").getValue(String::class.java)
                    Log.e("TAG", "customerMobile ::" + customerMobile)
                    val customerAddress = ds.child("customerAddress").getValue(String::class.java)
                    Log.e("TAG", "customerAddress ::" + customerAddress)


                    itemList = itemList + OrderProductModal(productName!!,productId!!,totalPrice!!,quantity!!,orderStatus!!,placedDate!!,customerAddress!!,customerMobile!!)
                    adapter = RecyclerViewOrdersAdapter(this,itemList)
                    recyclerView.adapter =adapter
                }



            } else {
                Log.e("TAG", task.exception!!.message!!) //Don't ignore potential errors!
            }
        }

        /* itemList = itemList + OrderProductModal(name!!,productId,description!!,newPrice!!,stockCount!!, storeId!!,imgUrl!!,productOldPrice!!,false)
         adapter = RecyclerViewOrdersAdapter(requireActivity(),itemList*//*,quantity*//*)
        gridView.adapter =adapter*/
    }
    private fun initSharedPref() {
        userSessionManager = UserSessionManager(this)
    }
    private fun initializeDbRef() {
        databaseReference = Firebase.database.reference
        //for RecyclerView
        recyclerView = binding!!.orderAllOrdersRecyclerView
        mLayoutManager = GridLayoutManager(this, 1)
        recyclerView.layoutManager = mLayoutManager
        //initialisation
        itemList = arrayListOf<OrderProductModal>()
    }
}