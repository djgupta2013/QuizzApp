package com.apps.quizz.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.apps.quizz.HomeActivity
import com.apps.quizz.R
import com.apps.quizz.databinding.ActivityOtpVerificationBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*
import java.util.concurrent.TimeUnit

class OtpVerificationActivity : AppCompatActivity(), View.OnFocusChangeListener, TextWatcher,
    View.OnKeyListener, View.OnClickListener  {
    private lateinit var binding: ActivityOtpVerificationBinding
    private var etDigitCurrent: EditText? = null
    var mobile: String = ""
    private var token: String? = ""
    private var storedVerificationId: String? = null
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    var auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_otp_verification)
        FirebaseApp.initializeApp(this)
        auth.languageCode = Locale.getDefault().language
        initFocusListener()
        initTextChangeListener()
        initKeyListener()
        getDeviceToken()
        setListener()
        mobile = this.intent.getStringExtra("mobile")!!
        Firebase.auth.signOut()
        sendVerificationCode()
    }

    private fun setTimer() {
        object : CountDownTimer(60 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.tvTimer.text = "Retry in 00:" + millisUntilFinished / 1000
            }

            override fun onFinish() {
                binding.tvResendOtp.visibility= View.VISIBLE
            }
        }.start()
    }

    private fun sendVerificationCode() {
       /* PhoneAuthProvider.getInstance().verifyPhoneNumber(
            "+917011164679",
            60,
            TimeUnit.SECONDS,
            this,
            phoneCallbacks
        )
*/
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+917011164679")       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        binding.tvResendOtp.visibility= View.GONE
        setTimer()
    }

   private val  callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
            val code = phoneAuthCredential.smsCode
            signInWithPhoneAuthCredential(phoneAuthCredential)
            Log.d("onVerificationCompleted", "IonVerificationCompleted $code")
            Toast.makeText(this@OtpVerificationActivity, code, Toast.LENGTH_LONG).show()
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w("onVerificationFailed", "onVerificationFailed", e)

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
            } else if (e is FirebaseTooManyRequestsException) {
                Toast.makeText(this@OtpVerificationActivity,"Something went wrong, Please connect with Admin",Toast.LENGTH_LONG).show()
            }

            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d("TAG", "onCodeSent:$verificationId")

            // Save verification ID and resending token so we can use them later
            storedVerificationId = verificationId
            resendToken = token
        }
    }

    private val phoneCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(
                s: String,
                forceResendingToken: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(s, forceResendingToken)
                storedVerificationId = s
                Log.d("onCodeSent", " onCodeSent()")

            }

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                val code = phoneAuthCredential.smsCode
                signInWithPhoneAuthCredential(phoneAuthCredential)
                Log.d("onVerificationCompleted", "IonVerificationCompleted $code")
                Toast.makeText(this@OtpVerificationActivity, code, Toast.LENGTH_LONG).show()
                //verifyCode(code!!)

            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(this@OtpVerificationActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    //callOtpVerifyAPI()
                    Log.e("OTP matched","OTP matched")
                    Log.e("mobile number", it.result?.user?.phoneNumber.toString())
                    startActivity(Intent(this, HomeActivity::class.java))
                    finishAffinity()
                } else {
                    Log.e("OTP not matched","OTP not matched")
                    //utils.simpleAlert(this,"","OTP not matched")
                    /* Toast.makeText(
                         this,
                         "Something went wrong, try again later please.",
                         Toast.LENGTH_LONG).show()*/
                }
            }
    }


    private fun getDeviceToken() {
        /*FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e("", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                token = task.result!!.token

                // Log and toast
                // val msg = getString(R.string.msg_token_fmt, token)
                Log.d("token", token!!)
                //Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
            })*/
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
                R.id.tv_resend_otp -> {
                    //utils.hideKeyboard(binding.tvResendOtp)
                    //callResendOtpAPI()
                    sendVerificationCode()
                }
                R.id.btnOtpVerify -> {
                    //utils.hideKeyboard(binding.btnOtpVerify)

                    if (isValid()) {
                        verifyCode(getOtp()!!)
                        /*if (getOtp() == model!!.otp.toString()) {
                            //ToastObj.message(this, "verify")

                        } else {
                            Toast.makeText(this, "OTP not matched", Toast.LENGTH_SHORT).show()
                        }*/
                    } else {
                        Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun verifyCode(code: String) {
        Log.d("verifyCode", code)
        try {
            val credential =
                PhoneAuthProvider.getCredential(storedVerificationId!!, code)
            signInWithPhoneAuthCredential(credential)
        } catch (e: Exception) {
            Log.d("PhoneAuth_Exc", e.toString())
            Toast.makeText(this, "please try again..", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun setListener() {
        binding.tvResendOtp.setOnClickListener(this)
        binding.btnOtpVerify.setOnClickListener(this)
    }

    private fun setBlankOtp() {
        binding.etDigit1.setText("")
        binding.etDigit2.setText("")
        binding.etDigit3.setText("")
        binding.etDigit4.setText("")
        binding.etDigit5.setText("")
        binding.etDigit6.setText("")
    }

    private fun initFocusListener() {
        binding.etDigit1.onFocusChangeListener = this
        binding.etDigit2.onFocusChangeListener = this
        binding.etDigit3.onFocusChangeListener = this
        binding.etDigit4.onFocusChangeListener = this
        binding.etDigit5.onFocusChangeListener = this
        binding.etDigit6.onFocusChangeListener = this
    }

    private fun initKeyListener() {
        binding.etDigit1.setOnKeyListener(this)
        binding.etDigit2.setOnKeyListener(this)
        binding.etDigit3.setOnKeyListener(this)
        binding.etDigit4.setOnKeyListener(this)
        binding.etDigit5.setOnKeyListener(this)
        binding.etDigit6.setOnKeyListener(this)
    }

    private fun initTextChangeListener() {
        binding.etDigit1.addTextChangedListener(this)
        binding.etDigit2.addTextChangedListener(this)
        binding.etDigit3.addTextChangedListener(this)
        binding.etDigit4.addTextChangedListener(this)
        binding.etDigit5.addTextChangedListener(this)
        binding.etDigit6.addTextChangedListener(this)
    }

    fun getOtp(): String? {
        return makeOtp()
    }

    fun setOtp(otp: String) {
        if (otp.length != 4) return
        binding.etDigit1.setText(otp[0].toString())
        binding.etDigit2.setText(otp[1].toString())
        binding.etDigit3.setText(otp[2].toString())
        binding.etDigit4.setText(otp[3].toString())
    }

    private fun makeOtp(): String? {
        val sb = StringBuilder()
        sb.append(binding.etDigit1.text.toString())
        sb.append(binding.etDigit2.text.toString())
        sb.append(binding.etDigit3.text.toString())
        sb.append(binding.etDigit4.text.toString())
        sb.append(binding.etDigit5.text.toString())
        sb.append(binding.etDigit6.text.toString())
        return sb.toString()
    }

    private fun isValid(): Boolean {
        return makeOtp()!!.length == 6
    }

    fun getCurrentFocused(): EditText? {
        return etDigitCurrent
    }

    override fun onFocusChange(view: View?, p1: Boolean) {
        etDigitCurrent = view as EditText

        when (view.id) {
            R.id.etDigit1 -> {
                if (binding.etDigit1.text.toString() != "") {
                    etDigitCurrent!!.setSelection(etDigitCurrent!!.text.length)
                } else {
                }
            }
            /* R.id.etDigit4 -> {
                 if( binding.etDigit4.isFocused)
                 if(binding.etDigit4.text.isEmpty()){
                     binding.etDigit4.setBackgroundResource(R.drawable.round_otp_2)
                 }else{
                     binding.etDigit4.setBackgroundResource(R.drawable.round_otp_1)
                 }
             }*/
        }
    }

    override fun afterTextChanged(p0: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, start: Int, before: Int, count: Int) {

        if (etDigitCurrent!!.text.isNotEmpty() && etDigitCurrent != binding.etDigit6) {
            if (count != 0) {
                etDigitCurrent!!.focusSearch(View.FOCUS_RIGHT).requestFocus()
                etDigitCurrent!!.imeOptions = EditorInfo.IME_ACTION_NEXT
            } else {
                etDigitCurrent!!.imeOptions = EditorInfo.IME_ACTION_DONE
                etDigitCurrent!!.focusSearch(View.FOCUS_LEFT).requestFocus()
            }

        } else if (etDigitCurrent!!.text.isNotEmpty() && etDigitCurrent == binding.etDigit6) {
            if (binding.etDigit1.text.isEmpty()) {
                binding.etDigit1.isFocusable = true
            }
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                binding.etDigit4.windowToken,
                0
            )
        } else {
            val currentValue = etDigitCurrent!!.text.toString()
            if (currentValue.isEmpty() && etDigitCurrent!!.selectionStart <= 0) {
                if (count != 0) {
                    etDigitCurrent!!.focusSearch(View.FOCUS_RIGHT).requestFocus()
                } else {
                    etDigitCurrent!!.focusSearch(View.FOCUS_LEFT).requestFocus()
                }
            }
        }
    }

    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (event!!.action == KeyEvent.ACTION_DOWN) {
            when (v!!.id) {

                R.id.etDigit1 -> {
                    if (binding.etDigit1.text.toString() != "")
                        return isKeyDel(binding.etDigit1, keyCode)
                }
                R.id.etDigit2 -> {
                    return isKeyDel(binding.etDigit2, keyCode)
                }
                R.id.etDigit3 -> {
                    return isKeyDel(binding.etDigit3, keyCode)
                }
                R.id.etDigit4 -> {
                    return isKeyDel(binding.etDigit4, keyCode)
                }
                R.id.etDigit5 -> {
                    return isKeyDel(binding.etDigit5, keyCode)
                }
                R.id.etDigit6 -> {
                    return isKeyDel(binding.etDigit6, keyCode)
                }
            }
        }
        return false
    }

    private fun isKeyDel(etDigit: EditText, keyCode: Int): Boolean {
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            etDigit.text = null
            return true
        }
        return false
    }
}