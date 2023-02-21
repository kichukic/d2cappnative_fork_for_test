package com.infolitz.cartitinfo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.infolitz.cartitinfo.R
import com.infolitz.cartitinfo.modals.ProductViewModal
import com.infolitz.cartitinfo.helper.getGlideProgress

internal class GridViewAdapterHome(
    private val itemList: List<ProductViewModal>,
    private val context: Context
) :
    BaseAdapter() {
    private var layoutInflater: LayoutInflater? = null
    private lateinit var productName: TextView
    private lateinit var productPrice: TextView
    private lateinit var itemImageView: ImageView

    var data = ArrayList<ProductViewModal>()

    // below method is use to return the count of course list
    override fun getCount(): Int {
        return itemList.size
    }

    // below function is use to return the item of grid view.
    override fun getItem(position: Int): Any? {
        return null
    }

    // below function is use to return item id of grid view.
    override fun getItemId(position: Int): Long {
        return 0
    }

    // in below function we are getting individual item of grid view.
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var convertView = convertView
        if (layoutInflater == null) {
            layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }
        if (convertView == null) {
            convertView = layoutInflater!!.inflate(R.layout.grid_layout_home, null)
        }

        itemImageView = convertView!!.findViewById(R.id.imageView_grid)
        productName = convertView!!.findViewById(R.id.tv_item_name_grid)
        productPrice = convertView!!.findViewById(R.id.tv_item_price_new_grid)

        /*itemImageView.setImageResource(itemList.get(position).proImgUrl)*/

        Glide.with(context).load(itemList.get(position).proImgUrl)
            .placeholder(context.getGlideProgress())
            .error(R.drawable.ic_user_profile).transition(
                DrawableTransitionOptions.withCrossFade()
            ).into(itemImageView)


        productName.setText(itemList.get(position).productName)
        productPrice.setText(itemList.get(position).price.toString())

        return convertView
    }

}
