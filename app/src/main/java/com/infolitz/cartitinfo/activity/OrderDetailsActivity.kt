package com.infolitz.cartitinfo.activity

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.infolitz.cartitinfo.R
import com.infolitz.cartitinfo.adapters.RecyclerViewAdapterCart
import com.infolitz.cartitinfo.adapters.RecyclerViewHomeAdapter
import com.infolitz.cartitinfo.databinding.ActivityOrderDetailsBinding
import com.infolitz.cartitinfo.helper.ProductViewModal
import com.infolitz.cartitinfo.helper.UserSessionManager
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


class OrderDetailsActivity : AppCompatActivity() {

    lateinit var binding: ActivityOrderDetailsBinding
    lateinit var userSessionManager: UserSessionManager
    private lateinit var databaseReference: DatabaseReference

    lateinit var recyclerView: RecyclerView
    lateinit var data: ArrayList<ProductViewModal>

    var totalQuantity:Double=0.0
    var totalPrice:Double=0.0

    val UPI_PAYMENT = 0

    //forInitDb order details
    lateinit var cartQuantity1:ArrayList<Int> //= arrayListOf() list of quantity
    lateinit var priceCartQuantity1:ArrayList<Double> //= arrayListOf() list of price of product multiplied
    lateinit var nameOfProductList1:ArrayList<String>
    lateinit var productIdList1:ArrayList<String>
    lateinit var storeIdList1:ArrayList<String>

