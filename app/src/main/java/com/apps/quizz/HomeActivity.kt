package com.apps.quizz

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.databinding.DataBindingUtil
import com.apps.quizz.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_home)
        setUpViews()
    }

    @SuppressLint("RtlHardcoded")
    private fun setUpViews() {
        //setUpDrawer()
        binding.toolBar.setNavigationOnClickListener {
            binding.drawerLayout.openDrawer(binding.navigationView)
        }
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->

            menuItem.isChecked = true
            binding.drawerLayout.closeDrawers()
            true
        }
    }

    private fun setUpDrawer() {
        setSupportActionBar(binding.toolBar)
        actionBarDrawerToggle = ActionBarDrawerToggle(this,binding.drawerLayout,R.string.app_name,R.string.app_name)
        actionBarDrawerToggle.syncState()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(actionBarDrawerToggle?.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)

    }
}