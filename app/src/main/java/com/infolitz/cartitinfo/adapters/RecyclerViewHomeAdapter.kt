package com.infolitz.cartitinfo.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.infolitz.cartitinfo.R
import com.infolitz.cartitinfo.activity.ProductDescripActivity
import com.infolitz.cartitinfo.helper.ProductViewModal
import com.infolitz.cartitinfo.helper.getGlideProgress
import kotlin.math.roundToInt

class RecyclerViewHomeAdapter(val context: Context, private var mList: List<ProductViewModal>) : RecyclerView.Adapter<RecyclerViewHomeAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.grid_layout_home, parent, false)

        return ViewHolder(view)
    }
//for search
    fun filterList(filterlist: ArrayList<ProductViewModal>) {
        // below line is to add our filtered
        // list in our course array list.
        mList = filterlist
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged()
    }
    //... for search end

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val itemsViewModel = mList[position]
        var count = 0
        //quantityCount=checkCartQuantity(ItemsViewModel.productId)


        // sets the image to the imageview from our itemHolder class
        /* holder.imageView.setImageResource(ItemsViewModel.proImgUrl)*/

        Glide.with(context).load(itemsViewModel.proImgUrl)
            .placeholder(context.getGlideProgress())
            .error(R.drawable.ic_user_profile).transition(
                DrawableTransitionOptions.withCrossFade()
            ).into(holder.itemImageView)

        // sets the text to the textview from our itemHolder class
        holder.productName.text = itemsViewModel.productName



        holder.productNewPrice.text = "${itemsViewModel.price}"

        holder.priceOfferPercentage.text = "${getOfferPercentage(itemsViewModel.price,itemsViewModel.productOldPrice)}"+"% Off"

        holder.productOldPrice.text = "${itemsViewModel.productOldPrice}"



            holder.cardButton.setOnClickListener{
                val intent = Intent (context, ProductDescripActivity::class.java)
                intent.putExtra("item_id",itemsViewModel.productId)
                intent.putExtra("item_count",""+1)
                context.startActivity(intent)
            }



    }

    private fun getOfferPercentage(price: Double, productOldPrice: Double):Int {
        var percentOffer=0

        percentOffer= 100 - Integer.valueOf(((price/productOldPrice)*100).roundToInt())

        return percentOffer
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {


        val itemImageView: ImageView = itemView.findViewById(R.id.imageView_grid)

        val productName: TextView = itemView.findViewById(R.id.tv_item_name_grid)
        val productNewPrice: TextView = itemView.findViewById(R.id.tv_item_price_new_grid)
        val productOldPrice: TextView = itemView.findViewById(R.id.tv_item_price_grid)
        val priceOfferPercentage: TextView = itemView.findViewById(R.id.tv_item_offer_price_grid)
        val cardButton: CardView = itemView.findViewById(R.id.card_home)




    }
}