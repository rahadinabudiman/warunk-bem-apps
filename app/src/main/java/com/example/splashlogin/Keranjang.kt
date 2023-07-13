package com.example.splashlogin

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.ImageView

class Keranjang : AppCompatActivity() {
    private lateinit var tvQuantity: TextView
    private lateinit var btnPlus: Button
    private lateinit var btnMinus: Button
    private var quantity = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_keranjang)

        btnMinus = findViewById(R.id.btnMinus)
        btnPlus = findViewById(R.id.btnPlus)
        tvQuantity = findViewById(R.id.tvQuantity)

        btnMinus.setBackgroundColor(Color.WHITE)
        btnPlus.setBackgroundColor(Color.WHITE)

        btnPlus.setOnClickListener {
            increaseQuantity()
        }

        btnMinus.setOnClickListener {
            decreaseQuantity()
        }

        val imageView6: ImageView = findViewById(R.id.imageView11)
        imageView6.setOnClickListener {
            goToMenuUtama()
        }
    }

    private fun increaseQuantity() {
        quantity++
        tvQuantity.text = quantity.toString()
    }

    private fun decreaseQuantity() {
        if (quantity > 0) {
            quantity--
            tvQuantity.text = quantity.toString()
        }
    }

    private fun goToMenuUtama() {
        onBackPressed()
    }
}
