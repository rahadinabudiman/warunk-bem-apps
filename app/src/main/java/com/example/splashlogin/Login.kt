package com.example.splashlogin

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val button = findViewById<Button>(R.id.button)
        button.setBackgroundColor(Color.WHITE)

        button.setOnClickListener {
            val intent = Intent(this@Login, VerifikasiLog::class.java)
            startActivity(intent)
        }

        val button5 = findViewById<Button>(R.id.button5)
        button5.setBackgroundColor(Color.WHITE)

        button5.setOnClickListener {
            val intent = Intent(this@Login, RegistrasiForm::class.java)
            startActivity(intent)
        }
    }
}