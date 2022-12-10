package com.example.inspector

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity


class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)
        //Hide Statusbar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)  //Actionbar was hidden in xml

        val imgAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_img_anim)
        val splashImage: ImageView = findViewById(R.id.splash_image)

        val txtAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_txt_anim)
        val splashText: TextView = findViewById(R.id.splash_text)

        val txtByAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_by_mike_anim)
        val splashByText: TextView = findViewById(R.id.by_mike)

        splashImage.startAnimation(imgAnimation)
        splashText.startAnimation(txtAnimation)
        splashByText.startAnimation(txtByAnimation)

        val homeIntent = Intent(this, MainActivity::class.java)
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(homeIntent)
            finish()
        }, 3000)
    }
}