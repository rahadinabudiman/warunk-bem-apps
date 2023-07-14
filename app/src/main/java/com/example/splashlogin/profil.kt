package com.example.splashlogin

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class profil : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)

        val button9 = findViewById<Button>(R.id.button9)
        button9.setBackgroundColor(Color.WHITE)

        val button10 = findViewById<Button>(R.id.button10)
        button10.setBackgroundColor(Color.WHITE)

        val button11 = findViewById<Button>(R.id.button11)
        button11.setBackgroundColor(Color.WHITE)

        val button12 = findViewById<Button>(R.id.button12)
        button12.setBackgroundColor(Color.WHITE)

        val button13 = findViewById<Button>(R.id.button13)
        button13.setBackgroundColor(Color.WHITE)
    }
}