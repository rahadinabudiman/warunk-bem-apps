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
import android.widget.TextView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.splashlogin.model.BeliKeranjang
import com.example.splashlogin.model.TransaksiSekarang
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
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

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

        val homeText: LinearLayout = findViewById(R.id.homebtn)
        homeText.setOnClickListener {
            // Pindah ke Dashboard Activity
            val intent = Intent(this, dashbord::class.java)
            startActivity(intent)
            finish()
        }

        val riwayatText: LinearLayout = findViewById(R.id.riwayatbtn)
        riwayatText.setOnClickListener{
            // Pindah ke Riwayat Pembelian Activity (refresh halaman)
            val intent = Intent(this, RiwayatPembelian::class.java)
            startActivity(intent)
            finish()
        }

        val cartText: LinearLayout = findViewById(R.id.cartbtn)
        cartText.setOnClickListener {
            val intent = Intent(this, Keranjang::class.java)
            startActivity(intent)
        }

        val SettingText: LinearLayout = findViewById(R.id.settingbtn)
        SettingText.setOnClickListener {
            val intent = Intent(this, ProfilData::class.java)
            startActivity(intent)
        }

        val BeliSemua: Button = findViewById(R.id.buttonBeliSemua)
        BeliSemua.setOnClickListener {
            // Beli Semua
            BeliSemuaKeranjang()
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
                                        val idKeranjang = dataJsonObject.optString("id")
                                        val sharedPreferences =
                                            getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                                        val editor = sharedPreferences.edit()
                                        editor.putString("selectedKeranjangId", idKeranjang)
                                        editor.apply()
                                        val produkJsonArray =
                                            dataJsonObject.optJSONArray("produk")
                                        if (produkJsonArray != null && produkJsonArray.length() > 0) {
                                            val gson = Gson()
                                            val produkList: ArrayList<Produk> = ArrayList()
                                            var total = 0
                                            for (i in 0 until produkJsonArray.length()) {
                                                val produk: Produk = gson.fromJson(
                                                    produkJsonArray.getJSONObject(i).toString(),
                                                    Produk::class.java
                                                )
                                                produkList.add(produk)
                                                total += produk.price * produk.stock
                                            }

                                            runOnUiThread {
                                                // Tambahkan tampilan produk baru berdasarkan data dari API
                                                val recyclerKeranjang: RecyclerView =
                                                    findViewById(R.id.recyclerKeranjang)
                                                recyclerKeranjang.layoutManager =
                                                    LinearLayoutManager(this@Keranjang)
                                                recyclerKeranjang.adapter =
                                                    CartAdapter(produkList, total)

                                                val totalPriceTextView: TextView =
                                                    findViewById(R.id.textViewTotalPrice)
                                                val formatRupiah =
                                                    NumberFormat.getCurrencyInstance(Locale("in", "ID"))
                                                formatRupiah.currency = Currency.getInstance("IDR")
                                                val TotalPriceFormatted =
                                                    formatRupiah.format(total)

                                                totalPriceTextView.text =
                                                    "Total Price: $TotalPriceFormatted"

                                                val emptyTextView: TextView =
                                                    findViewById(R.id.emptyTextView)
                                                emptyTextView.visibility = View.GONE
                                            }
                                        } else {
                                            // Tampilkan emptyTextView jika produkJsonArray null atau kosong
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

    private fun BeliSemuaKeranjang(){
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        val selectedKeranjangId = sharedPreferences.getString("selectedKeranjangId", null)

        val BeliSemuaKeranjangData = BeliKeranjang(selectedKeranjangId)

        lifecycleScope.launch {
            try {
                showLoading("Processing transaction...")
                val response = apiService.transaksiKeranjang("Bearer $token", BeliSemuaKeranjangData)

                if (response.isSuccessful) {
                    val transaksiResponse = response.body()?.data
                    if (transaksiResponse != null) {
                        // Handle the successful transaction response
                        runOnUiThread {
                            // Perform actions after a successful transaction
                            // Example: Show a success message, update UI, etc.
                            Toast.makeText(this@Keranjang, "Transaction successful!", Toast.LENGTH_SHORT).show()
                            // Additional actions...

                            // Redirect to the next page (replace NextActivity with your desired activity)
                            val intent = Intent(this@Keranjang, dashbord::class.java)
                            startActivity(intent)
                        }
                    }
                } else {
                    // Handle the unsuccessful transaction response
                    val errorResponse = response.errorBody()?.string()
                    val errorMessage = extractErrorMessage(errorResponse)
                    Log.e("Transaction", "Failed to process transaction: $errorMessage")
                    runOnUiThread {
                        // Perform actions for unsuccessful transaction
                        // Example: Show an error message, handle errors, etc.
                        Toast.makeText(this@Keranjang, "Transaction failed: $errorMessage", Toast.LENGTH_SHORT).show()
                        // Additional actions...
                    }
                }
            } catch (e: Exception) {
                // Handle any exceptions that occur during the API request
                Log.e("Transaction", "Error processing transaction", e)
                runOnUiThread {
                    // Perform actions for transaction error
                    // Example: Show an error message, handle errors, etc.
                    Toast.makeText(this@Keranjang, "Transaction failed. Please try again.", Toast.LENGTH_SHORT).show()
                    // Additional actions...
                }
            } finally {
                progressDialog?.dismiss()
            }
        }
    }

    private fun extractErrorMessage(response: String?): String {
        return try {
            val jsonResponse = JSONObject(response)
            jsonResponse.getString("errors")
        } catch (e: Exception) {
            "Unknown error"
        }
    }
}