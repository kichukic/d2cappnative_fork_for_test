package com.infolitz.cartit1.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.infolitz.cartit1.R
import com.infolitz.cartit1.databinding.ActivityMainBinding
import com.infolitz.cartit1.fragment.HomeFragment
import com.infolitz.cartit1.fragment.CartFragment
import com.infolitz.cartit1.helper.UserSessionManager

class MainActivity : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth

    lateinit var userSessionManager: UserSessionManager
    lateinit var activityMainBinding: ActivityMainBinding
    lateinit var toggle: ActionBarDrawerToggle

    //for firebase
    private lateinit var databaseReference: DatabaseReference


    lateinit var drawerLayout:DrawerLayout

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
            }
            true
        }

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
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
        auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
    }
}