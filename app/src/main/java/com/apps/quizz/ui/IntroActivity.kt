package com.apps.quizz.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.apps.quizz.R
import com.apps.quizz.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIntroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_intro)
        setListener()
    }

    private fun setListener() {
        binding.btnLetsStart.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }
    }
}