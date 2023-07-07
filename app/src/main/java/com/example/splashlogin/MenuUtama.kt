package com.example.splashlogin

import APIService
import android.app.AlertDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.example.splashlogin.R
import com.example.splashlogin.RetrofitHelper
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class MenuUtama : AppCompatActivity() {
    private lateinit var apiService: APIService
    private var progressDialog: AlertDialog? = null
    private val httpClient = OkHttpClient.Builder().addInterceptor(
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    ).build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_utama)
        apiService = RetrofitHelper.getInstance().create(APIService::class.java)

        getUserDashboard()
    }

    private fun getUserDashboard() {
        lifecycleScope.launch {
            try {
                showLoading("Loading data...")
                val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                val token = sharedPreferences.getString("token", null)
                Log.d("Dashboard", "Dashboard JWT: $token")

                val request = Request.Builder()
                    .url("http://10.0.2.2:8080/api/v1/dashboard/")
                    .addHeader("Authorization", "Bearer $token")
                    .build()

                httpClient.newCall(request).enqueue(object : Callback {
                    override fun onResponse(call: Call, response: Response) {
                        val responseData = response.body?.string()
                        if (response.isSuccessful) {
                            try {
                                val jsonResponse = JSONObject(responseData)

                                val statusCode = jsonResponse.getInt("status_code")
                                val message = jsonResponse.getString("message")

                                if (statusCode == 200) {
                                    val dataObject = jsonResponse.getJSONObject("data")

                                    val saldoObject = dataObject.getJSONObject("saldo")
                                    val saldoAmount = saldoObject.getInt("amount")

                                    val profilObject = dataObject.getJSONObject("profil")
                                    val name = profilObject.getString("name")
                                    val email = profilObject.getString("email")

                                    val produkArray = dataObject.getJSONArray("produk")
                                    val produkList = mutableListOf<Produk>()
                                    for (i in 0 until produkArray.length()) {
                                        val produkObject = produkArray.getJSONObject(i)
                                        val produk = Produk(
                                            produkObject.getString("id"),
                                            produkObject.getString("created_at"),
                                            produkObject.getString("updated_at"),
                                            produkObject.getString("slug"),
                                            produkObject.getString("name"),
                                            produkObject.getString("detail"),
                                            produkObject.getInt("price"),
                                            produkObject.getInt("stock"),
                                            produkObject.getString("category"),
                                            produkObject.getString("image")
                                        )
                                        produkList.add(produk)
                                    }

                                    runOnUiThread {
                                        val textViewName = findViewById<TextView>(R.id.textViewName)
                                        val textViewSaldo = findViewById<TextView>(R.id.textViewSaldo)
                                        val textViewProduk = findViewById<TextView>(R.id.textViewProduk)

                                        textViewName.text = "Name: $name"
                                        textViewSaldo.text = "Saldo: $saldoAmount"
                                        textViewProduk.text = produkList.joinToString("\n") { it.name }
                                    }
                                } else {
                                    Log.e("Dashboard", "Failed to retrieve data: $message")
                                }
                            } catch (e: Exception) {
                                Log.e("Dashboard", "Error parsing JSON", e)
                            }
                        } else {
                            Log.e("Dashboard", "Failed to retrieve data: ${response.message}")
                        }

                        progressDialog?.dismiss()
                    }

                    override fun onFailure(call: Call, e: IOException) {
                        Log.e("Dashboard", "Error retrieving data", e)
                        progressDialog?.dismiss()
                    }
                })
            } catch (e: Exception) {
                Log.e("Dashboard", "Error retrieving data", e)
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