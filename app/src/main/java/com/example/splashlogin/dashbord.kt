package com.example.splashlogin

import APIService
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.squareup.picasso.Picasso
import com.example.splashlogin.R.dimen.text_size
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

class dashbord : AppCompatActivity() {
    private lateinit var apiService: APIService
    private var progressDialog: AlertDialog? = null
    private val httpClient = OkHttpClient.Builder().addInterceptor(
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    ).build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashbord)

        apiService = RetrofitHelper.getInstance().create(APIService::class.java)

        getUserDashboard()

        val textView12: TextView = findViewById(R.id.textView12)
        textView12.setOnClickListener {
            // Pindah ke Dashboard Activity
            val intent = Intent(this, MenuUtama::class.java)
            startActivity(intent)
        }

        val riwayatText: LinearLayout = findViewById(R.id.riwayatbtn)
        riwayatText.setOnClickListener{
            // Pindah ke Riwayat Activity
            val intent = Intent(this, RiwayatPembelian::class.java)
            startActivity(intent)
        }

        val cartText: LinearLayout = findViewById(R.id.cartbtn)
        cartText.setOnClickListener {
            val intent = Intent(this, Keranjang::class.java)
            startActivity(intent)
        }

        val button4 = findViewById<Button>(R.id.button4)
        button4.setBackgroundColor(Color.BLUE)

        button4.setOnClickListener {
            val intent = Intent(this@dashbord, MenuUtama::class.java)
            startActivity(intent)
        }


        val button14 = findViewById<Button>(R.id.ButtonBeli)
        button14.setBackgroundColor(Color.BLUE)

        button14.setOnClickListener {
            val intent = Intent(this@dashbord, DetailProduk::class.java)
            startActivity(intent)
        }


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
                                        // val textViewName = findViewById<TextView>(R.id.SaldoWBEM)
                                        val textViewSaldo = findViewById<TextView>(R.id.saldoText)
                                        val linearLayout = findViewById<LinearLayout>(R.id.LinearLayoutProduk)
                                        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
                                        formatRupiah.currency = Currency.getInstance("IDR")
                                        val saldoFormatted = formatRupiah.format(saldoAmount)
                                        textViewSaldo.text = saldoFormatted

                                        // Hapus semua tampilan produk yang ada
                                        linearLayout.removeAllViews()

                                        // Tambahkan tampilan produk baru berdasarkan data dari API
                                        for (produk in produkList) {
                                            val produkLayout = layoutInflater.inflate(R.layout.produk_item, null) as LinearLayout

                                            val cardView = produkLayout.findViewById<CardView>(R.id.cardViewProduk)
                                            val imageView = produkLayout.findViewById<ImageView>(R.id.imageProduk)
                                            val textView = produkLayout.findViewById<TextView>(R.id.NamaProduk)
                                            val button = produkLayout.findViewById<Button>(R.id.ButtonBeli)

                                            // Set gambar menggunakan URL dari respons API
                                            Picasso.get().load(produk.image).into(imageView)

                                            textView.text = produk.name
                                            button.setOnClickListener {
                                                val idProduk = produk.id // Ganti dengan ID produk yang sesuai
                                                val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                                                val editor = sharedPreferences.edit()
                                                editor.putString("selectedProductId", idProduk)
                                                editor.apply()

                                                val intent = Intent(this@dashbord, DetailProduk::class.java)
                                                startActivity(intent)
                                            }

                                            val linearLayout = findViewById<LinearLayout>(R.id.LinearLayoutProduk)
                                            val layoutParams = LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT
                                            )
                                            layoutParams.setMargins(0, 0, 0, 16) // Atur jarak bawah antara produk
                                            cardView.layoutParams = layoutParams

                                            linearLayout.addView(produkLayout)
                                        }
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