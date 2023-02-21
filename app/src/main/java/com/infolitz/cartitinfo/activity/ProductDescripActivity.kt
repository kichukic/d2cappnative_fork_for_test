package com.infolitz.cartitinfo.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.infolitz.cartitinfo.R
import com.infolitz.cartitinfo.databinding.ActivityProductDescripBinding
import com.infolitz.cartitinfo.helper.UserSessionManager
import com.infolitz.cartitinfo.helper.getGlideProgress

class ProductDescripActivity : AppCompatActivity() {

    lateinit var productDescripBinding: ActivityProductDescripBinding
    lateinit var productId: String
    lateinit var userSessionManager: UserSessionManager
     var quantityCount: Int = 1


    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_descrip)

        productDescripBinding = ActivityProductDescripBinding.inflate(layoutInflater)
        setContentView(productDescripBinding.root)

        /*if (supportActionBar != null) {
            supportActionBar?.hide(); //to hide actionbar
        }*/

        productId = intent.getStringExtra("item_id")!!
        quantityCount = intent.getStringExtra("item_count")!!.toInt()
        Log.e("the key is", productId)




        initSharedPref()
        initializeDbRef()
        initProductdata()
        initClicks()



    }

    private fun initSharedPref() {
        userSessionManager = UserSessionManager(this)
    }

    private fun initClicks() {
        productDescripBinding.tvCartProductQuantityCart.text="${quantityCount}"
        productDescripBinding.cartProductPlusBtn.setOnClickListener{
            quantityCount += 1
            productDescripBinding.tvCartProductQuantityCart.text="${quantityCount}"
        }
        productDescripBinding.cartProductMinusBtn.setOnClickListener{
            if (quantityCount>1){
                quantityCount -= 1
                productDescripBinding.tvCartProductQuantityCart.text="${quantityCount}"
            }
        }

        productDescripBinding.proDetailsAddCartBtn.setOnClickListener{

            addToCart()
            Toast.makeText(applicationContext,"Added to Cart successfully",Toast.LENGTH_LONG).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        productDescripBinding.proDetailsOrderBtn.setOnClickListener{
            addToCart()
            var intent =Intent(this,OrderDetailsActivity::class.java)
            intent.putExtra("passed_from","ProductDescripActivity")
            startActivity(intent)
        }
    }

    private fun addToCart() {

        var userReference=databaseReference.child("Agents").child(userSessionManager.getAgentUId())
        userReference.child("cart").child(productId).child("orderStatus").setValue("oncart")
        userReference.child("cart").child(productId).child("quantity").setValue(quantityCount)

    }

    private fun initializeDbRef() {
        databaseReference = Firebase.database.reference
    }

    private fun initProductdata() {


        val lngRef = databaseReference.child("Products")
        lngRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {

                val snapshot = task.result

//                Log.e("TAG", "productss: ${snapshot.value}")// getting values of Products
                for (ds in snapshot.children) {
                    if (ds.key.toString() == productId) { //checks for our product
                        val productId = ds.key.toString()
                        Log.e("TAG", "product id ::" + productId) //got product id

                        val availPin = ds.child("availPin").getValue(Int::class.java)
                        Log.e("TAG", "availPin ::" + availPin) //got availPin

                        val description = ds.child("description").getValue(String::class.java)
                        Log.e("TAG", "description ::" + description) //got description
                        val imgUrl = ds.child("imgUrl").getValue(String::class.java)
                        Log.e("TAG", "imgUrl ::" + imgUrl) //got imgUrl
                        val name = ds.child("name").getValue(String::class.java)
                        Log.e("TAG", "name ::" + name) //got name

                        val newPrice = ds.child("offerPrice").getValue(Double::class.java)
                        Log.e("TAG","offerPrice ::"+newPrice) //got offerPrice

                        val productOldPrice = ds.child("price").getValue(Double::class.java)
                        Log.e("TAG","price ::"+productOldPrice) //got old price

                        val stockCount = ds.child("stockCount").getValue(Int::class.java)
                        Log.e("TAG", "stockCount ::" + stockCount) //got stockCount
                        val storeId = ds.child("storeId").getValue(String::class.java)
                        Log.e("TAG", "storeId ::" + storeId) //got storeId

                        productDescripBinding.proDetailsTitleTv.text= name //setting product name
                        supportActionBar!!.title = name // setting the tittle
                        productDescripBinding.proDetailsPriceTv.text= "₹$newPrice" //setting price

                        productDescripBinding.proDetailsSpecificationsLabel.text=description

                        Glide.with(this).load(imgUrl)
                            .placeholder(this.getGlideProgress())
                            .error(R.drawable.ic_user_profile).transition(
                                DrawableTransitionOptions.withCrossFade()
                            ).into(productDescripBinding.proImageIv)

                    }
                }

            } else {
                Log.e("TAG", task.exception!!.message!!) //Don't ignore potential errors!
            }
        }


    }
}