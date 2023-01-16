package com.infolitz.cartit1.helper

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri

class UserSessionManager(context: Context) {

    private var sharedPreferences: SharedPreferences =
        context.getSharedPreferences("UserSharedPref", Context.MODE_PRIVATE)

    private val isAgentLogedin = "isUserLoggedIn"
    private val mAgentEmail = "userEmail"
    private val mAgentName = "userName"
    private val mAgentImage = "userImage"

    private val mAgentUId = "userUid"
    private val mAgentMobile = "userMobileNumber"

    private val mAgentFirstLogin = "user_first_time"

    private val customerCount = "0"


    private val custFirstName1 ="fname"
    private val custLastName1 ="lname"
    private val custMobile1 ="custmob"
    private val custStreetAddress1 ="cs1"
    private val custStreetAddress21 ="cs2"
    private val custCity1 ="cc"
    private val custState1 ="cs"
    private val custPin1 ="cp"

    fun saveCurrentCustomerData(custFirstName: String?,
                                custLastName: String?,
                                custMobile: String?,
                                custStreetAddress:String?,
                                custStreetAddress2:String?,
                                custCity:String?,
                                custState:String?,
                                custPin:String ) {
        val editor = sharedPreferences.edit()
        editor.putString(custFirstName1, custFirstName)
        editor.putString(custLastName1, custLastName)
        editor.putString(custMobile1, custMobile)
        editor.putString(custStreetAddress1, custStreetAddress)
        editor.putString(custStreetAddress21, custStreetAddress2)
        editor.putString(custCity1, custCity)
        editor.putString(custState1, custState)
        editor.putString(custPin1, custPin)
        editor.apply()
    }
    fun getCustFirstName(): String {
        return sharedPreferences.getString(custFirstName1, "")!!
    }
    fun getCustLastName(): String {
        return sharedPreferences.getString(custLastName1, "")!!
    }
    fun getCustMobile(): String {
        return sharedPreferences.getString(custMobile1, "")!!
    }
    fun getCustStreetAddress(): String {
        return sharedPreferences.getString(custStreetAddress1, "")!!
    }
    fun getCustStreetAddress2(): String {
        return sharedPreferences.getString(custStreetAddress21, "")!!
    }
    fun getCustCity(): String {
        return sharedPreferences.getString(custCity1, "")!!
    }
    fun getCustState(): String {
        return sharedPreferences.getString(custState1, "")!!
    }
    fun getCustPin(): String {
        return sharedPreferences.getString(custPin1, "")!!
    }


    fun setCustomerCount(value: Int) {
        sharedPreferences.edit().putInt(customerCount, value).apply()
    }
    fun getCustomerCount(): Int {
        return sharedPreferences.getInt(customerCount, 0)!!
    }


    fun setAgentUId(value: String) {
        sharedPreferences.edit().putString(mAgentUId, value).apply()
    }
    fun getAgentUId(): String {
        return sharedPreferences.getString(mAgentUId, "")!!
    }
    fun setMobileNumber(Mob: String) {
        sharedPreferences.edit().putString(mAgentMobile, Mob).apply()
    }
    fun getMobileNumber(): String {
        return sharedPreferences.getString(mAgentMobile, "")!!
    }

    fun getAgentEmail(): String {
        return sharedPreferences.getString(mAgentEmail, "")!!
    }
    fun setAgentEmail(uname: String) {
        sharedPreferences.edit().putString(mAgentEmail, uname).apply()
    }

    fun setAgentName(uemail: String) {
        sharedPreferences.edit().putString(mAgentName, uemail).apply()
    }
    fun getAgentName(): String {
        return sharedPreferences.getString(mAgentName, "")!!
    }



    fun getAgentImage(): String {
        return sharedPreferences.getString(mAgentImage, "")!!
    }


    fun getFirstLogin():Boolean {
        return sharedPreferences.getBoolean(mAgentFirstLogin, true)
    }
    fun setFirstLogin() {
        sharedPreferences.edit().putBoolean(mAgentFirstLogin, false).apply()
    }

    fun setIsAgentLoggedIn(value: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(isAgentLogedin, value)
        editor.apply()
    }

    fun getIsAgentLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(isAgentLogedin, false)
    }

    fun clearData(applicationContext: Context?) {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }


}