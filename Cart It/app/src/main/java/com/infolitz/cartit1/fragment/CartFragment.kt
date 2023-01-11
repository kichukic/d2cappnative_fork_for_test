package com.infolitz.cartit1.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.infolitz.cartit1.R
import com.infolitz.cartit1.adapters.RecyclerViewAdapterCart
import com.infolitz.cartit1.databinding.FragmentCartBinding
import com.infolitz.cartit1.helper.ProductViewModal


class CartFragment : Fragment() {

    private var cartBinding: FragmentCartBinding?=null
    lateinit var drawerLayout: DrawerLayout


    lateinit var recyclerView: RecyclerView
    lateinit var itemList: List<ProductViewModal>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        container?.removeAllViews()
        cartBinding = FragmentCartBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment



        initRecyclerView()

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

    private fun initRecyclerView() {
        recyclerView = cartBinding!!.recyclerView

        // this creates a vertical layout Manager
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())

        // ArrayList of class ItemsViewModel
        val data = ArrayList<ProductViewModal>()


        data.add(ProductViewModal("1", R.drawable.ic_user_profile))
        data.add(ProductViewModal("2", R.drawable.ic_user_profile))
        data.add(ProductViewModal("3", R.drawable.ic_user_profile))
        data.add(ProductViewModal("4", R.drawable.ic_user_profile))
        data.add(ProductViewModal("5", R.drawable.ic_user_profile))

        // This will pass the ArrayList to our Adapter
        val adapter1 = RecyclerViewAdapterCart(data)


        recyclerView.adapter =
            adapter1   // Setting the Adapter with the recyclerview


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