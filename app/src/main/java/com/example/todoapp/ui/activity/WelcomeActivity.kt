package com.example.todoapp.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.example.todoapp.R

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)


        val appname = findViewById<TextView>(R.id.appname)
        val myanim = findViewById<LottieAnimationView>(R.id.animWelcome)

        appname.animate().translationY(-1000F).setDuration(2700).setStartDelay(4500)
        myanim.animate().translationX(2000F).setDuration(2000).setStartDelay(5000)

        Handler().postDelayed({
            val i = Intent(applicationContext, MainActivity::class.java)
            startActivity(i)
            finish()
        }, 8000)

    }
}