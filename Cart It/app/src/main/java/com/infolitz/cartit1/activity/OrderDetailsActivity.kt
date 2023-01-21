package com.infolitz.cartit1.activity

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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.infolitz.cartit1.R
import com.infolitz.cartit1.adapters.RecyclerViewAdapterCart
import com.infolitz.cartit1.databinding.ActivityOrderDetailsBinding
import com.infolitz.cartit1.helper.ProductViewModal
import com.infolitz.cartit1.helper.UserSessionManager
import java.text.SimpleDateFormat
import java.util.*


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
        binding.orderDetailsPaymentLayout.priceItemsAmountTv.setText("₹ "+  sumPrice )
        binding.orderDetailsPaymentLayout.priceShippingAmountTv.setText("₹ "+  "0")
        binding.orderDetailsPaymentLayout.priceChargesAmountTv.setText("₹ "+  "0" )

        binding.orderDetailsPaymentLayout.priceTotalAmountTv.setText("₹ "+  sumPrice )


        initOrderDetailsForDB(totalQuantity,totalPrice,nameOfProductList,productIdList,storeIdList)


    }

    private fun initOrderDetailsForDB(totalQuantity:ArrayList<Int>,totalPrice:ArrayList<Double>,nameOfProductList:ArrayList<String>,productIdList:ArrayList<String>,storeIdList:ArrayList<String>) {
        var i=0
        while (i<totalQuantity.size){

            Log.e("initOrderDetailsForDB::", "Total Price::" +totalPrice[i])
            Log.e("initOrderDetailsForDB::", "Product ID::" +productIdList[i])
            Log.e("initOrderDetailsForDB::", "Product Name::" +nameOfProductList[i])
            Log.e("initOrderDetailsForDB::", "Store ID::" +storeIdList[i])
            Log.e("initOrderDetailsForDB::", "Quantity::" +totalQuantity[i])


            cartQuantity1.add(totalQuantity[i])
            priceCartQuantity1.add(totalPrice[i])
            nameOfProductList1.add(nameOfProductList[i])
            productIdList1.add(productIdList[i])
            storeIdList1.add(storeIdList[i])
            i++
        }
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
        var ad=""+userSessionManager.getCustFirstName()+" "+
        userSessionManager.getCustLastName()+", "+
                userSessionManager.getCustStreetAddress()+" "+
                userSessionManager.getCustStreetAddress2()+", "+
                userSessionManager.getCustCity()+", "+
                userSessionManager.getCustState()+", "+
                userSessionManager.getCustPin()

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
                    val price = snapshot.child("price").getValue(Double::class.java)
                    Log.e("TAG", "price ::" + price) //got price
                    val stockCount = snapshot.child("stockCount").getValue(Int::class.java)
                    Log.e("TAG", "stockCount ::" + stockCount) //got stockCount
                    val storeId = snapshot.child("storeId").getValue(String::class.java)
                    Log.e("TAG", "storeId ::" + storeId) //got storeId

                    data.add(
                        ProductViewModal(
                            name!!,
                            productId,
                            description!!,
                            price!!,
                            stockCount!!,
                            storeId!!,
                            imgUrl!!
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
        while (i<cartQuantity1.size) {
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
        }

        callAlertViewSuccess()


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

        // find the radiobutton by returned id
        val radioButton = findViewById<View>(selectedId) as RadioButton
        if (radioButton.getText()=="Pay by UPI"){
            payUsingUpi("10","6282475079@ybl", "Nithin", "purchased for cartIt app")
        }else if(radioButton.getText()=="Cash On Delivery"){
            placeOrder("COD")

        }

        Toast.makeText(
            this,
            radioButton.getText(), Toast.LENGTH_SHORT
        ).show()


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