package com.infolitz.cartit1.helper

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri

class UserSessionManager(context: Context) {

    private var sharedPreferences: SharedPreferences =
        context.getSharedPreferences("UserSharedPref", Context.MODE_PRIVATE)

    private val isUserLogedin = "isUserLoggedIn"
    private val mUserEmail = "userEmail"
    private val mUserName = "userName"
    private val mUserImage = "userImage"

    private val mUserUId = "userUid"
    private val mUserMobile = "userMobileNumber"

    private val mUserFirstLogin = "user_first_time"

   /* fun saveUserData(userEmail: String?, userName: String?, userImage: Uri?, userUid:String ) {
        val editor = sharedPreferences.edit()
        editor.putString(mUserEmail, userEmail)
        editor.putString(mUserName, userName)
        editor.putString(mUserImage, userImage?.toString())
//        editor.putString(mUserUId, userUid)
        editor.apply()
    }*/

    fun setUserUId(value: String) {
        sharedPreferences.edit().putString(mUserUId, value).apply()
    }
    fun getUserUId(): String {
        return sharedPreferences.getString(mUserUId, "")!!
    }
    fun setMobileNumber(Mob: String) {
        sharedPreferences.edit().putString(mUserMobile, Mob).apply()
    }
    fun getMobileNumber(): String {
        return sharedPreferences.getString(mUserMobile, "")!!
    }

    fun getUserEmail(): String {
        return sharedPreferences.getString(mUserEmail, "")!!
    }
    fun setUserEmail(uname: String) {
        sharedPreferences.edit().putString(mUserName, uname).apply()
    }

    fun setUserName(uemail: String) {
        sharedPreferences.edit().putString(mUserEmail, uemail).apply()
    }
    fun getUserName(): String {
        return sharedPreferences.getString(mUserName, "")!!
    }



    fun getUserImage(): String {
        return sharedPreferences.getString(mUserImage, "")!!
    }


    fun getFirstLogin():Boolean {
        return sharedPreferences.getBoolean(mUserFirstLogin, true)
    }
    fun setFirstLogin() {
        sharedPreferences.edit().putBoolean(mUserFirstLogin, false).apply()
    }

    fun setIsUserLoggedIn(value: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(isUserLogedin, value)
        editor.apply()
    }

    fun getIsUserLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(isUserLogedin, false)
    }

    fun clearData(applicationContext: Context?) {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }


}