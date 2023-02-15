package com.infolitz.cartitinfo.adapters

import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
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
import com.infolitz.cartitinfo.activity.ProductDescripActivity
import com.infolitz.cartitinfo.helper.ProductModelHomeFragment
import com.infolitz.cartitinfo.helper.ProductViewModal
import com.infolitz.cartitinfo.helper.UserSessionManager
import com.infolitz.cartitinfo.helper.getGlideProgress
import kotlin.math.roundToInt

class RecyclerViewHomeAdapter(
    val context: Context,
    private val showAddToCartButton: (Boolean) -> Unit,
    private var mList: List<ProductModelHomeFragment>
) : RecyclerView.Adapter<RecyclerViewHomeAdapter.ViewHolder>() {

    private var isEnable = false//for selection for cart
    private var itemSelectedList = mutableListOf<Int>()//for holding selected list
    lateinit var userSessionManager: UserSessionManager
    private lateinit var databaseReference: DatabaseReference

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.grid_layout_home, parent, false)

        initSharedPref()
        initializeDbRef()

        return ViewHolder(view)
    }

    //for search
    fun filterList(filterlist: ArrayList<ProductModelHomeFragment>) {
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

        holder.priceOfferPercentage.text =
            "${getOfferPercentage(itemsViewModel.price, itemsViewModel.productOldPrice)}" + "% Off"

        holder.productOldPrice.text = "${itemsViewModel.productOldPrice}"



        holder.cardButton.setOnClickListener {
           /* val intent = Intent(context, ProductDescripActivity::class.java)
            intent.putExtra("item_id", itemsViewModel.productId)
            intent.putExtra("item_count", "" + 1)
            context.startActivity(intent)*/
            Log.e("tagg:position",": "+position)
            if (itemSelectedList.contains(position)){
//                itemSelectedList.removeAt(position)//if once selected on second click remove it
                itemSelectedList.remove(position)//if once selected on second click remove it
                holder.frameLayout.visibility=View.GONE
                itemsViewModel.selected=false //setting selection false in viewModal class
                if (itemSelectedList.isEmpty()){
                    showAddToCartButton(false) //hiding the add to cart button
                    isEnable=false
                    itemSelectedList.clear()
                }
            }else if (isEnable){
                selectItem(holder,itemsViewModel,position)
            }else if (!isEnable){

                val intent = Intent(context, ProductDescripActivity::class.java)
                intent.putExtra("item_id", itemsViewModel.productId)
                intent.putExtra("item_count", "" + 1)
                context.startActivity(intent)
            }
        }


        holder.cardButton.setOnLongClickListener{ //if the card is long pressed
            selectItem(holder,itemsViewModel,position)
            Log.e("tagg:position",": "+position)

            true
        }




    }

    private fun selectItem(holder: RecyclerViewHomeAdapter.ViewHolder, itemsViewModel: ProductModelHomeFragment, position: Int) {
        isEnable=true
        itemSelectedList.add(position) //adding the positions of item selected
        itemsViewModel.selected=true
        holder.frameLayout.visibility=View.VISIBLE
        showAddToCartButton(true)
    }

    private fun getOfferPercentage(price: Double, productOldPrice: Double): Int {
        var percentOffer = 0
        percentOffer = 100 - Integer.valueOf(((price / productOldPrice) * 100).roundToInt())
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
        val frameLayout: FrameLayout = itemView.findViewById(R.id.frame_layout_click_multi_select)


    }

    private fun initSharedPref() {
        userSessionManager = UserSessionManager(context)
    }
    private fun initializeDbRef() {
        databaseReference = Firebase.database.reference
    }



    fun clearTheSelected() { //add data to db and clear the selected
        if(itemSelectedList.isNotEmpty()){
//            addTODB

            writeToDB(itemSelectedList)
            isEnable=false
            itemSelectedList.clear()
        }
    }

    private fun writeToDB(itemSelectedList: MutableList<Int>) {

        for (position in itemSelectedList ){
            Log.e("Present data",""+mList[position].productId)

            var userReference=databaseReference.child("Agents").child(userSessionManager.getAgentUId())
            userReference.child("cart").child(mList[position].productId).child("orderStatus").setValue("oncart")
            userReference.child("cart").child(mList[position].productId).child("quantity").setValue(1)

        }
    }
}