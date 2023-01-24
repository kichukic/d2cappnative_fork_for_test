package com.infolitz.cartit1.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuItemCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.infolitz.cartit1.R
import com.infolitz.cartit1.databinding.ActivityMainBinding
import com.infolitz.cartit1.fragment.CartFragment
import com.infolitz.cartit1.fragment.HomeFragment
import com.infolitz.cartit1.helper.UserSessionManager


class MainActivity : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth

    lateinit var userSessionManager: UserSessionManager
    lateinit var activityMainBinding: ActivityMainBinding
    lateinit var toggle: ActionBarDrawerToggle

    //for firebase
    private lateinit var databaseReference: DatabaseReference
    private var uId = ""

    lateinit var drawerLayout:DrawerLayout
    lateinit var tv_text_set_pin:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        initSharedPref()
        initDrawer()

        initFirebase()
        initializeDbRef()

        initUserValues()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_set_action_bar_pincode, menu)


        val item = menu.findItem(R.id.set_pin)
        MenuItemCompat.setActionView(item, R.layout.actionbar_set_pin_layout)
        val notifCount = MenuItemCompat.getActionView(item) as LinearLayout

        tv_text_set_pin = notifCount.findViewById<View>(R.id.actionbar_notifcation_textview) as TextView
        val relativeLayout = notifCount.findViewById<View>(R.id.relativeLayout_setPin_actionBar) as LinearLayout

        if(userSessionManager.getAgentPinCode()==0) {
            tv_text_set_pin.text = "Set the pin"
        }else{
            tv_text_set_pin.text=""+userSessionManager.getAgentPinCode()
        }

        /*tv.setOnClickListener{

        }*/
        relativeLayout.setOnClickListener{
            Toast.makeText(applicationContext,"Clicked set pin",Toast.LENGTH_SHORT).show()
            callAlertViewSuccess()
        }

       /* val setpinLayout = menu.findItem(R.id.set_pin).actionView as RelativeLayout //the layout where text
        val tv = setpinLayout.findViewById(R.id.actionbar_notifcation_textview) as TextView
        tv.text = "12"*/

        return true
    }
    private fun initFirebase() {
        auth = FirebaseAuth.getInstance()
    }


    private fun initDrawer() {
         drawerLayout =findViewById(R.id.drawer_layout_main_activity)
        val navView: NavigationView =findViewById(R.id.nav_view)

        val headerView: View = navView.getHeaderView(0)// for setting the user name
        headerView.findViewById<TextView>(R.id.tv_user_name).text = userSessionManager.getAgentName()// for setting the user name
        headerView.findViewById<TextView>(R.id.tv_user_mail).text = userSessionManager.getAgentEmail()// for setting the user name

        replaceFragment(HomeFragment(),"Home") //on load set the home default

        toggle=ActionBarDrawerToggle(this,drawerLayout,R.string.nav_open,R.string.nav_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navView.setNavigationItemSelectedListener {
            it.isChecked=true
            when(it.itemId){

                R.id.nav_home->{Toast.makeText(applicationContext,"Clicked home",Toast.LENGTH_SHORT).show()
                                replaceFragment(HomeFragment(),it.title.toString())
                                }
                R.id.nav_cart->{Toast.makeText(applicationContext,"Clicked cart",Toast.LENGTH_SHORT).show()
                    replaceFragment(CartFragment(),it.title.toString())
                }
                R.id.nav_logout->{Toast.makeText(applicationContext,"Clicked Logout",Toast.LENGTH_SHORT).show()
                    onLogOutClicked()
                }
               /* R.id.set_pin->{

                }*/
            }
            true
        }

    }
   /* override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                Toast.makeText(this, "click..!!", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }*/
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
       val id = item.itemId
       if (id == R.id.set_pin) {
           Toast.makeText(applicationContext,"Clicked set pin",Toast.LENGTH_SHORT).show()
           return true
       }
        if (toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    private fun replaceFragment(fragment: Fragment, tittle:String){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentLayout,fragment)
        fragmentTransaction.commit()
        drawerLayout.closeDrawers()
        setTitle(tittle)
    }


    private fun initSharedPref() {
        userSessionManager = UserSessionManager(this)
    }


    private fun initUserValues() {
        Log.e("my_fire","uid:-"+userSessionManager.getAgentUId())


        val lngRef =
            databaseReference.child("Agents").child(userSessionManager.getAgentUId()).child("customerCount")

        lngRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                if(snapshot.value !=null) { //check if the key customerCount Exist
                    Log.e("TAG","customer count::"+snapshot.value)
                    var cnt=snapshot.value.toString()
                    userSessionManager.setCustomerCount(cnt.toInt())
                    /*for (ds in snapshot.children) {
                        Log.e("TAG", "customer ::" + ds.key.toString())
//                    productIdList.add(ds.key.toString())
                        *//* if (ds.key.toString() == "quantity") {
                         holder.quantity.text = "${ds.getValue(Int::class.java)!!}"
                         count = ds.getValue(Int::class.java)!!
                     }*//*
                    }*/
                }else{
                    lngRef.setValue(0) //if customerCount null then setting its value as 0
                }

            } else {
                Log.e("TAG customer", task.exception!!.message!!) //Don't ignore potential errors!

            }
        }



    }
    //for firebase
    private fun initializeDbRef() {
        databaseReference = Firebase.database.reference
    }
    private fun onLogOutClicked() {
        userSessionManager.setIsAgentLoggedIn(false)
        auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
    }

    //alert to enter the pin code
    private fun callAlertViewSuccess() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.layout_popup_set_pin,null)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Set the PinCode")
        val alertDialog = builder.show() //to show dialog
        val button = dialogView.findViewById<Button>(R.id.set_current_pincode_btn)
        val etPinNumber = dialogView.findViewById<TextInputEditText>(R.id.et_pincode_number)
        val currentPin = dialogView.findViewById<TextView>(R.id.enter_pin_label_tv)
        currentPin.text="Current pincode: "+userSessionManager.getAgentPinCode().toString() // setting current pincode value
        button.setOnClickListener{
            val pinNumber=etPinNumber.text.toString().toInt()
            userSessionManager.setAgentPinCode(pinNumber)
            writeDataToFirebase(""+pinNumber)
            initDrawer()
            alertDialog.dismiss()
        }


    }

    private fun writeDataToFirebase(s: String) {
        uId=userSessionManager.getAgentUId()
        var userReference = databaseReference.child("Agents").child(uId)
        userReference.child("agentPinCode").setValue(s)
        tv_text_set_pin.text=""+s
    }
}