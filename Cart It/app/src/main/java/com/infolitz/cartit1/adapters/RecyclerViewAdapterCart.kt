package com.infolitz.cartit1.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.infolitz.cartit1.R
import com.infolitz.cartit1.helper.ProductViewModal

 class RecyclerViewAdapterCart(private val mList: List<ProductViewModal>) : RecyclerView.Adapter<RecyclerViewAdapterCart.ViewHolder>() {

     // create new views
     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
         // inflates the card_view_design view
         // that is used to hold list item
         val view = LayoutInflater.from(parent.context)
             .inflate(R.layout.recyclerview_cart_data, parent, false)

         return ViewHolder(view)
     }

     // binds the list items to a view
     override fun onBindViewHolder(holder: ViewHolder, position: Int) {

         val ItemsViewModel = mList[position]

         // sets the image to the imageview from our itemHolder class
         holder.imageView.setImageResource(ItemsViewModel.itemImg)

         // sets the text to the textview from our itemHolder class
         holder.textView.text = ItemsViewModel.itemName

     }

     // return the number of the items in the list
     override fun getItemCount(): Int {
         return mList.size
     }

     // Holds the views for adding it to image and text
     class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
         val imageView: ImageView = itemView.findViewById(R.id.img_view_cart_re)
         val textView: TextView = itemView.findViewById(R.id.tv_product_name_cart_re)
     }
 }