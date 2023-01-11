package com.infolitz.cartit1.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.infolitz.cartit1.R
import com.infolitz.cartit1.helper.ProductViewModal

internal class GridViewAdapterHome(
    private val itemList: List<ProductViewModal>,
    private val context: Context
) :
    BaseAdapter() {
    private var layoutInflater: LayoutInflater? = null
    private lateinit var itemTextView: TextView
    private lateinit var itemImageView: ImageView

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
        itemTextView = convertView!!.findViewById(R.id.tv_item_name_grid)

        itemImageView.setImageResource(itemList.get(position).itemImg)
        itemTextView.setText(itemList.get(position).itemName)

        return convertView
    }
}