    lateinit var modeOfPay :String
    //

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_order_details)
        binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (supportActionBar != null) {
            supportActionBar?.hide(); //to hide actionbar
        }

        data = ArrayList<ProductViewModal>()

        //for db ordering list
        cartQuantity1= ArrayList<Int>()
        priceCartQuantity1=ArrayList<Double>()
        nameOfProductList1=ArrayList<String>()
        productIdList1=ArrayList<String>()
        storeIdList1=ArrayList<String>()
        ///....

        initSharedPref()
        initializeDbRef()
        initAllProductID()

        initAddressCard()

        initClicks()
    }


    fun initPaymentDetails(totalQuantity:ArrayList<Int>,totalPrice:ArrayList<Double>,nameOfProductList:ArrayList<String>,productIdList:ArrayList<String>,storeIdList:ArrayList<String>) {
        Log.e("Position ::","changed the tagg")

//        calculateTotalPrice(totalQuantity,totalPrice)//to calculate total price and quantity
        var sumQuantity=0
        var sumPrice=0.0
        var i=0
        while (i<totalQuantity.size){
            sumQuantity += totalQuantity[i]
            sumPrice += totalPrice[i]
            i++
        }


//        var price=((totalPrice * 100.0).roundToInt() / 100.0) // 295.33
        Log.e("in OrderDetailsActiviy:","call from recycler view")
        binding.orderDetailsPaymentLayout.priceItemsLabelTv.setText("Items("+  sumQuantity +")")
        binding.orderDetailsPaymentLayout.priceItemsAmountTv.setText("₹ "+  ((sumPrice* 100.0).roundToInt() / 100.0))
        binding.orderDetailsPaymentLayout.priceShippingAmountTv.setText("₹ "+  "0")
        binding.orderDetailsPaymentLayout.priceChargesAmountTv.setText("₹ "+  "0" )

        binding.orderDetailsPaymentLayout.priceTotalAmountTv.setText("₹ "+  ((sumPrice* 100.0).roundToInt() / 100.0) )


        Log.e("initOrderDetailsForDB::", "Total Price::" +totalPrice)
        Log.e("initOrderDetailsForDB::", "Product ID::" +productIdList)
        Log.e("initOrderDetailsForDB::", "Product Name::" +nameOfProductList)
        Log.e("initOrderDetailsForDB::", "Store ID::" +storeIdList)
        Log.e("initOrderDetailsForDB::", "Quantity::" +totalQuantity)





        initOrderDetailsForDB(totalQuantity,totalPrice,nameOfProductList,productIdList,storeIdList)


    }

    private fun initOrderDetailsForDB(totalQuantity:ArrayList<Int>,totalPrice:ArrayList<Double>,nameOfProductList:ArrayList<String>,productIdList:ArrayList<String>,storeIdList:ArrayList<String>) {
        var i=0
        /*while (i<totalQuantity.size){

            Log.e("initOrderDetailsForDB::", "Total Price::" +totalPrice[0])
            Log.e("initOrderDetailsForDB::", "Product ID::" +productIdList[0])
            Log.e("initOrderDetailsForDB::", "Product Name::" +nameOfProductList[0])
            Log.e("initOrderDetailsForDB::", "Store ID::" +storeIdList[0])
            Log.e("initOrderDetailsForDB::", "Quantity::" +totalQuantity[0])


            cartQuantity1.add(totalQuantity[0])
            priceCartQuantity1.add(totalPrice[0])
            nameOfProductList1.add(nameOfProductList[0])
            productIdList1.add(productIdList[0])
            storeIdList1.add(storeIdList[0])
            i++
        }*/
    }


    private fun initAddressCard() {

        binding.orderDetailsShippingAddLayout.shipDateValueTv.text = getDateShipping()

        Log.e("address)))",getCustAddress())
        binding.orderDetailsShippingAddLayout.shipAddValueTv.text = getCustAddress()
        binding.orderDetailsShippingAddLayout.shipCurrStatusValueTv.text= "In Cart"
    }

    private fun getCustAddress(): String {

       /* custFirstName: String,
        custLastName: String,
        custMobile: String,
        custStreetAddress:String,
        custStreetAddress2:String,
        custCity:String,
        custState:String,
        custPin:String*/

        Log.e("---first--",userSessionManager.getCustFirstName())
        Log.e("---last--",userSessionManager.getCustLastName())
        Log.e("---street--",userSessionManager.getCustStreetAddress())
        var ad="Please enter the Customer Address"
        if (userSessionManager.getCustMobile()!="") {
             ad = "" + userSessionManager.getCustFirstName() + " " +
                    userSessionManager.getCustLastName() + ", " +
                    userSessionManager.getCustStreetAddress() + " " +
                    userSessionManager.getCustStreetAddress2() + ", " +
                    userSessionManager.getCustCity() + ", " +
                    userSessionManager.getCustState() + ", " +
                    userSessionManager.getCustPin()
        }

        return ad
    }

    private fun getDateShipping(): String {


        val formatter = SimpleDateFormat("EEE, MMM dd, yyyy")
        val date = Date()
        val current = formatter.format(date)

        return ""+current
    }
    private fun getDateorder(): String {


        val formatter = SimpleDateFormat("EEE, dd-MM-yyyy, HH:mm:ss")
        val date = Date()
        val current = formatter.format(date)

        return ""+current
    }

    private fun initClicks() {
        binding.orderChangeStatusBtn.setOnClickListener {
            var intent = Intent(this, EditAddressActivity::class.java)
            intent.putExtra("custNumber",userSessionManager.getCustMobile())
            startActivity(intent)
        }
        binding.continuePaymentButton.setOnClickListener{





            startPaymentFun()
        }
        binding.askPaymentMethodLayout.radioGroupPayment.setOnCheckedChangeListener { group, checkedId ->
             /*if (R.id.radio_upi_payment == checkedId){
                 "Upi"
             }else{
                 "COD"
             }*/
            //on click on button listened
        }


    }

    private fun callAlertViewSuccess() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.layout_order_placed_success,null)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Order Status")
        val alertDialog = builder.show() //to show dialog
        val button = dialogView.findViewById<Button>(R.id.back_to_home_btn)
        button.setOnClickListener{
            alertDialog.dismiss()
            callIntendToMainActivity()
        }


    }

    private fun callIntendToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }


    fun initAllProductID() {//for getting the productids under the present user
         data.clear()//making the arraylist empty
        val lngRef =
            databaseReference.child("Agents").child(userSessionManager.getAgentUId()).child("cart")

        lngRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val productIdList = ArrayList<String>()
                val snapshot = task.result
                Log.e("TAG123", "productid::" + snapshot.value)

                if (snapshot.value==null){//when the total products in cart are empty
                    binding.orderDetailsPaymentLayout.priceItemsLabelTv.setText("Items(0)")
                    binding.orderDetailsPaymentLayout.priceItemsAmountTv.setText("₹ 0" )
                    binding.orderDetailsPaymentLayout.priceShippingAmountTv.setText("₹ 0")
                    binding.orderDetailsPaymentLayout.priceChargesAmountTv.setText("₹ 0" )

                    binding.orderDetailsPaymentLayout.priceTotalAmountTv.setText("₹ 0" )
                }
                for (ds in snapshot.children) {
                    Log.e("TAG12", "productid::" + ds.key.toString())
                    productIdList.add(ds.key.toString())
                }
                initRecyclerView(productIdList)

            } else {
                Log.e("TAG", task.exception!!.message!!) //Don't ignore potential errors!
            }
        }
    }

    //for loading the products in cart
    private fun initRecyclerView(productIdList: ArrayList<String>) {
        recyclerView = binding!!.orderDetailsProRecyclerView

        // this creates a vertical layout Manager
        recyclerView.layoutManager = LinearLayoutManager(this)

        // ArrayList of class ItemsViewModel

        for (product in productIdList) {
            val lngRef = databaseReference.child("Products").child(product)
            lngRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val snapshot = task.result

                    Log.e(
                        "TAG",
                        "productss___-----: ${snapshot.value}"
                    )// getting values of Products
                    /*  for (ds1 in snapshot.children) {*/
                    val productId = snapshot.key.toString()
                    Log.e("TAG", "product id ::" + productId) //got product id

                   /* val availPin = snapshot.child("availPin").getValue(Int::class.java)
                    Log.e("TAG", "availPin ::" + availPin) //got availPin*/

                    val description = snapshot.child("description").getValue(String::class.java)
                    Log.e("TAG", "description ::" + description) //got description
                    val imgUrl = snapshot.child("imgUrl").getValue(String::class.java)
                    Log.e("TAG", "imgUrl ::" + imgUrl) //got imgUrl
                    val name = snapshot.child("name").getValue(String::class.java)
                    Log.e("TAG", "name ::" + name) //got name
                    val olDprice = snapshot.child("price").getValue(Double::class.java)
                    Log.e("TAG", "price ::" + olDprice) //got old price

                    val price = snapshot.child("offerPrice").getValue(Double::class.java)
                    Log.e("TAG", "price ::" + price) //got new price

                    val stockCount = snapshot.child("stockCount").getValue(Int::class.java)
                    Log.e("TAG", "stockCount ::" + stockCount) //got stockCount
                    val storeId = snapshot.child("storeId").getValue(String::class.java)
                    Log.e("TAG", "storeId ::" + storeId) //got storeId

                    data.add(
                        ProductViewModal(
                            name!!,
                            productId,
                            description!!,
                            price!!,//new price
                            stockCount!!,
                            storeId!!,
                            imgUrl!!,
                            olDprice!!
                        )
                    )

                    val adapter1 = RecyclerViewAdapterCart(this, data/*,quantity*/)
                    recyclerView.adapter =
                        adapter1   // Setting the Adapter with the recyclerview

                } else {
                    Log.e("TAG", task.exception!!.message!!) //Don't ignore potential errors!
                }
            }

        }


    }

    private fun initSharedPref() {
        userSessionManager = UserSessionManager(this)
    }

    //firebase.....
    private fun initializeDbRef() {
        databaseReference = Firebase.database.reference
    }

    private fun placeOrder(paymentMethod:String) {
        //paymentMethod is COD"cash on delivery"
        //paymentMethod is PBU"pay by UPI"

        if (paymentMethod=="COD"){
            modeOfPay="COD"
        }else if (paymentMethod=="PBU"){
            modeOfPay="PBU"
        }

        var i=0
        if(userSessionManager.getCustMobile()==""){
            Toast.makeText(
                this,
                "Please enter the customer Address",
                Toast.LENGTH_SHORT
            ).show()
        }else{
      /*  while (i<cartQuantity1.size) {
            Log.e("order::", "Address::" +getCustAddress())
            Log.e("order::", "Mode of pay::" + modeOfPay)
            Log.e("order::", "Order Status::" + "Order Placed")
            Log.e("order::", "Order Placed Date::" + getDateShipping())
            Log.e("order::", "Total Price::" +priceCartQuantity1[i])
            Log.e("order::", "Product ID::" +productIdList1[i])
            Log.e("order::", "Product Name::" +nameOfProductList1[i])
            Log.e("order::", "Store ID::" +storeIdList1[i])
            Log.e("order::", "customer ID::" + "ct" + userSessionManager.getCustMobile())
            Log.e("order::", "Quantity::" +cartQuantity1[i])

            Log.e("order::", "orderID:: oId" +callUniqueId())


            var orderReference = databaseReference.child("Orders").child("oId"+callUniqueId())

            orderReference.child("address").setValue(getCustAddress()) //increment customercount in agent db
            orderReference.child("modeOfPay").setValue(modeOfPay) //increment customercount in agent db
            orderReference.child("status").setValue("Order Placed") //increment customercount in agent db
            orderReference.child("placedDate").setValue(getDateorder()) //increment customercount in agent db
            orderReference.child("totalPrice").setValue(priceCartQuantity1[i]) //increment customercount in agent db
            orderReference.child("productId").setValue(productIdList1[i]) //increment customercount in agent db
            orderReference.child("productName").setValue(nameOfProductList1[i]) //increment customercount in agent db
            orderReference.child("storeId").setValue(storeIdList1[i]) //increment customercount in agent db
            orderReference.child("customerId").setValue("ct"+userSessionManager.getCustMobile()) //increment customercount in agent db
            orderReference.child("quantity").setValue(cartQuantity1[i]) //increment customercount in agent db
            orderReference.child("customerMobile").setValue(userSessionManager.getCustMobile()) //increment customercount in agent db
            i++
        }*/
            getTheOrderPlaced()

        callAlertViewSuccess()

        }

    }

    private fun getTheOrderPlaced() {
        Log.e("orderDetailsActivity",modeOfPay.toString())

        //

        val lngRef =
            databaseReference.child("Agents").child(userSessionManager.getAgentUId()).child("cart")

        lngRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val productIdList = ArrayList<String>()
                val productQuantityList = ArrayList<String>()
                val snapshot = task.result
                Log.e("TAG","productid::"+snapshot.value) //storeid::{s1=sIdsId1673406243}
                for (ds in snapshot.children) {
                    Log.e("TAG","product_id::"+ds.key.toString())
                    Log.e("TAG","quantity_::"+ds.child("quantity").getValue(Long::class.java))
                    productIdList.add(ds.key.toString())
                    productQuantityList.add(""+ds.child("quantity").getValue(Long::class.java))
                    /* if (ds.key.toString() == "quantity") {
                         holder.quantity.text = "${ds.getValue(Int::class.java)!!}"
                         count = ds.getValue(Int::class.java)!!
                     }*/
                }
                setDBdata(productIdList,productQuantityList)

            } else {
                Log.e("TAG.", task.exception!!.message!!) //Don't ignore potential errors!
            }
        }
    }

    private fun setDBdata(productIdList: ArrayList<String>, productQuantityList: ArrayList<String>) {
        var i = -1
        for (productId in productIdList){

            Log.e("product id is2::",""+productId)
            val lngRef = databaseReference.child("Products").child(productId)
            lngRef.get().addOnCompleteListener { task ->
                i++
                Log.e("i is::",""+i)
                Log.e("product quantity is::",""+productQuantityList[i])
                Log.e("product id is::",""+productIdList[i])
                if (task.isSuccessful) {
                    val snapshot = task.result
                    val productId11= snapshot.key.toString()
//                    Log.e("TAG","product id ::"+productId11) //got product id
                    val description = snapshot.child("description").getValue(String::class.java)
//                    Log.e("TAG","description ::"+description) //got description
                    val imgUrl= snapshot.child("imgUrl").getValue(String::class.java)
//                    Log.e("TAG","imgUrl ::"+imgUrl) //got imgUrl
                    val name = snapshot.child("name").getValue(String::class.java)
//                    Log.e("TAG","name ::"+name) //got name
                    val price = snapshot.child("price").getValue(Double::class.java)
//                    Log.e("TAG","price ::"+price) //got price
                    val stockCount = snapshot.child("stockCount").getValue(Int::class.java)
//                    Log.e("TAG","stockCount ::"+stockCount) //got stockCount
                    val storeId = snapshot.child("storeId").getValue(String::class.java)
//                    Log.e("TAG","storeId ::"+storeId) //got storeId


                    val total_price= price?.toFloat()!! * productQuantityList[i].toFloat()
                    val roundoffPrice = (total_price * 100.0).roundToInt() / 100.0



                    Log.e("order::", "Address::" +getCustAddress())
                    Log.e("order::", "Mode of pay::" + modeOfPay)
                    Log.e("order::", "Order Status::" + "Order Placed")
                    Log.e("order::", "Order Placed Date::" + getDateShipping())
                    Log.e("order::", "Total Price::" +roundoffPrice)
                    Log.e("order::", "Product ID::" +productId11)
                    Log.e("order::", "Product Name::" +name)
                    Log.e("order::", "Store ID::" +storeId)
                    Log.e("order::", "customer ID::" + "ct" + userSessionManager.getCustMobile())
                    Log.e("order::", "Quantity::" +productQuantityList[i])

                    Log.e("order::", "orderID:: oId" +i+callUniqueId())

                    var orderReference = databaseReference.child("Orders").child("oId_"+i+callUniqueId())

                    orderReference.child("address").setValue(getCustAddress()) //increment customercount in agent db
                    orderReference.child("modeOfPay").setValue(modeOfPay) //increment customercount in agent db
                    orderReference.child("status").setValue("Order Placed") //increment customercount in agent db
                    orderReference.child("placedDate").setValue(getDateorder()) //increment customercount in agent db
                    orderReference.child("totalPrice").setValue(roundoffPrice) //increment customercount in agent db
                    orderReference.child("productId").setValue(productId11) //increment customercount in agent db
                    orderReference.child("productName").setValue(name) //increment customercount in agent db
                    orderReference.child("storeId").setValue(storeId) //increment customercount in agent db
                    orderReference.child("customerId").setValue("ct"+userSessionManager.getCustMobile()) //increment customercount in agent db
                    orderReference.child("quantity").setValue(productQuantityList[i]) //increment customercount in agent db
                    orderReference.child("customerMobile").setValue(userSessionManager.getCustMobile()) //increment customercount in agent db
                } else {
                    Log.e("TAG", task.exception!!.message!!) //Don't ignore potential errors!
                }
            }

        }
    }

    private fun callUniqueId():String {
        val tsLong = System.currentTimeMillis() / 1000
        val ts = tsLong.toString()
        return ts
    }


    //payment function...

    private fun startPaymentFun() {

        // get selected radio button from radioGroup
        val selectedId: Int = binding.askPaymentMethodLayout.radioGroupPayment.checkedRadioButtonId //gets the checked radiobutton id

        Log.e("selected one",selectedId.toString())
        // find the radiobutton by returned id
        if(selectedId != -1) { //checks if the radio button clicked
            val radioButton = findViewById<View>(selectedId) as RadioButton

            if (radioButton.getText() == "Pay by UPI") {
                payUsingUpi("10", "6282475079@ybl", "Nithin", "purchased for cartIt app")
            } else if (radioButton.getText() == "Cash On Delivery") {
                placeOrder("COD")

            }

           /* Toast.makeText(
                this,
                radioButton.getText(), Toast.LENGTH_SHORT
            ).show()*/
        }else{
            Toast.makeText(
                this,
               "Please select a payment method", Toast.LENGTH_SHORT
            ).show()
        }


    }



    fun payUsingUpi(amount: String?, upiId: String?, name: String?, note: String?) {

        //amount:the amount
        //upiId:the receivers
        //name:the
        //note:the about the payment
        val uri: Uri = Uri.parse("upi://pay").buildUpon()
            .appendQueryParameter("pa", upiId)
            .appendQueryParameter("pn", name)
            .appendQueryParameter("tn", note)
            .appendQueryParameter("am", amount)
            .appendQueryParameter("cu", "INR")
            .build()
        val upiPayIntent = Intent(Intent.ACTION_VIEW)
        upiPayIntent.data = uri

        // will always show a dialog to user to choose an app
        val chooser = Intent.createChooser(upiPayIntent, "Pay with")

        // check if intent resolves
        if (null != chooser.resolveActivity(packageManager)) {
            startActivityForResult(chooser, UPI_PAYMENT)
        } else {
            Toast.makeText(
                this,
                "No UPI app found, please install one to continue",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            UPI_PAYMENT -> if (RESULT_OK == resultCode || resultCode == 11) {
                if (data != null) {
                    val trxt = data.getStringExtra("response")
                    //Log.d("UPI", "onActivityResult: " + trxt);
                    val dataList: ArrayList<String?> = ArrayList()
                    dataList.add(trxt)
                    upiPaymentDataOperation(dataList)
                } else {
                    //Log.d("UPI", "onActivityResult: " + "Return data is null");
                    val dataList: ArrayList<String?> = ArrayList()
                    dataList.add("nothing")
                    upiPaymentDataOperation(dataList)
                }
            } else {
                //Log.d("UPI", "onActivityResult: " + "Return data is null"); //when user simply back without payment
                val dataList: ArrayList<String?> = ArrayList()
                dataList.add("nothing")
                upiPaymentDataOperation(dataList)
            }
        }
    }

    private fun upiPaymentDataOperation(data: ArrayList<String?>) {
        if (isConnectionAvailable(this)) {
            var str = data[0]
            //Log.d("UPIPAY", "upiPaymentDataOperation: "+str);
            var paymentCancel = ""
            if (str == null) str = "discard"
            var status = ""
            var approvalRefNo = ""
            val response = str.split("&").toTypedArray()
            for (i in response.indices) {
                val equalStr = response[i].split("=").toTypedArray()
                if (equalStr.size >= 2) {
                    if (equalStr[0].lowercase(Locale.getDefault()) == "Status".lowercase(Locale.getDefault())) {
                        status = equalStr[1].lowercase(Locale.getDefault())
                    } else if (equalStr[0].lowercase(Locale.getDefault()) == "ApprovalRefNo".lowercase(
                            Locale.getDefault()
                        ) || equalStr[0].lowercase(Locale.getDefault()) == "txnRef".lowercase(
                            Locale.getDefault()
                        )
                    ) {
                        approvalRefNo = equalStr[1]
                    }
                } else {
                    paymentCancel = "Payment cancelled by user."
                }
            }
            if (status == "success") {

                placeOrder("PBU") //payment by UPI
                //Code to handle successful transaction here.
                Toast.makeText(this, "Transaction successful.", Toast.LENGTH_SHORT).show()
                // Log.d("UPI", "responseStr: "+approvalRefNo);
                Toast.makeText(
                    this,
                    "YOUR ORDER HAS BEEN PLACED\n THANK YOU AND ORDER AGAIN",
                    Toast.LENGTH_LONG
                ).show()
            } else if ("Payment cancelled by user." == paymentCancel) {
                Toast.makeText(this, "Payment cancelled by user.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Transaction failed.Please try again", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            Toast.makeText(
                this,
                "Internet connection is not available. Please check and try again",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun isConnectionAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val netInfo = connectivityManager.activeNetworkInfo
            if (netInfo != null && netInfo.isConnected
                && netInfo.isConnectedOrConnecting
                && netInfo.isAvailable
            ) {
                return true
            }
        }
        return false
    }
}