package com.elinkthings.ailinkmqttdemo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title= getString(R.string.app_name) + AppBaseActivity.getVersionName(this)
        toolbar.navigationIcon = null
        findViewById<Button>(R.id.btnStart).setOnClickListener {
            startActivity(Intent(this,PublicMqttActivity::class.java))
        }
    }
}