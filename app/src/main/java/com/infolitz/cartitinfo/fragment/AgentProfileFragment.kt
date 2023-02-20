package com.infolitz.cartitinfo.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.infolitz.cartitinfo.R
import com.infolitz.cartitinfo.adapters.RecyclerViewOrdersAdapter
import com.infolitz.cartitinfo.databinding.FragmentAgentProfileBinding
import com.infolitz.cartitinfo.databinding.FragmentOrdersBinding
import com.infolitz.cartitinfo.helper.OrderProductModal
import com.infolitz.cartitinfo.helper.UserSessionManager

class AgentProfileFragment : Fragment() {

    private var binding: FragmentAgentProfileBinding? = null
    lateinit var userSessionManager: UserSessionManager
    private lateinit var databaseReference: DatabaseReference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        container?.removeAllViews()
        binding = FragmentAgentProfileBinding.inflate(inflater, container, false)

        initSharedPref()
        initializeDbRef()


        setUserData()

        initClicks()


//        initViewWithDBdata()


        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) { //triggered on back button press
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), callback)

        // Inflate the layout for this fragment
        return binding?.root
    }

    private fun setUserData() {
        binding!!.profileEmailTv.text= userSessionManager.getAgentEmail()
        binding!!.profileMobileTv.text= userSessionManager.getMobileNumber()
        binding!!.profileNameTv.text= userSessionManager.getAgentName()
    }


    private fun initClicks() {
        //click the card implementation
    }

    //firebase.....
    private fun initializeDbRef() {
        databaseReference = Firebase.database.reference
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
}