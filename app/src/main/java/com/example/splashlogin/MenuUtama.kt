package com.example.splashlogin

import APIService
import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import com.example.splashlogin.R
import com.example.splashlogin.RetrofitHelper
import kotlinx.coroutines.launch

class MenuUtama : AppCompatActivity() {
    private lateinit var apiService: APIService
    private var progressDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_utama)
        apiService = RetrofitHelper.getInstance().create(APIService::class.java)

        findViewById<Button>(R.id.button4).setOnClickListener {
            getUserByID()
        }
    }

    private fun getUserByID() {
        lifecycleScope.launch {
            try {
                showLoading("Getting, please wait......")
                val result = apiService.getUserByID("64a4bd208473049babdb726c")
                if (result.isSuccessful) {
                    Log.e("ooooo", "getUserBydID: ${result.body()?.data}")
                } else {
                    Log.e("ooooo", "getUserBydID field: ${result.message()}")
                }
            } catch (e: Exception) {
                Log.e("ooooo", "Error in getUserByID", e)
            } finally {
                progressDialog?.dismiss()
            }
        }
    }

    private fun showLoading(msg: String) {
        progressDialog = AlertDialog.Builder(this)
            .setMessage(msg)
            .setCancelable(false)
            .show()
    }
}