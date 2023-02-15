package com.infolitz.cartitinfo.fragment

import android.R
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.infolitz.cartitinfo.activity.MainActivity
import com.infolitz.cartitinfo.activity.OrderDetailsActivity
import com.infolitz.cartitinfo.adapters.RecyclerViewHomeAdapter
import com.infolitz.cartitinfo.databinding.FragmentHomeBinding
import com.infolitz.cartitinfo.helper.ProductModelHomeFragment
import com.infolitz.cartitinfo.helper.ProductViewModal
import com.infolitz.cartitinfo.helper.UserSessionManager
import retrofit2.http.Field
import java.util.*


class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null

    lateinit var gridView: RecyclerView
//    lateinit var gridView: GridView
    lateinit var itemList: List<ProductModelHomeFragment>

    lateinit var tempArrayList: ArrayList<ProductModelHomeFragment> /// for searching purpose

    lateinit var itemNameList: ArrayList<String>

    lateinit var userSessionManager: UserSessionManager

    lateinit var  mLayoutManager: RecyclerView.LayoutManager
    lateinit var adapter1: RecyclerViewHomeAdapter

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

        searchData()


        initClicks()
        showAddToCartButton(false)//add to cart button hidden


//        initViewWithDBdata()





        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) { //triggered on back button press
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), callback)

        // Inflate the layout for this fragment
        return binding?.root
    }

    private fun initClicks() {
        binding?.homeProDetailsAddCartBtn?.setOnClickListener{ //on clicking add to cart item
            addToCart()

        }
    }

    private fun addToCart() {
        val alertDialog =AlertDialog.Builder(requireActivity())
        alertDialog.setTitle("Add to cart")
        alertDialog.setMessage("Do you want to cart the items?")
        alertDialog.setPositiveButton("Yes"){_,_ ->
            Toast.makeText(requireActivity(),"Added to Cart Successfully",Toast.LENGTH_SHORT).show()
            adapter1.clearTheSelected()
//            addDataToCartDB()
            showAddToCartButton(false)
            var intent = Intent(requireActivity(), MainActivity::class.java)
            startActivity(intent)
        }
        alertDialog.setNegativeButton("No"){
            _,_ ->
        }
        alertDialog.show()
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
                if (snapshot.value.toString()=="null"){
                    Toast.makeText(requireActivity(), "No Stores Available..Change Pincode", Toast.LENGTH_LONG)
                        .show()
                }
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


        //for RecyclerView
        gridView = binding!!.gridView
        mLayoutManager = GridLayoutManager(context, 2)
        gridView.layoutManager = mLayoutManager
    }

    ////////////////////////////////////////////////search
    private fun searchData(){


        // below line is to call set on query text listener method.
        binding!!.searchViewHome.setOnQueryTextListener(object :SearchView.OnQueryTextListener,
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
        // creating a new array list to filter our data.
        val filteredlist: ArrayList<ProductModelHomeFragment> = ArrayList()

        // running a for loop to compare elements.
        for (item in itemList) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.productName.toLowerCase().contains(text.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item)
            }
        }
        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            Toast.makeText(requireActivity(), "No Data Found..", Toast.LENGTH_SHORT).show()
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
//            val adapter1 = RecyclerViewHomeAdapter(requireActivity(), itemList/*,quantity*/)
//            gridView.adapter =adapter1

            adapter1.filterList(filteredlist)
        }
    }

    ////////////////////////////////////////////////search

    private fun initViewWithDBdata(productIdInStoreList:ArrayList<String>) {
//        gridView = binding!!.gridView //
//        itemList = ArrayList<ProductViewModal>()
        itemList = arrayListOf<ProductModelHomeFragment>()

        tempArrayList = arrayListOf<ProductModelHomeFragment>()

        itemNameList =ArrayList<String>()


        for (productId in productIdInStoreList){
            tempArrayList.clear()

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
                    itemNameList.add(name!!)

                    val newPrice = snapshot.child("offerPrice").getValue(Double::class.java)
                    Log.e("TAG","offerPrice ::"+newPrice) //got offerPrice

                    val productOldPrice = snapshot.child("price").getValue(Double::class.java)
                    Log.e("TAG","price ::"+productOldPrice) //got old price

                    val stockCount = snapshot.child("stockCount").getValue(Int::class.java)
                    Log.e("TAG","stockCount ::"+stockCount) //got stockCount
                    val storeId = snapshot.child("storeId").getValue(String::class.java)
                    Log.e("TAG","storeId ::"+storeId) //got storeId



                    itemList = itemList + ProductModelHomeFragment(name!!,productId,description!!,newPrice!!,stockCount!!, storeId!!,imgUrl!!,productOldPrice!!,false)


                    tempArrayList.addAll(itemList)

                    //new adding
                   /* val mLayoutManager: RecyclerView.LayoutManager = GridLayoutManager(context, 2)
                    gridView.layoutManager = mLayoutManager*/
                    adapter1 = RecyclerViewHomeAdapter(requireActivity(),{show-> showAddToCartButton(show)},itemList/*,quantity*/)
                    gridView.adapter =adapter1   // Setting the Adapter with the recyclerview
                    // new adding end



                   /* val itemAdapter = GridViewAdapterHome(itemList = itemList, requireActivity()) //initialising adapter
                    gridView.adapter = itemAdapter*/

                    //search start.....................

                    binding!!.searchViewHome.isSubmitButtonEnabled = true //side button enabled in  search button


                    binding!!.searchViewHome.setQueryHint(Html.fromHtml("<font color = #505050>" + getResources().getString(R.string.search_go) + "</font>"));


                   binding!!.searchViewHome.setOnQueryTextFocusChangeListener(object :SearchView.OnQueryTextListener,
                        View.OnFocusChangeListener {
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            // if query exist or not.
                            if (itemNameList.contains(query)) {
                                // if query exist within list we
                                // are filtering our list adapter.
                                Log.e("tag.1","found::"+query)
                               /* adapter1.filter.filter(query)*/
                            } else {
                                // if query is not present we are displaying
                                // a toast message as no  data found..
                                Toast.makeText(requireActivity(), "No Language found..", Toast.LENGTH_LONG)
                                    .show()
                            }
                            return false
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {

                            tempArrayList.clear()
                            val searchText=newText!!.toLowerCase(Locale.getDefault())

                            if (searchText.isNotEmpty()){
                                itemList.forEach{

                                     if (it.productName.toLowerCase(Locale.getDefault()).contains(searchText)){
                                         tempArrayList.add(it)
                                     }
                                }

                                gridView.adapter!!.notifyDataSetChanged()
                            }else {
                                    tempArrayList.clear()
                                tempArrayList.addAll(itemList)
                                gridView.adapter!!.notifyDataSetChanged()

                            }
                            // are filtering our adapter with
                            // new text on below line.
                          /*  itemAdapter.filter.filter(newText)*/
                            Log.e("tag.1","found changed ::"+newText)
                            return false
                        }

                        override fun onFocusChange(p0: View?, p1: Boolean) {
                            Log.e("tag.1","found changed deeply::")
                        }


                    })
                    ////////////////////////////////////////////////////////////search end

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











        /*gridView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ -> //onclicking the grid
            Toast.makeText(
                requireActivity(), itemList[position].productName + " selected",
                Toast.LENGTH_SHORT
            ).show()

            val intent = Intent (activity, ProductDescripActivity::class.java)
            intent.putExtra("item_id",itemList[position].productId)
            intent.putExtra("item_count",""+1)
            startActivity(intent)
        }*/









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

    fun showAddToCartButton(show:Boolean){
        binding?.homeProDetailsAddCartBtn?.isVisible=show //when show =true; item showed
    }
}