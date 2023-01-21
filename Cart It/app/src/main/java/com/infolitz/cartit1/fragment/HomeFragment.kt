package com.infolitz.cartit1.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.infolitz.cartit1.R
import com.infolitz.cartit1.activity.ProductDescripActivity
import com.infolitz.cartit1.adapters.GridViewAdapterHome
import com.infolitz.cartit1.databinding.FragmentHomeBinding
import com.infolitz.cartit1.helper.ProductViewModal
import com.infolitz.cartit1.helper.UserSessionManager


class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null

    lateinit var gridView: GridView
    lateinit var itemList: List<ProductViewModal>

    lateinit var userSessionManager: UserSessionManager

    lateinit var textViewOldPrice: TextView
    lateinit var textViewNewPrice: TextView
    lateinit var searchView: SearchView
    val productIdInStoreList = ArrayList<String>()

    private lateinit var databaseReference: DatabaseReference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        container?.removeAllViews()
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        initSharedPref()
        initializeDbRef()

        setPinCode()


//        initViewWithDBdata()
        initSearch()




        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) { //triggered on back button press
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), callback)

        // Inflate the layout for this fragment
        return binding?.root
    }

    private fun setPinCode() {
       val pinCode= userSessionManager.getAgentPinCode()
        Log.e("current pincode is",":"+pinCode)
        val lngRef =
            databaseReference.child("PinCode").child(pinCode.toString())

        lngRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val storeIdList = ArrayList<String>()
                val snapshot = task.result
                Log.e("TAG","storeid::"+snapshot.value) //storeid::{s1=sIdsId1673406243}
                for (ds in snapshot.children) {
                    Log.e("TAG","store_id::"+ds.value.toString())
                    storeIdList.add(ds.value.toString())
                    /* if (ds.key.toString() == "quantity") {
                         holder.quantity.text = "${ds.getValue(Int::class.java)!!}"
                         count = ds.getValue(Int::class.java)!!
                     }*/
                }
                getProductIdFromStore(storeIdList)
//                initRecyclerView(productIdList)

            } else {
                Log.e("TAG.", task.exception!!.message!!) //Don't ignore potential errors!
            }
        }

    }

    private fun getProductIdFromStore(storeIdList: ArrayList<String>) { //to get product id from store
        productIdInStoreList.clear()
        for (storeId in storeIdList){

            val lngRef =
                databaseReference.child("Store").child(storeId).child("avilProduct")

            lngRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
//                    val storeIdList = ArrayList<String>()
                    val snapshot = task.result
                    Log.e("TAG","Product id array::"+snapshot.value) //Product id array::{proId1673406243={price=48, stock=4}, proId13245678={price=89, stock=2}}
                    for (ds in snapshot.children) {
                        Log.e("TAG","Product_id::"+ds.key.toString())
                        productIdInStoreList.add(ds.key.toString())
                        /* if (ds.key.toString() == "quantity") {
                             holder.quantity.text = "${ds.getValue(Int::class.java)!!}"
                             count = ds.getValue(Int::class.java)!!
                         }*/
                    }
                    initViewWithDBdata(productIdInStoreList)

                } else {
                    Log.e("TAG.", task.exception!!.message!!) //Don't ignore potential errors!
                }
            }

        }

    }

    //firebase.....
    private fun initializeDbRef() {
        databaseReference = Firebase.database.reference
    }

    private fun initViewWithDBdata(productIdInStoreList:ArrayList<String>) {
        gridView = binding!!.gridView
        itemList = ArrayList<ProductViewModal>()


        for (productId in productIdInStoreList){

            val lngRef = databaseReference.child("Products").child(productId)
            lngRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {


                    val snapshot = task.result

//                val username = snapshot.child("username").getValue(String::class.java)
//                Log.e("TAG", "productss12: ${snapshot.value}"+"from id " +productId)// getting values of Products

                    val productId11= snapshot.key.toString()
                    Log.e("TAG","product id ::"+productId11) //got product id

                    val description = snapshot.child("description").getValue(String::class.java)
                    Log.e("TAG","description ::"+description) //got description
                    val imgUrl= snapshot.child("imgUrl").getValue(String::class.java)
                    Log.e("TAG","imgUrl ::"+imgUrl) //got imgUrl
                    val name = snapshot.child("name").getValue(String::class.java)
                    Log.e("TAG","name ::"+name) //got name
                    val price = snapshot.child("price").getValue(Double::class.java)
                    Log.e("TAG","price ::"+price) //got price
                    val stockCount = snapshot.child("stockCount").getValue(Int::class.java)
                    Log.e("TAG","stockCount ::"+stockCount) //got stockCount
                    val storeId = snapshot.child("storeId").getValue(String::class.java)
                    Log.e("TAG","storeId ::"+storeId) //got storeId

                    itemList = itemList + ProductViewModal(name!!,productId,description!!,price!!,stockCount!!, storeId!!,imgUrl!!)

                    val itemAdapter = GridViewAdapterHome(itemList = itemList, requireActivity()) //initialising adapter
                    gridView.adapter = itemAdapter

                    /*      for (ds in snapshot.children) {
                              val productId= ds.key.toString()
                              Log.e("TAG","product id ::"+productId) //got product id

                              val description = ds.child("description").getValue(String::class.java)
                              Log.e("TAG","description ::"+description) //got description
                              val imgUrl= ds.child("imgUrl").getValue(String::class.java)
                              Log.e("TAG","imgUrl ::"+imgUrl) //got imgUrl
                              val name = ds.child("name").getValue(String::class.java)
                              Log.e("TAG","name ::"+name) //got name
                              val price = ds.child("price").getValue(Double::class.java)
                              Log.e("TAG","price ::"+price) //got price
                              val stockCount = ds.child("stockCount").getValue(Int::class.java)
                              Log.e("TAG","stockCount ::"+stockCount) //got stockCount
                              val storeId = ds.child("storeId").getValue(String::class.java)
                              Log.e("TAG","storeId ::"+storeId) //got storeId


                              itemList = itemList + ProductViewModal(name!!,productId,description!!,price!!,stockCount!!, storeId!!,imgUrl!!)
      //                    itemList = itemList + ProductViewModal(name!!,productId,availPin!!,description!!,price!!,stockCount!!, storeId!!,R.drawable.img_curry_powder_cumin)

                          }*/

              /*      val itemAdapter = GridViewAdapterHome(itemList = itemList, requireActivity()) //initialising adapter
                    gridView.adapter = itemAdapter */ // on below line we are setting adapter to our grid view.

                } else {
                    Log.e("TAG", task.exception!!.message!!) //Don't ignore potential errors!
                }
            }

        }


        //////////////////////////////////////////////////////////////////////////////////////////////////////working code start
        /*val lngRef = databaseReference.child("Products")
        lngRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {

                itemList = ArrayList<ProductViewModal>()

                val snapshot = task.result

//                val username = snapshot.child("username").getValue(String::class.java)
//                Log.e("TAG", "productss: ${snapshot.value}")// getting values of Products
                for (ds in snapshot.children) {
                    val productId= ds.key.toString()
                    Log.e("TAG","product id ::"+productId) //got product id

                   *//* val availPin = ds.child("availPin").getValue(Int::class.java)
                    Log.e("TAG","availPin ::"+availPin) //got availPin*//*

                    val description = ds.child("description").getValue(String::class.java)
                    Log.e("TAG","description ::"+description) //got description
                    val imgUrl= ds.child("imgUrl").getValue(String::class.java)
                    Log.e("TAG","imgUrl ::"+imgUrl) //got imgUrl
                    val name = ds.child("name").getValue(String::class.java)
                    Log.e("TAG","name ::"+name) //got name
                    val price = ds.child("price").getValue(Double::class.java)
                    Log.e("TAG","price ::"+price) //got price
                    val stockCount = ds.child("stockCount").getValue(Int::class.java)
                    Log.e("TAG","stockCount ::"+stockCount) //got stockCount
                    val storeId = ds.child("storeId").getValue(String::class.java)
                    Log.e("TAG","storeId ::"+storeId) //got storeId
                    *//*val imgUrl = ds.child("ImageUrl").getValue(String::class.java)
                    Log.e("imgurl", imgUrl.toString())
                    arrayOfLang.add(LanguagesDashboard(ds.key.toString(), imgUrl))*//*

                    itemList = itemList + ProductViewModal(name!!,productId,description!!,price!!,stockCount!!, storeId!!,imgUrl!!)
//                    itemList = itemList + ProductViewModal(name!!,productId,availPin!!,description!!,price!!,stockCount!!, storeId!!,R.drawable.img_curry_powder_cumin)

                }

                val itemAdapter = GridViewAdapterHome(itemList = itemList, requireActivity()) //initialising adapter
                gridView.adapter = itemAdapter  // on below line we are setting adapter to our grid view.

            } else {
                Log.e("TAG", task.exception!!.message!!) //Don't ignore potential errors!
            }
        }*/
        //////////////////////////////////////////////////////////////////////////////////////////////////////working code end

        gridView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ -> //onclicking the grid
            Toast.makeText(
                requireActivity(), itemList[position].productName + " selected",
                Toast.LENGTH_SHORT
            ).show()

            val intent = Intent (activity, ProductDescripActivity::class.java)
            intent.putExtra("item_id",itemList[position].productId)
            intent.putExtra("item_count",""+1)
            startActivity(intent)
        }


       /* val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    for (ds2 in ds.children) {
                        if (ds2.key.toString() == Firebase.auth.uid.toString()) {//checking required uid and our uid equal
                            for (ds3 in ds2.children) {
                                //getting values
                                if (ds3.key.toString() == "userCollege") {
                                    Log.e("userCollege:", ds3.value.toString())
                                    activityUpdateProfileBinding.etCollege.setText(ds3.value.toString())
                                }
                                if (ds3.key.toString() == "userCourse") {
                                    Log.e("userCourse:", ds3.value.toString())
                                    activityUpdateProfileBinding.etCourse.setText(ds3.value.toString())

                                }
                                if (ds3.key.toString() == "userPassOutYear") {
                                    Log.e("userPassOutYear:", ds3.value.toString())
                                    activityUpdateProfileBinding.etYearOfPassOut.setText(ds3.value.toString())

                                }
                            }
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // handle error

            }
        }
        databaseReference.addListenerForSingleValueEvent(menuListener)*/
    }
    //firebase.....close











    private fun initSearch() {
        /*gridView = binding!!.gridView
        itemList = ArrayList<ProductViewModal>()*/


//        itemList = itemList + ProductViewModal("1","pid123",671531,"abcdd",20.0,100, "sid",R.drawable.img_curry_powder_cumin)
       /* itemList = itemList + ProductViewModal("2", R.drawable.ic_user_profile)
        itemList = itemList + ProductViewModal("3", R.drawable.ic_user_profile)
        itemList = itemList + ProductViewModal("4", R.drawable.ic_user_profile)
        itemList = itemList + ProductViewModal("5", R.drawable.ic_user_profile)*/

        // on below line we are initializing our course adapter
        // and passing course list and context.


//        val itemAdapter = GridViewAdapterHome(itemList = itemList, requireActivity()) //initialising adapter
//        gridView.adapter = itemAdapter  // on below line we are setting adapter to our grid view.


       /* searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                var i=0
                while (i<itemAdapter.count){
                    if (itemList.get(i).itemName.contains(query)) {
                        itemAdapter.ge.filter(query)
                    } else {
                        Toast.makeText(requireActivity(), "No Match found", Toast.LENGTH_LONG)
                            .show()
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                //    adapter.getFilter().filter(newText);
                return false
            }
        })*/

      /*  gridView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            Toast.makeText(
                requireActivity(), itemList[position].productName + " selected",
                Toast.LENGTH_SHORT
            ).show()

            val intent = Intent (activity, ProductDescripActivity::class.java)
            intent.putExtra("item_id",itemList[position].productName)
            startActivity(intent)
        }*/

    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
            }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        binding=null
    }
    override fun onResume() {
        super.onResume()
    }
    private fun initSharedPref() {
        userSessionManager = UserSessionManager(requireActivity())
    }
}