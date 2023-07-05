package com.example.splashlogin

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class VerifikasiLog : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verifikasi_log)

        val button = findViewById<Button>(R.id.button6)
        button.setBackgroundColor(Color.WHITE)

        button.setOnClickListener {
            val intent = Intent(this, MenuUtama::class.java)
            startActivity(intent)
        }
    }
}