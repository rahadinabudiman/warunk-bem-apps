package com.example.splashlogin

import APIService
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import java.io.IOException
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

class RiwayatPembelian : AppCompatActivity() {
    private lateinit var apiService: APIService
    private var progressDialog: AlertDialog? = null
    private val httpClient = OkHttpClient.Builder().addInterceptor(
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    ).build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayat_pembelian)

        apiService = RetrofitHelper.getInstance().create(APIService::class.java)

        getRiwayatPembelian()

        val button1 = findViewById<Button>(R.id.button1)
        val button2 = findViewById<Button>(R.id.button2)

        val homeText: LinearLayout = findViewById(R.id.homebtn)
        homeText.setOnClickListener {
            // Pindah ke Dashboard Activity
            val intent = Intent(this, dashbord::class.java)
            startActivity(intent)
        }

        val riwayatText: LinearLayout = findViewById(R.id.riwayatbtn)
        riwayatText.setOnClickListener{
            // Pindah ke Riwayat Pembelian Activity (refresh halaman)
            val intent = Intent(this, RiwayatPembelian::class.java)
            startActivity(intent)
            finish()
        }

        button1.setOnClickListener {
            button1.setBackgroundColor(Color.BLUE)
            button2.setBackgroundColor(Color.WHITE)
        }

        button2.setOnClickListener {
            button1.setBackgroundColor(Color.WHITE)
            button2.setBackgroundColor(Color.BLUE)
        }
    }

    private fun getRiwayatPembelian() {
        lifecycleScope.launch {
            try {
                showLoading("Loading data...")
                val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                val token = sharedPreferences.getString("token", null)
                Log.d("Riwayat Pembelian", "JWT: $token")

                val request = Request.Builder()
                    .url("http://10.0.2.2:8080/api/v1/transaksi/")
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
                                    val dataJsonArray = jsonResponse.optJSONArray("data")
                                    if (dataJsonArray != null) {
                                        val gson = Gson()
                                        val transactionHistoryList: ArrayList<TransactionHistory> = ArrayList()
                                        for (i in 0 until dataJsonArray.length()) {
                                            val transactionHistory: TransactionHistory = gson.fromJson(dataJsonArray.getJSONObject(i).toString(), TransactionHistory::class.java)
                                            transactionHistoryList.add(transactionHistory)
                                        }

                                        runOnUiThread {
                                            // Tambahkan tampilan produk baru berdasarkan data dari API
                                            val recyclerTransaction: RecyclerView = findViewById(R.id.recyclerTransaction)
                                            recyclerTransaction.layoutManager = LinearLayoutManager(this@RiwayatPembelian)
                                            recyclerTransaction.adapter = TransactionAdapter(transactionHistoryList)
                                        }
                                    } else {
                                        // Tampilkan emptyTextView jika dataJsonArray null
                                        runOnUiThread {
                                            val emptyTextView: TextView = findViewById(R.id.emptyTextView)
                                            emptyTextView.visibility = View.VISIBLE
                                        }
                                    }
                                } else {
                                    Log.e("Riwayat Pembelian", "Failed to retrieve data: $message")
                                }
                            } catch (e: Exception) {
                                Log.e("Riwayat Pembelian", "Error parsing JSON", e)
                            }
                        } else {
                            Log.e("Riwayat Pembelian", "Failed to retrieve data: ${response.message}")
                        }

                        progressDialog?.dismiss()
                    }

                    override fun onFailure(call: Call, e: IOException) {
                        Log.e("Riwayat Pembelian", "Error retrieving data", e)
                        progressDialog?.dismiss()
                    }
                })
            } catch (e: Exception) {
                Log.e("Riwayat Pembelian", "Error retrieving data", e)
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