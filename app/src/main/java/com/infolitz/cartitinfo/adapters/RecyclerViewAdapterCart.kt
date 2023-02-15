package com.infolitz.cartitinfo.adapters

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
import com.infolitz.cartitinfo.R
import com.infolitz.cartitinfo.activity.LoginActivity
import com.infolitz.cartitinfo.activity.OrderDetailsActivity
import com.infolitz.cartitinfo.activity.ProductDescripActivity
import com.infolitz.cartitinfo.fragment.CartFragment
import com.infolitz.cartitinfo.helper.ProductViewModal
import com.infolitz.cartitinfo.helper.UserSessionManager
import com.infolitz.cartitinfo.helper.getGlideProgress
import kotlin.math.roundToInt

class RecyclerViewAdapterCart(val context: Context, private var mList: ArrayList<ProductViewModal>) :
        RecyclerView.Adapter<RecyclerViewAdapterCart.ViewHolder>() {

    lateinit var userSessionManager: UserSessionManager
    private lateinit var databaseReference: DatabaseReference

    lateinit var cartQuantity:ArrayList<Int> //= arrayListOf() list of quantity
    lateinit var priceCartQuantity:ArrayList<Double> //= arrayListOf() list of price of product multiplied
    lateinit var nameOfProductList:ArrayList<String>
    lateinit var productIdList:ArrayList<String>
    lateinit var storeIdList:ArrayList<String>

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_cart_data, parent, false)

        cartQuantity= ArrayList<Int>()
        priceCartQuantity=ArrayList<Double>()
        nameOfProductList=ArrayList<String>()
        productIdList=ArrayList<String>()
        storeIdList=ArrayList<String>()
   /*     sumCartQuantity= arrayListOf<Int>()
        priceCartQuantity=arrayListOf<Double>()*/


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
        nameOfProductList.add(itemsViewModel.productName.toString()) //quantity taken to array list
        productIdList.add(itemsViewModel.productId.toString()) //quantity taken to array list
        storeIdList.add(itemsViewModel.storeId.toString()) //quantity taken to array list

        holder.productNewPrice.text = "${itemsViewModel.price}"

        holder.priceOfferPercentage.text = "${getOfferPercentage(itemsViewModel.price,itemsViewModel.productOldPrice)}"+"% Off"

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

                cartQuantity.add(count.toInt()) //quantity taken to array list
                priceCartQuantity.add(itemsViewModel.price * cartQuantity[position])//
                if (context is OrderDetailsActivity) { //made to call the function in OrderDetailsActivity
                    (context as OrderDetailsActivity).initPaymentDetails(cartQuantity,priceCartQuantity,nameOfProductList,productIdList,storeIdList)
                    Log.e("OrderDetails activity","cleared success")
                }

            } else {
                Log.e("TAG", task.exception!!.message!!) //Don't ignore potential errors!

            }
        }
        holder.plusImageButton.contentDescription = "Add quantity of "+itemsViewModel.productName
        holder.minusImageButton.contentDescription = "Reduce quantity of "+itemsViewModel.productName
        holder.cartDeleteButton.contentDescription = "Delete "+itemsViewModel.productName+" from cart"
        holder.plusImageButton.setOnClickListener {
            count += 1
            holder.quantity.text = "$count"
            databaseReference.child("Agents").child(userSessionManager.getAgentUId()).child("cart")
                .child(itemsViewModel.productId).child("quantity").setValue(count)


            //
            cartQuantity[position]= cartQuantity[position] + 1
            priceCartQuantity[position]=itemsViewModel.price * cartQuantity[position]//
            if (context is OrderDetailsActivity) { //made to call the function in OrderDetailsActivity
                (context as OrderDetailsActivity).initPaymentDetails(cartQuantity,priceCartQuantity,nameOfProductList,productIdList,storeIdList)
                Log.e("OrderDetails activity","cleared success")
            }

        }
        holder.minusImageButton.setOnClickListener {
            if (count>1) {
                count -= 1
                holder.quantity.text = "$count"
                databaseReference.child("Agents").child(userSessionManager.getAgentUId())
                    .child("cart")
                    .child(itemsViewModel.productId).child("quantity")
                    .setValue(count) //update to firebase

                cartQuantity[position]= cartQuantity[position] - 1
                priceCartQuantity[position]=itemsViewModel.price * cartQuantity[position]//
                if (context is OrderDetailsActivity) { //made to call the function in OrderDetailsActivity
                    (context as OrderDetailsActivity).initPaymentDetails(cartQuantity,priceCartQuantity,nameOfProductList,productIdList,storeIdList)
                    Log.e("OrderDetails activity","cleared success")
                }
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
            mList.removeAt(position)
            this.notifyDataSetChanged()

            /*this.mList

            mList.remove(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount)
            setData(this)*/

            /*if (context is OrderDetailsActivity) { //made to call the function in OrderDetailsActivity
                (context as OrderDetailsActivity).initAllProductID()
                Log.e("OrderDetails activity","cleared success")
            }else
            *//*if (context is CartFragment)*//* {
                Log.e("CartFragment activity","cleared success")
                mList.drop(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, itemCount)
//                notifyItemChanged(position)
                this.notifyDataSetChanged()
            }
*/
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
        val productNewPrice: TextView = itemView.findViewById(R.id.tv_price_old_cart_re)
        val priceOfferPercentage: TextView = itemView.findViewById(R.id.tv_offer_cart_re)
        val quantity: TextView = itemView.findViewById(R.id.tv_cart_product_quantity_cart_re)
        val plusImageButton: ImageButton = itemView.findViewById(R.id.cart_product_plus_btn)
        val minusImageButton: ImageButton = itemView.findViewById(R.id.cart_product_minus_btn)
        val cardButton: CardView = itemView.findViewById(R.id.card_view_cart_re)
        val cartDeleteButton: ImageButton = itemView.findViewById(R.id.image_button_cart_product_delete_btn_re)
    }

    fun setData(data: ArrayList<ProductViewModal>) {

        mList = data
        notifyDataSetChanged()
        // where this.data is the recyclerView's dataset you are
        // setting in adapter=new Adapter(this,db.getData());
    }
    private fun getOfferPercentage(price: Double, productOldPrice: Double):Int {
        var percentOffer=0

        percentOffer= 100 - Integer.valueOf(((price/productOldPrice)*100).roundToInt())

        return percentOffer
    }
}