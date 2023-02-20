package com.infolitz.cartitinfo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.infolitz.cartitinfo.R
import com.infolitz.cartitinfo.helper.OrderProductModal
import com.infolitz.cartitinfo.helper.UserSessionManager

class RecyclerViewOrdersAdapter(
    val context: Context,
    private var mList: List<OrderProductModal>
) : RecyclerView.Adapter<RecyclerViewOrdersAdapter.ViewHolder>() {

    lateinit var userSessionManager: UserSessionManager
    private lateinit var databaseReference: DatabaseReference

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_order_summary_card, parent, false)

        initSharedPref()
        initializeDbRef()

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val itemsViewModel = mList[position]
        var count = 0
        //quantityCount=checkCartQuantity(ItemsViewModel.productId)


        // sets the image to the imageview from our itemHolder class
        /* holder.imageView.setImageResource(ItemsViewModel.proImgUrl)*/

        // sets the text to the textview from our itemHolder class
        holder.productName.text = itemsViewModel.productName
        holder.orderedDate.text = "Ordered On :"+itemsViewModel.orderPlacedDate
        holder.totalItemsPurchased.text = "" + itemsViewModel.totalItems
        holder.totalPrice.text = "₹ " + itemsViewModel.totalPrice
        holder.orderStatus.text = itemsViewModel.orderStatus
        holder.customerAddress.text = itemsViewModel.addressCust
        holder.customerMobile.text = "Mob :"+itemsViewModel.mobileCustomer

    }


    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }


    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        val productName: TextView = itemView.findViewById(R.id.order_summary_id_tv)
        val orderedDate: TextView = itemView.findViewById(R.id.order_summary_date_tv)
        val totalItemsPurchased: TextView = itemView.findViewById(R.id.order_summary_items_count_tv)
        val totalPrice: TextView = itemView.findViewById(R.id.order_summary_total_amount_tv)
        val orderStatus: TextView = itemView.findViewById(R.id.order_summary_status_value_tv)
        val customerAddress: TextView = itemView.findViewById(R.id.ship_address_customer_details)
        val customerMobile: TextView = itemView.findViewById(R.id.order_customer_number)


    }

    private fun initSharedPref() {
        userSessionManager = UserSessionManager(context)
    }

    private fun initializeDbRef() {
        databaseReference = Firebase.database.reference
    }


}