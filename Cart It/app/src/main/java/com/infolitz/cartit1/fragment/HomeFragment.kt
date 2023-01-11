package com.infolitz.cartit1.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.infolitz.cartit1.R
import com.infolitz.cartit1.activity.ProductDescripActivity
import com.infolitz.cartit1.adapters.GridViewAdapterHome
import com.infolitz.cartit1.databinding.FragmentHomeBinding
import com.infolitz.cartit1.helper.ProductViewModal


class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null

    lateinit var gridView: GridView
    lateinit var itemList: List<ProductViewModal>

    lateinit var textViewOldPrice: TextView
    lateinit var textViewNewPrice: TextView
    lateinit var searchView: SearchView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        container?.removeAllViews()
        binding = FragmentHomeBinding.inflate(inflater, container, false)



        initSearch()




        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
//                onExitClicked()
                requireActivity().finish()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), callback)

        // Inflate the layout for this fragment
        return binding?.root
    }

    private fun initSearch() {
        gridView = binding!!.gridView
        itemList = ArrayList<ProductViewModal>()


        itemList = itemList + ProductViewModal("1", R.drawable.ic_user_profile)
        itemList = itemList + ProductViewModal("2", R.drawable.ic_user_profile)
        itemList = itemList + ProductViewModal("3", R.drawable.ic_user_profile)
        itemList = itemList + ProductViewModal("4", R.drawable.ic_user_profile)
        itemList = itemList + ProductViewModal("5", R.drawable.ic_user_profile)

        // on below line we are initializing our course adapter
        // and passing course list and context.
        val itemAdapter = GridViewAdapterHome(itemList = itemList, requireActivity())


        gridView.adapter = itemAdapter  // on below line we are setting adapter to our grid view.


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

        gridView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            Toast.makeText(
                requireActivity(), itemList[position].itemName + " selected",
                Toast.LENGTH_SHORT
            ).show()

            val intent = Intent (activity, ProductDescripActivity::class.java)
            intent.putExtra("item_id",itemList[position].itemName)
            startActivity(intent)
        }

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
}