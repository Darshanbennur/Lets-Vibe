package com.example.letsvibe

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class splashScreen : AppCompatActivity() {

    lateinit var handler : Handler
    private lateinit var preferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        preferences = getSharedPreferences("userData", MODE_PRIVATE)
        handler = Handler()

        handler.postDelayed({

            if (preferences.getString("number", "no").toString() == "no") {
                val intent = Intent(this, LoginPage::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        },1900)

    }
}