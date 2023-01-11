package com.infolitz.cartit1.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.internal.ContextUtils.getActivity
import com.infolitz.cartit1.R
import com.infolitz.cartit1.databinding.ActivityProductDescripBinding

class ProductDescripActivity : AppCompatActivity() {

    lateinit var productDescripBinding: ActivityProductDescripBinding
    lateinit var productId :String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_descrip)

        productDescripBinding = ActivityProductDescripBinding.inflate(layoutInflater)
        setContentView(productDescripBinding.root)




        productId= intent.getStringExtra("item_id")!!
        Log.e("the key is",productId)
        supportActionBar!!.title = productId // setting the tittle


    }
}