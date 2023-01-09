package com.infolitz.cartit1.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.system.Os.close
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.infolitz.cartit1.BuildConfig
import com.infolitz.cartit1.R
import com.infolitz.cartit1.databinding.ActivityMainBinding
import com.infolitz.cartit1.fragment.HomeFragment
import com.infolitz.cartit1.fragment.SettingsFragment
import com.infolitz.cartit1.helper.UserSessionManager

class MainActivity : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth

    lateinit var userSessionManager: UserSessionManager
    lateinit var activityMainBinding: ActivityMainBinding
    lateinit var toggle: ActionBarDrawerToggle


    lateinit var drawerLayout:DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        initSharedPref()
        initDrawer()

        initFirebase()
        initUserValues()
    }

    private fun initFirebase() {
        auth = FirebaseAuth.getInstance()
    }


    private fun initDrawer() {
         drawerLayout =findViewById(R.id.drawer_layout_main_activity)
        val navView: NavigationView =findViewById(R.id.nav_view)

        val headerView: View = navView.getHeaderView(0)// for setting the user name
        headerView.findViewById<TextView>(R.id.tv_user_name).text = userSessionManager.getUserName()// for setting the user name
        headerView.findViewById<TextView>(R.id.tv_user_mail).text = userSessionManager.getUserEmail()// for setting the user name

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
                R.id.nav_settings->{Toast.makeText(applicationContext,"Clicked settings",Toast.LENGTH_SHORT).show()
                    replaceFragment(SettingsFragment(),it.title.toString())
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
        Log.e("my_fire","uid:-"+userSessionManager.getUserUId())
    }
    private fun onLogOutClicked() {
        auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
    }
}