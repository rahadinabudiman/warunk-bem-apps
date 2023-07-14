package com.example.splashlogin

import APIService
import ProdukAdapter
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.splashlogin.R
import com.example.splashlogin.RetrofitHelper
import com.google.gson.annotations.SerializedName
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

class MenuUtama : AppCompatActivity() {
    private lateinit var apiService: APIService
    private lateinit var recyclerViewProduk: RecyclerView
    private var progressDialog: AlertDialog? = null
    private val httpClient = OkHttpClient.Builder().addInterceptor(
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    ).build()

    private val produkList = mutableListOf<Produk>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_utama)

        // Inisialisasi RecyclerView
        recyclerViewProduk = findViewById(R.id.recyclerViewProduk)
        recyclerViewProduk.layoutManager = GridLayoutManager(this, 2)

        // Create an instance of your custom adapter
        val adapter = ProdukAdapter(produkList)

        // Set the adapter to the RecyclerView
        recyclerViewProduk.adapter = adapter
        apiService = RetrofitHelper.getInstance().create(APIService::class.java)

        getProduk()

//        val button6: Button = findViewById(R.id.button6)
//        button6.setBackgroundColor(Color.WHITE)
//
//        val button8: Button = findViewById(R.id.button8)
//        button8.setBackgroundColor(Color.WHITE)
//
//        val button7: Button = findViewById(R.id.button7)
//        button7.setBackgroundColor(Color.WHITE)
    }

    private fun navigateToDetailProduk(detailProdukClass: Class<*>) {
        val intent = Intent(this, detailProdukClass)
        startActivity(intent)
    }

    private fun showLoading(msg: String) {
        progressDialog = AlertDialog.Builder(this)
            .setMessage(msg)
            .setCancelable(false)
            .show()
    }

    private fun getProduk() {
        lifecycleScope.launch {
            try {
                showLoading("Loading data...")
                val request = Request.Builder()
                    .url("http://10.0.2.2:8080/api/v1/produk/")
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

                                    val produkArray = dataObject.getJSONArray("produks")
                                    val produkList = mutableListOf<Produk>()
                                    for (i in 0 until produkArray.length()) {
                                        val produkObject = produkArray.getJSONObject(i)
                                        val produk = Produk(
                                            produkObject.getString("id"),
                                            "", // created_at tidak tersedia di data API
                                            "", // updated_at tidak tersedia di data API
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
                                        recyclerViewProduk.adapter?.notifyDataSetChanged()

                                        val adapter = ProdukAdapter(produkList)

                                        // Set the adapter to the RecyclerView
                                        recyclerViewProduk.adapter = adapter
                                    }
                                } else {
                                    Log.e("MenuUtama", "Failed to retrieve data: $message")
                                }
                            } catch (e: Exception) {
                                Log.e("MenuUtama", "Error parsing JSON", e)
                            }
                        } else {
                            Log.e("MenuUtama", "Failed to retrieve data: ${response.message}")
                        }

                        progressDialog?.dismiss()
                    }

                    override fun onFailure(call: Call, e: IOException) {
                        Log.e("MenuUtama", "Error retrieving data", e)
                        progressDialog?.dismiss()
                    }
                })
            } catch (e: Exception) {
                Log.e("MenuUtama", "Error retrieving data", e)
                progressDialog?.dismiss()
            }
        }
    }
}