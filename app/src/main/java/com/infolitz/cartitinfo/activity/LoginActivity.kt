package com.infolitz.cartitinfo.activity

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.infolitz.cartitinfo.databinding.ActivityLoginBinding
import com.infolitz.cartitinfo.helper.UserSessionManager
import com.infolitz.cartitinfo.R
import java.util.concurrent.TimeUnit


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


        otpToEachET()

        initFirebase()
        initializeDbRef()

        initClicks()

    }
    private fun initSharedPref() {
        userSessionManager = UserSessionManager(this)
    }

    private fun initFirebase() {

        auth = Firebase.auth // Initialize Firebase Auth
        activityLoginBinding.tvResendOtp.isEnabled=false//resend button disabled
        activityLoginBinding.rlResendOtp.visibility = View.INVISIBLE//resend button disabled
        activityLoginBinding.llEtEnterOtp.visibility=View.GONE

        // Initialize phone auth callbacks
        // [START phone_auth_callbacks]
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.e(TAG, "onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)
            }

            //triggered if otp failed
            override fun onVerificationFailed(e: FirebaseException) {
                Log.w(TAG, "onVerificationFailed", e)
                if (e is FirebaseAuthInvalidCredentialsException) {
                } else if (e is FirebaseTooManyRequestsException) {
                }
                // Show a message and update the UI
            }

            //triggered if the otp is send
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.e(TAG, "onCodeSent:$verificationId")
                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token
                activityLoginBinding.btSendOtp.visibility = View.GONE
                activityLoginBinding.btVerifyOtp.visibility = View.VISIBLE
                runTimerForOTP()

            }
        }
    }

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
    }

    private fun initClicks() {

//        activityLoginBinding.etMobileNumber.setHintTextColor(getResources().getColor(R.color.dark_gray));
        activityLoginBinding.btSendOtp.setOnClickListener {
            startPhoneNumberVerification("+91" + /*"1234567890"*/activityLoginBinding.etMobileNumber.text)
            activityLoginBinding.loaderLayout.loaderFrameLayout.visibility=View.VISIBLE
        }

        activityLoginBinding.btVerifyOtp.setOnClickListener {
            activityLoginBinding.loaderLayout.loaderFrameLayout.visibility=View.VISIBLE //loader disabling
            verifyOTP()
        }
        activityLoginBinding.tvResendOtp.setOnClickListener {
            resendVerificationCode("+91" + activityLoginBinding.etMobileNumber.text,resendToken)//resend otp
            activityLoginBinding.loaderLayout.loaderFrameLayout.visibility=View.VISIBLE
        }
    }

    private fun verifyOTP() {
        //collect otp from all the edit texts
        val typedOTP =
            (activityLoginBinding.etOtp1.text.toString() +
                    activityLoginBinding.etOtp2.text.toString() +
                    activityLoginBinding.etOtp3.text.toString() +
                    activityLoginBinding.etOtp4.text.toString() +
                    activityLoginBinding.etOtp5.text.toString() +
                    activityLoginBinding.etOtp6.text.toString())

        if (typedOTP.isNotEmpty()) {
            if (typedOTP.length == 6) {
                val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(
                    storedVerificationId!!, typedOTP
                )
//                    progressBar.visibility = View.VISIBLE
                signInWithPhoneAuthCredential(credential)
            } else {
                Toast.makeText(this, "Please Enter Correct OTP", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Please Enter OTP", Toast.LENGTH_SHORT).show()
        }


    }


    private fun otpToEachET() {
        // to move the curser to the next edittext box
        activityLoginBinding.etOtp1.addTextChangedListener(
            GenericTextWatcher(
                activityLoginBinding.etOtp1,
                activityLoginBinding.etOtp2
            )
        )
        activityLoginBinding.etOtp2.addTextChangedListener(
            GenericTextWatcher(
                activityLoginBinding.etOtp2,
                activityLoginBinding.etOtp3
            )
        )
        activityLoginBinding.etOtp3.addTextChangedListener(
            GenericTextWatcher(
                activityLoginBinding.etOtp3,
                activityLoginBinding.etOtp4
            )
        )
        activityLoginBinding.etOtp4.addTextChangedListener(
            GenericTextWatcher(
                activityLoginBinding.etOtp4,
                activityLoginBinding.etOtp5
            )
        )
        activityLoginBinding.etOtp5.addTextChangedListener(
            GenericTextWatcher(
                activityLoginBinding.etOtp5,
                activityLoginBinding.etOtp6
            )
        )
        activityLoginBinding.etOtp6.addTextChangedListener(
            GenericTextWatcher(
                activityLoginBinding.etOtp6,
                null
            )
        )

        //go to the previous on delete
        activityLoginBinding.etOtp1.setOnKeyListener(
            GenericKeyEvent(
                activityLoginBinding.etOtp1,
                null
            )
        )
        activityLoginBinding.etOtp2.setOnKeyListener(
            GenericKeyEvent(
                activityLoginBinding.etOtp2,
                activityLoginBinding.etOtp1
            )
        )
        activityLoginBinding.etOtp3.setOnKeyListener(
            GenericKeyEvent(
                activityLoginBinding.etOtp3,
                activityLoginBinding.etOtp2
            )
        )
        activityLoginBinding.etOtp4.setOnKeyListener(
            GenericKeyEvent(
                activityLoginBinding.etOtp4,
                activityLoginBinding.etOtp3
            )
        )
        activityLoginBinding.etOtp5.setOnKeyListener(
            GenericKeyEvent(
                activityLoginBinding.etOtp5,
                activityLoginBinding.etOtp4
            )
        )
        activityLoginBinding.etOtp6.setOnKeyListener(
            GenericKeyEvent(
                activityLoginBinding.etOtp6,
                activityLoginBinding.etOtp5
            )
        )


    }

    class GenericKeyEvent internal constructor(
        private val currentView: EditText,
        private val previousView: EditText?
    ) : View.OnKeyListener {
        override fun onKey(p0: View?, keyCode: Int, event: KeyEvent?): Boolean {
            if (event!!.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && currentView.id != R.id.et_otp1 && currentView.text.isEmpty()) {
                //If current is empty then previous EditText's number will also be deleted
                previousView?.text = null
                previousView?.requestFocus()
                return true
            }
            return false
        }


    }

    class GenericTextWatcher internal constructor(
        private val currentView: View,
        private val nextView: View?
    ) :
        TextWatcher {
        override fun afterTextChanged(editable: Editable) { // TODO Auto-generated method stub
            val text = editable.toString()
            when (currentView.id) {
                R.id.et_otp1 -> if (text.length == 1) nextView!!.requestFocus()
                R.id.et_otp2 -> if (text.length == 1) nextView!!.requestFocus()
                R.id.et_otp3 -> if (text.length == 1) nextView!!.requestFocus()
                R.id.et_otp4 -> if (text.length == 1) nextView!!.requestFocus()
                R.id.et_otp5 -> if (text.length == 1) nextView!!.requestFocus()
            }
        }

        override fun beforeTextChanged(
            arg0: CharSequence,
            arg1: Int,
            arg2: Int,
            arg3: Int
        ) { // TODO Auto-generated method stub
        }

        override fun onTextChanged(
            arg0: CharSequence,
            arg1: Int,
            arg2: Int,
            arg3: Int
        ) { // TODO Auto-generated method stub
        }

    }


    //for firebase.....
    // [START on_start_check_user]
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }
    // [END on_start_check_user]

    private fun startPhoneNumberVerification(phoneNumber: String) {
        //cancel timer if available
        if(cTimer!=null)
            cTimer?.cancel()

        // [START start_phone_auth]
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

        Log.e("my_fire", "reached ")
        // [END start_phone_auth]
    }

    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        // [START verify_with_code]
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        // [END verify_with_code]
    }

    // [START resend_verification]
    private fun resendVerificationCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?
    ) {
        //cancel timer if available
        if(cTimer!=null)
            cTimer?.cancel()

        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
        if (token != null) {
            optionsBuilder.setForceResendingToken(token) // callback's ForceResendingToken
        }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }
    // [END resend_verification]

    // [START sign_in_with_phone]
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    val user = task.result?.user
//                    val user = Firebase.auth.currentUser
                    auth = Firebase.auth
                    userSessionManager.setAgentUId( auth.uid.toString()) // write to shared pref
                    userSessionManager.setMobileNumber("+91" +activityLoginBinding.etMobileNumber.text) // write to shared
                    writeDataToFirebase() //write to firebase


                    userSessionManager.setIsAgentLoggedIn(true)
                    val intent = Intent(this, UpdateProfileActivity::class.java)
                    intent.putExtra("mobile_number", userSessionManager.getMobileNumber())
                    startActivity(intent)

                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }
    // [END sign_in_with_phone]

    private fun updateUI(user: FirebaseUser? = auth.currentUser) {

    }

    companion object {
        private const val TAG = "PhoneAuthActivity"
    }

    private fun writeDataToFirebase() {
        uId=userSessionManager.getAgentUId()
        var userReference = databaseReference.child("Agents").child(uId)
        userReference.child("id").setValue(userSessionManager.getAgentUId())
        userReference.child("mobile").setValue(userSessionManager.getMobileNumber())
    }

    //for firebase
    private fun initializeDbRef() {
        databaseReference = Firebase.database.reference
    }
    //for firebase.....close
}