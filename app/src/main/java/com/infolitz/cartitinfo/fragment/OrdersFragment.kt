package com.infolitz.cartitinfo.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.infolitz.cartitinfo.adapters.RecyclerViewOrdersAdapter
import com.infolitz.cartitinfo.databinding.FragmentOrdersBinding
import com.infolitz.cartitinfo.modals.OrderProductModal
import com.infolitz.cartitinfo.helper.UserSessionManager

class OrdersFragment : Fragment() {

    private var binding: FragmentOrdersBinding? = null

    lateinit var gridView: RecyclerView

    //    lateinit var gridView: GridView
    lateinit var itemList: List<OrderProductModal>

    lateinit var userSessionManager: UserSessionManager

    lateinit var mLayoutManager: RecyclerView.LayoutManager
    lateinit var adapter: RecyclerViewOrdersAdapter

    lateinit var textViewOldPrice: TextView
    lateinit var textViewNewPrice: TextView
    lateinit var searchView: SearchView
    val productIdInStoreList = ArrayList<String>()

    private lateinit var databaseReference: DatabaseReference

    lateinit var bottomnav: BottomNavigationView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        container?.removeAllViews()
        binding = FragmentOrdersBinding.inflate(inflater, container, false)

        bottomnav = requireActivity().findViewById(com.infolitz.cartitinfo.R.id.bottomNavigationView);
        disableBottomNav(false)

        initSharedPref()
        initializeDbRef()

        setContentForRecyclerView()

        initClicks()


//        initViewWithDBdata()


        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) { //triggered on back button press
                override fun handleOnBackPressed() {
//                    requireActivity().finish()
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), callback)

        // Inflate the layout for this fragment
        return binding?.root
    }

    private fun setContentForRecyclerView() {
        val lngRef =
            databaseReference.child("Agents").child(userSessionManager.getAgentUId()).child("order")

        lngRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val orderIdList = ArrayList<String>()
                val snapshot = task.result
//                Log.e("TAG","orderid::"+snapshot.value)

                if (snapshot.value.toString()=="null"){
                    Toast.makeText(requireActivity(), "No Items in order...", Toast.LENGTH_LONG)
                        .show()
                }
                binding?.loaderLayout!!.loaderFrameLayout.visibility= View.GONE
                binding!!.ordersEmptyTextView.visibility= View.GONE
                disableBottomNav(true)



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
                    val customerId = ds.child("customerId").getValue(String::class.java)
                    Log.e("TAG", "customerId ::" + customerId)
                    val quantity = ds.child("quantity").getValue(String::class.java)
                    Log.e("TAG", "quantity ::" + quantity)
                    val customerMobile = ds.child("customerMobile").getValue(String::class.java)
                    Log.e("TAG", "customerMobile ::" + customerMobile)
                    val customerAddress = ds.child("customerAddress").getValue(String::class.java)
                    Log.e("TAG", "quantity ::" + customerAddress)


                    itemList = itemList + OrderProductModal(productName!!,productId!!,totalPrice!!,quantity!!,orderStatus!!,placedDate!!,customerAddress!!,customerMobile!!)
                    adapter = RecyclerViewOrdersAdapter(requireActivity(),itemList/*,quantity*/)
                    gridView.adapter =adapter
                }



            } else {
                Log.e("TAG", task.exception!!.message!!) //Don't ignore potential errors!
            }
        }

       /* itemList = itemList + OrderProductModal(name!!,productId,description!!,newPrice!!,stockCount!!, storeId!!,imgUrl!!,productOldPrice!!,false)
        adapter = RecyclerViewOrdersAdapter(requireActivity(),itemList*//*,quantity*//*)
        gridView.adapter =adapter*/
    }

    private fun initClicks() {
    //click the card implementation
    }

    //firebase.....
    private fun initializeDbRef() {
        databaseReference = Firebase.database.reference

         //for RecyclerView
         gridView = binding!!.orderAllOrdersRecyclerView
         mLayoutManager = GridLayoutManager(context, 1)
         gridView.layoutManager = mLayoutManager
        //initialisation
        itemList = arrayListOf<OrderProductModal>()
    }


    //firebase.....close


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OrdersFragment().apply {
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onResume() {
        super.onResume()
    }

    private fun initSharedPref() {
        userSessionManager = UserSessionManager(requireActivity())
    }

    private fun disableBottomNav(b: Boolean) {
        bottomnav.menu.findItem(com.infolitz.cartitinfo.R.id.bottom_nav_cart).isEnabled = b
        bottomnav.menu.findItem(com.infolitz.cartitinfo.R.id.bottom_nav_order).isEnabled = b
        bottomnav.menu.findItem(com.infolitz.cartitinfo.R.id.bottom_nav_profile).isEnabled = b
        bottomnav.menu.findItem(com.infolitz.cartitinfo.R.id.bottom_nav_home).isEnabled = b
    }
}