package com.apps.quizz.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.apps.quizz.R
import com.apps.quizz.databinding.ActivityLoginBinding
import java.util.regex.Matcher
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        setListener()
    }

    private fun setListener() {
        binding.btnLogin.setOnClickListener {
            if(checkValidation()) {
                startActivity(Intent(this, OtpVerificationActivity::class.java)
                    .putExtra("mobile",binding.edtPhoneNumber.text.toString())
                )
                Toast.makeText(this,"verify",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkValidation(): Boolean {

        val mobileNumber = binding.edtPhoneNumber.text.toString()
        val pattern  =  Pattern.compile("^[6-9]\\d{9}\$")
        val m: Matcher = pattern.matcher(mobileNumber)
        //return m.find() && m.group().equals(mobileNumber)
        if(!(m.find() && m.group().equals(mobileNumber))){
            binding.edtPhoneNumber.apply {
                error = "Please enter valid Phone Number"
                requestFocus()
            }
           return false
        }
        return true
    }
}