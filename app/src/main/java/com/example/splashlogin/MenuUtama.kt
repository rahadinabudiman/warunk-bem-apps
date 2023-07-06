package com.example.splashlogin

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MenuUtama : AppCompatActivity() {
    private lateinit var apiService: APIService
    private var progressDialog: ProgressDialog? = null

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
            showLoading("Getting, please wait......")
            val result = apiService.getUserByID("2")
            if (result.isSuccessful) {
                Log.e("ooooo", "getUserBydID: ${result.body()?.data}")
            } else {
                Log.e("ooooo", "getUserBydID field: ${result.message()}")
            }
            progressDialog?.dismiss()
        }
    }

    private fun showLoading(msg: String) {
        progressDialog = ProgressDialog.show(this, null, msg, true)
    }
}