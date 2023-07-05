package com.example.splashlogin

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class RegistrasiForm : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrasi_form)

        val button = findViewById<Button>(R.id.button3)
        button.setBackgroundColor(Color.WHITE)

        button.setOnClickListener {
            val intent = Intent(this@RegistrasiForm, Login::class.java)
            startActivity(intent)
        }

    }
}