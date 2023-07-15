package com.example.splashlogin

import APIService
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import java.io.IOException

class Keranjang : AppCompatActivity() {
    private lateinit var apiService: APIService
    private var progressDialog: AlertDialog? = null
    private val httpClient = OkHttpClient.Builder().addInterceptor(
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    ).build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_keranjang)

        apiService = RetrofitHelper.getInstance().create(APIService::class.java)
        getKeranjang()

        val imageView6: ImageView = findViewById(R.id.imageView11)
        imageView6.setOnClickListener {
            goToMenuUtama()
        }
    }

    private fun goToMenuUtama() {
        onBackPressed()
    }

    private fun showLoading(msg: String) {
        progressDialog = AlertDialog.Builder(this)
            .setMessage(msg)
            .setCancelable(false)
            .show()
    }

    private fun getKeranjang() {
        lifecycleScope.launch {
            try {
                showLoading("Loading data...")
                val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                val token = sharedPreferences.getString("token", null)
                Log.d("Riwayat Pembelian", "JWT: $token")

                val request = Request.Builder()
                    .url("http://10.0.2.2:8080/api/v1/keranjang/")
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
                                    val dataJsonObject = jsonResponse.optJSONObject("data")
                                    if (dataJsonObject != null) {
                                        val total = dataJsonObject.getInt("total")
                                        val produkJsonArray = dataJsonObject.optJSONArray("produk")
                                        if (produkJsonArray != null) {
                                            val gson = Gson()
                                            val produkList: ArrayList<Produk> = ArrayList()
                                            for (i in 0 until produkJsonArray.length()) {
                                                val produk: Produk =
                                                    gson.fromJson(
                                                        produkJsonArray.getJSONObject(i).toString(),
                                                        Produk::class.java
                                                    )
                                                produkList.add(produk)
                                            }

                                            runOnUiThread {
                                                // Tambahkan tampilan produk baru berdasarkan data dari API
                                                val recyclerKeranjang: RecyclerView =
                                                    findViewById(R.id.recyclerKeranjang)
                                                recyclerKeranjang.layoutManager =
                                                    LinearLayoutManager(this@Keranjang)
                                                recyclerKeranjang.adapter =
                                                    CartAdapter(produkList, total)
                                            }
                                        } else {
                                            // Tampilkan emptyTextView jika produkJsonArray null
                                            runOnUiThread {
                                                val emptyTextView: TextView =
                                                    findViewById(R.id.emptyTextView)
                                                emptyTextView.visibility = View.VISIBLE
                                            }
                                        }
                                    } else {
                                        Log.e("Keranjang", "Data object is null")
                                    }
                                } else {
                                    Log.e("Keranjang", "Failed to retrieve data: $message")
                                }
                            } catch (e: Exception) {
                                Log.e("Keranjang", "Error parsing JSON", e)
                            }
                        } else {
                            Log.e("Keranjang", "Failed to retrieve data: ${response.message}")
                        }

                        progressDialog?.dismiss()
                    }

                    override fun onFailure(call: Call, e: IOException) {
                        Log.e("Keranjang", "Error retrieving data", e)
                        progressDialog?.dismiss()
                    }
                })
            } catch (e: Exception) {
                Log.e("Keranjang", "Error retrieving data", e)
                progressDialog?.dismiss()
            }
        }
    }
}
