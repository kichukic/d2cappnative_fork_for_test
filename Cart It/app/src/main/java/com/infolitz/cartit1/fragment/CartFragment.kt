package com.infolitz.cartit1.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.infolitz.cartit1.adapters.RecyclerViewAdapterCart
import com.infolitz.cartit1.databinding.FragmentCartBinding
import com.infolitz.cartit1.helper.ProductViewModal
import com.infolitz.cartit1.helper.UserSessionManager


class CartFragment : Fragment() {

    private var cartBinding: FragmentCartBinding?=null
    lateinit var drawerLayout: DrawerLayout

    private lateinit var databaseReference: DatabaseReference

    lateinit var recyclerView: RecyclerView
    lateinit var itemList: List<ProductViewModal>
    lateinit var data: ArrayList<ProductViewModal>
    lateinit var userSessionManager: UserSessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        container?.removeAllViews()
        cartBinding = FragmentCartBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment

         data = ArrayList<ProductViewModal>()

        initSharedPref()
        initializeDbRef()
        initAllProductID()


        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) { //back button
            override fun handleOnBackPressed() {

                /*drawerLayout =requireActivity().findViewById(R.id.drawer_layout_main_activity)
                val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.fragmentLayout,HomeFragment())
                fragmentTransaction.commit()
                drawerLayout.closeDrawers()*/
//                onExitClicked()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), callback)
        return cartBinding?.root
    }
    private fun initSharedPref() {
        userSessionManager = UserSessionManager(requireActivity())
    }

    private fun initAllProductID() {//for getting the productids under the present user
        val lngRef =
            databaseReference.child("Agents").child(userSessionManager.getAgentUId()).child("cart")

        lngRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val productIdList = ArrayList<String>()
                val snapshot = task.result
                Log.e("TAG","productid::"+snapshot.value)
                for (ds in snapshot.children) {
                    Log.e("TAG","productid::"+ds.key.toString())
                    productIdList.add(ds.key.toString())
                   /* if (ds.key.toString() == "quantity") {
                        holder.quantity.text = "${ds.getValue(Int::class.java)!!}"
                        count = ds.getValue(Int::class.java)!!
                    }*/
                }
                initRecyclerView(productIdList)

            } else {
                Log.e("TAG", task.exception!!.message!!) //Don't ignore potential errors!
            }
        }
    }


    //firebase.....
    private fun initializeDbRef() {
        databaseReference = Firebase.database.reference
    }


    //firebase.....close

    private fun initRecyclerView(productIdList: ArrayList<String>) {
        recyclerView = cartBinding!!.recyclerView

        // this creates a vertical layout Manager
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())

        // ArrayList of class ItemsViewModel

        for(product in productIdList) {
            val lngRef = databaseReference.child("Products").child(product)
            lngRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {


                    val snapshot = task.result

//                val username = snapshot.child("username").getValue(String::class.java)
                Log.e("TAG", "productss___-----: ${snapshot.value}")// getting values of Products
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
                        /*val imgUrl = ds.child("ImageUrl").getValue(String::class.java)*/
                   /* Log.e("imgurl", imgUrl.toString())*/
                  /*  arrayOfLang.add(LanguagesDashboard(ds.key.toString(), imgUrl))*/

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
//                    itemList = itemList + ProductViewModal(name!!,productId,availPin!!,description!!,price!!,stockCount!!, storeId!!,R.drawable.img_curry_powder_cumin)

                    //}

                    val adapter1 = RecyclerViewAdapterCart(requireActivity(), data/*,quantity*/)
                    recyclerView.adapter =
                        adapter1   // Setting the Adapter with the recyclerview

                } else {
                    Log.e("TAG", task.exception!!.message!!) //Don't ignore potential errors!
                }
            }

        }




//        data.add(ProductViewModal("1","pid123",671531,"abcdd",20.0,100, "sid","R.drawable.img_curry_powder_cumin"))
//        data.add(ProductViewModal("1","pid123",671531,"abcdd",20.0,100, "sid",R.drawable.img_curry_powder_cumin))
      /*  data.add(ProductViewModal("2", R.drawable.ic_user_profile))
        data.add(ProductViewModal("3", R.drawable.ic_user_profile))
        data.add(ProductViewModal("4", R.drawable.ic_user_profile))
        data.add(ProductViewModal("5", R.drawable.ic_user_profile))*/

        // This will pass the ArrayList to our Adapter
//        val adapter1 = RecyclerViewAdapterCart(data)



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

    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CartFragment().apply {
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cartBinding=null
    }
    override fun onResume() {
        super.onResume()
    }
}