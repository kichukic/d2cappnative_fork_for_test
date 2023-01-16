package com.infolitz.cartit1.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.infolitz.cartit1.R
import com.infolitz.cartit1.activity.ProductDescripActivity
import com.infolitz.cartit1.helper.ProductViewModal
import com.infolitz.cartit1.helper.UserSessionManager
import com.infolitz.cartit1.helper.getGlideProgress

class RecyclerViewAdapterCart(val context: Context, private var mList: List<ProductViewModal>) :
        RecyclerView.Adapter<RecyclerViewAdapterCart.ViewHolder>() {

    lateinit var userSessionManager: UserSessionManager
    private lateinit var databaseReference: DatabaseReference

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_cart_data, parent, false)

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
        var count = 0
        //quantityCount=checkCartQuantity(ItemsViewModel.productId)


        // sets the image to the imageview from our itemHolder class
        /* holder.imageView.setImageResource(ItemsViewModel.proImgUrl)*/

        Glide.with(context).load(itemsViewModel.proImgUrl)
            .placeholder(context.getGlideProgress())
            .error(R.drawable.ic_user_profile).transition(
                DrawableTransitionOptions.withCrossFade()
            ).into(holder.imageView)

        // sets the text to the textview from our itemHolder class
        holder.productName.text = itemsViewModel.productName
        holder.productPrice.text = "${itemsViewModel.price}"


        //for quantity count
        val lngRef =
            databaseReference.child("Agents").child(userSessionManager.getAgentUId()).child("cart")
                .child(itemsViewModel.productId)
        lngRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                for (ds in snapshot.children) {
                    if (ds.key.toString() == "quantity") {
                        holder.quantity.text = "${ds.getValue(Int::class.java)!!}" //setting the count value
                        count = ds.getValue(Int::class.java)!!
                    }
                }

            } else {
                Log.e("TAG", task.exception!!.message!!) //Don't ignore potential errors!
            }
        }

        holder.plusImageButton.setOnClickListener {
            count += 1
            holder.quantity.text = "$count"
            databaseReference.child("Agents").child(userSessionManager.getAgentUId()).child("cart")
                .child(itemsViewModel.productId).child("quantity").setValue(count)

        }
        holder.minusImageButton.setOnClickListener {
            if (count>1) {
                count -= 1
                holder.quantity.text = "$count"
                databaseReference.child("Agents").child(userSessionManager.getAgentUId())
                    .child("cart")
                    .child(itemsViewModel.productId).child("quantity")
                    .setValue(count) //update to firebase
            }

        }
        holder.cardButton.setOnClickListener{
            val intent = Intent (context, ProductDescripActivity::class.java)
            intent.putExtra("item_id",itemsViewModel.productId)
            intent.putExtra("item_count",""+count)
            context.startActivity(intent)
        }
        holder.cartDeleteButton.setOnClickListener{
            databaseReference.child("Agents").child(userSessionManager.getAgentUId())
                .child("cart")
                .child(itemsViewModel.productId).removeValue()

            this.mList

//            mList.remove(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount)

        }


    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.img_view_cart_re)
        val productName: TextView = itemView.findViewById(R.id.tv_product_name_cart_re)
        val productPrice: TextView = itemView.findViewById(R.id.tv_price_old_cart_re)
        val quantity: TextView = itemView.findViewById(R.id.tv_cart_product_quantity_cart_re)
        val plusImageButton: ImageButton = itemView.findViewById(R.id.cart_product_plus_btn)
        val minusImageButton: ImageButton = itemView.findViewById(R.id.cart_product_minus_btn)
        val cardButton: CardView = itemView.findViewById(R.id.card_view_cart_re)
        val cartDeleteButton: ImageButton = itemView.findViewById(R.id.image_button_cart_product_delete_btn_re)
    }

    fun setData(data: List<ProductViewModal>) {
        mList = data
        notifyDataSetChanged()
        // where this.data is the recyclerView's dataset you are
        // setting in adapter=new Adapter(this,db.getData());
    }
}