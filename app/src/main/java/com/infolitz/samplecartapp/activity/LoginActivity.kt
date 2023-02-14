package com.infolitz.samplecartapp.activity

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.*
import com.google.firebase.database.DatabaseReference
import com.infolitz.samplecartapp.databinding.ActivityLoginBinding
import com.infolitz.samplecartapp.helper.UserSessionManager
import com.infolitz.samplecartapp.helper.RetrofitService
import com.infolitz.samplecartapp.modals.agentAuth.ModalAgentLogin
import com.infolitz.samplecartapp.repo.AgentLoginRepository
import com.infolitz.samplecartapp.viewModel.agentAuth.AgentLoginViewModel
import com.infolitz.samplecartapp.viewModel.agentAuth.AgentLoginViewModelFactory


class LoginActivity : AppCompatActivity() {
    private lateinit var activityLoginBinding: ActivityLoginBinding

    //for phone_number_auth
    private lateinit var auth: FirebaseAuth
    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    //for phone_number_auth..//close

    //for timer
    var cTimer: CountDownTimer? = null
    //for shared pref
    private lateinit var userSessionManager: UserSessionManager

    //for firebase
    private lateinit var databaseReference: DatabaseReference
    private var uId = ""

    //for retrofit
    private val retrofitService = RetrofitService.getInstance()

    lateinit var viewModel: AgentLoginViewModel
    var profileReq = mutableListOf<ModalAgentLogin>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initSharedPref() //initializing session
        activityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)
        if (userSessionManager.getIsAgentLoggedIn()) {
            val intent = Intent(this, MainActivity::class.java) //if already logged go to main activity
            startActivity(intent)
        }else{
            setContentView(activityLoginBinding.root)
        }

        if (supportActionBar != null) {
            supportActionBar?.hide(); //to hide actionbar
        }

//        setContentView(R.layout.activity_login)
        Log.e("my_fire", "main run")


        initAccessToken()
        initClicks()

    }
    private fun initSharedPref() {
        userSessionManager = UserSessionManager(this)
    }


/*

    private fun runTimerForOTP() {
        activityLoginBinding.rlResendOtp.visibility = View.VISIBLE //visible resend button
        activityLoginBinding.llEtEnterOtp.visibility=View.VISIBLE
        activityLoginBinding.loaderLayout.loaderFrameLayout.visibility=View.GONE //loader disabling
        activityLoginBinding.tvResendOtp.isEnabled= true
        cTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                activityLoginBinding.tvOtpTimer.setText("00:" + millisUntilFinished / 1000+"sec")
                // logic to set the EditText could go here
            }

            override fun onFinish() {
                activityLoginBinding.tvOtpTimer.setText("00:00")
            }
        }//.start()

        cTimer?.start()

     */
/*   //cancel timer if available //code to cancel timer
        if(cTimer!=null)
            cTimer?.cancel()*//*


        //rest of codes... shortcut
     */
/*   activityLoginBinding.tvResendOtp.isEnabled=false//resend button disabled
        activityLoginBinding.rlResendOtp.visibility = View.INVISIBLE//resend button disabled
        activityLoginBinding.llEtEnterOtp.visibility=View.GONE*//*


    }
*/

    private fun initClicks() {


        activityLoginBinding.btSendOtp.setOnClickListener {
           /* startPhoneNumberVerification("+91" + *//*"1234567890"*//*activityLoginBinding.etAgentEmail.text)
            activityLoginBinding.loaderLayout.loaderFrameLayout.visibility=View.VISIBLE*/

          /*  userSessionManager.setAgentUId( auth.uid.toString()) // write to shared pref
            userSessionManager.setMobileNumber("+91" +activityLoginBinding.etAgentEmail.text) // write to shared*/

            Log.e("LoginActivity","email::"+activityLoginBinding.etAgentEmail.text.toString())
            Log.e("LoginActivity","Password::"+activityLoginBinding.etPassword.text.toString())

           /* val intent = Intent(this, UpdateProfileActivity::class.java)
            intent.putExtra("mobile_number", "12354567890")
            startActivity(intent)*/

            observeResponse(activityLoginBinding.etAgentEmail.text.toString(), activityLoginBinding.etPassword.text.toString())
        }

        activityLoginBinding.btVerifyOtp.setOnClickListener {
            activityLoginBinding.loaderLayout.loaderFrameLayout.visibility=View.VISIBLE //loader disabling

        }
        activityLoginBinding.tvResendOtp.setOnClickListener {

        }
        activityLoginBinding.loginSignupTextView.setOnClickListener {

            val intent = Intent(this, UpdateProfileActivity::class.java)
            intent.putExtra("mobile_number", "12354567890")
            startActivity(intent)
        }
    }




    override fun onStart() {
        super.onStart()

    }


    companion object {
        private const val TAG = "PhoneAuthActivity"
    }


    private fun initAccessToken() {
        viewModel = ViewModelProvider(
            this,
            AgentLoginViewModelFactory(AgentLoginRepository(retrofitService))
        ).get(
            AgentLoginViewModel::class.java
        )
    }

    private fun observeResponse(userEmail: String?, userPassword: String?) {
        viewModel.dataList.observe(this) {
            Log.e("RESPONSE", "DATA : $it")
            if(it != null) {
                if (it.token != "") {
                    Log.e("TOKEN::", it.token.toString())
                    userSessionManager.setAgentToken(it.token.toString())
//                UserSessionManager(this).setToken(it.data?.token)
//                UserSessionManager(this).setUserId(it.data?.Id)
//                gotoProfile(userEmail, userPassword)
                    gotoHomeMain()
                } else {
                    /* MyDialogSheet(this).apply {
                    setTitle(getString(R.string.oops))
                    setMessage(it.message)
                    setPositiveButton(getString(R.string.retry)) {
                        viewModel.getProfile(profileReq)
                    }
                    setNegativeButton(getString(R.string.cancel), null)
                }.show()*/
                    Log.e("response", "no Token")
                }
            }else{
                Toast.makeText(this, "Enter a valid Email or Password", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        viewModel.errorMessage.observe(this) {
            Log.e("ERROR RESPONSE.", it.toString())
            // have to show error layout
            /*MyDialogSheet(this).apply {
                setTitle(getString(R.string.oops))
                setMessage(getString(R.string.server_error))
                setPositiveButton(getString(R.string.retry)) {
                    viewModel.getProfile(profileReq)
                }
                setNegativeButton(getString(R.string.cancel), null)
            }.show()*/
        }
        profileReq.add(
            ModalAgentLogin(
                userEmail!!,
                userPassword!!
            )
        )
        viewModel.getProfile(profileReq)
    }

    private fun gotoHomeMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

}