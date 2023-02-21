package com.infolitz.cartitinfo.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.infolitz.cartitinfo.R
import com.infolitz.cartitinfo.activity.CustomerOrderDetailsActivity
import com.infolitz.cartitinfo.helper.UserSessionManager
import com.infolitz.cartitinfo.modals.CustomerModalForAgentProfile

class RecyclerViewCustomerAdapter(
    val context: Context,
    private var mList: List<CustomerModalForAgentProfile>
) :
    RecyclerView.Adapter<RecyclerViewCustomerAdapter.ViewHolder>() {

    lateinit var userSessionManager: UserSessionManager
    private lateinit var databaseReference: DatabaseReference

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_customers_list, parent, false)

        initSharedPref()
        initializeDbRef()

        return ViewHolder(view)
    }

    private fun initializeDbRef() {
        databaseReference = Firebase.database.reference
    }

    private fun initSharedPref() {
        userSessionManager = UserSessionManager(context)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemsViewModel = mList[position]

        holder.customerMobileNumber.text = itemsViewModel.customerNumber
        holder.customerName.text =  itemsViewModel.customerName

        holder.cardButton.setOnClickListener {


                val intent = Intent(context, CustomerOrderDetailsActivity::class.java)
                intent.putExtra("customer_id", itemsViewModel.customerId)
                context.startActivity(intent)

        }

    }


    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val customerName: TextView = itemView.findViewById(R.id.customer_name_profile)
        val customerMobileNumber: TextView = itemView.findViewById(R.id.customer_mobile_profile)
        val cardButton: CardView = itemView.findViewById(R.id.card_home_customer)
    }

    //for search
    fun filterList(filterlist: ArrayList<CustomerModalForAgentProfile>) {
        // below line is to add our filtered
        // list in our course array list.
        mList = filterlist
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged()
    }
    //... for search end


}