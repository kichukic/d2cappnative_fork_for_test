package com.infolitz.cartitinfo.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.infolitz.cartitinfo.adapters.RecyclerViewCustomerAdapter
import com.infolitz.cartitinfo.databinding.FragmentAgentProfileBinding
import com.infolitz.cartitinfo.helper.UserSessionManager
import com.infolitz.cartitinfo.modals.CustomerModalForAgentProfile
import com.infolitz.cartitinfo.modals.ProductModelHomeFragment

class AgentProfileFragment : Fragment() {

    private var binding: FragmentAgentProfileBinding? = null
    lateinit var userSessionManager: UserSessionManager
    private lateinit var databaseReference: DatabaseReference

    lateinit var bottomnav: BottomNavigationView

    lateinit var recyclerView: RecyclerView
    lateinit var mLayoutManager: RecyclerView.LayoutManager
    lateinit var itemList: List<CustomerModalForAgentProfile>
    lateinit var adapter: RecyclerViewCustomerAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        container?.removeAllViews()
        binding = FragmentAgentProfileBinding.inflate(inflater, container, false)

        bottomnav =
            requireActivity().findViewById(com.infolitz.cartitinfo.R.id.bottomNavigationView);
        disableBottomNav(false)

        initSharedPref()
        initializeDbRef()


        setUserData()
        initRecyclerView()

        searchData()

        initClicks()


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

    private fun initRecyclerView() {

        val lngRef =
            databaseReference.child("Agents").child(userSessionManager.getAgentUId())
                .child("customerId")

        lngRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val orderIdList = ArrayList<String>()
                val snapshot = task.result
                Log.e("TAG", "CustomerId::" + snapshot.value)

                if (snapshot.value.toString() == "null") {
                    Toast.makeText(requireActivity(), "No Items in order...", Toast.LENGTH_LONG)
                        .show()
                }
                binding?.loaderLayout!!.loaderFrameLayout.visibility = View.GONE
                disableBottomNav(true)

//                Log.e("TAG","orderid::"+snapshot.key.toString())


                for (ds in snapshot.children) {
                    Log.e("TAG", "CustomerId::" + ds.key.toString())

                    val customerName = ds.child("customerName").getValue(String::class.java)
                    Log.e("TAG", "customerName ::" + customerName)
                    val customerNumber = ds.child("customerNumber").getValue(String::class.java)
                    Log.e("TAG", "customerNumber ::" + customerNumber)
                    val CustomerId = ds.key.toString()
                    Log.e("TAG", "CustomerId ::" + CustomerId)



                    itemList = itemList + CustomerModalForAgentProfile(
                        customerName!!,
                        customerNumber!!,
                        CustomerId!!
                    )
                    adapter = RecyclerViewCustomerAdapter(requireActivity(), itemList)
                    recyclerView.adapter = adapter
                }


            } else {
                Log.e("TAG", task.exception!!.message!!) //Don't ignore potential errors!
            }
        }

    }

    private fun setUserData() {
        binding!!.profileEmailTv.text = userSessionManager.getAgentEmail()
        binding!!.profileMobileTv.text = userSessionManager.getMobileNumber()
        binding!!.profileNameTv.text = userSessionManager.getAgentName()
    }


    private fun initClicks() {
        //click the card implementation
    }

    //firebase.....
    private fun initializeDbRef() {
        databaseReference = Firebase.database.reference
        //for RecyclerView
        recyclerView = binding!!.recyclerViewCustomers
        mLayoutManager = GridLayoutManager(context, 1)
        recyclerView.layoutManager = mLayoutManager
        //initialisation
        itemList = arrayListOf<CustomerModalForAgentProfile>()
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

    ////////////////////////////////////////////////search
    private fun searchData(){


        // below line is to call set on query text listener method.
        binding!!.searchViewAgentCustomer.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            View.OnFocusChangeListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(msg: String): Boolean {
                // inside on query text change method we are
                // calling a method to filter our recycler view.
                filter(msg)
                return false
            }

            override fun onFocusChange(p0: View?, p1: Boolean) {

                Log.e("tag.2","found changed deeply::")
            }
        })

    }

    private fun filter(text: String) {
        val filteredlist: java.util.ArrayList<CustomerModalForAgentProfile> = java.util.ArrayList()
        for (item in itemList) {
            if (item.customerName.toLowerCase().contains(text.toLowerCase()) || item.customerNumber.toLowerCase().contains(text.toLowerCase())) {
                filteredlist.add(item)
            }

        }
        if (filteredlist.isEmpty()) {
            Toast.makeText(requireActivity(), "No Data Found..", Toast.LENGTH_SHORT).show()
        } else {

            adapter.filterList(filteredlist)
        }
    }

    ////////////////////////////////////////////////search

    private fun disableBottomNav(b: Boolean) {
        bottomnav.menu.findItem(com.infolitz.cartitinfo.R.id.bottom_nav_cart).isEnabled = b
        bottomnav.menu.findItem(com.infolitz.cartitinfo.R.id.bottom_nav_order).isEnabled = b
        bottomnav.menu.findItem(com.infolitz.cartitinfo.R.id.bottom_nav_profile).isEnabled = b
        bottomnav.menu.findItem(com.infolitz.cartitinfo.R.id.bottom_nav_home).isEnabled = b
    }
}