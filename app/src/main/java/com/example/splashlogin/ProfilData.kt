package com.example.splashlogin

import APIService
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
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

class ProfilData : AppCompatActivity() {
    private lateinit var apiService: APIService
    private var progressDialog: AlertDialog? = null
    private val httpClient = OkHttpClient.Builder().addInterceptor(
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    ).build()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)

        apiService = RetrofitHelper.getInstance().create(APIService::class.java)
        getUserSetting()

        val HomeText: LinearLayout = findViewById(R.id.homebtn)
        HomeText.setOnClickListener {
            val intent = Intent(this, dashbord::class.java)
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

        val SettingText: LinearLayout = findViewById(R.id.settingbtn)
        SettingText.setOnClickListener {
            val intent = Intent(this, ProfilData::class.java)
            startActivity(intent)
        }

        val button9 = findViewById<Button>(R.id.button9)
        button9.setBackgroundColor(Color.WHITE)
        button9.setOnClickListener {
            val intent = Intent(this, RiwayatPembelian::class.java)
            startActivity(intent)
        }

        val button10 = findViewById<Button>(R.id.button10)
        button10.setBackgroundColor(Color.WHITE)

        val button11 = findViewById<Button>(R.id.button11)
        button11.setBackgroundColor(Color.WHITE)

        val button12 = findViewById<Button>(R.id.button12)
        button12.setBackgroundColor(Color.WHITE)

        val button13 = findViewById<Button>(R.id.button13)
        button13.setBackgroundColor(Color.WHITE)
        button13.setOnClickListener {
            logoutAkun()
        }
    }
    private fun showLoading(msg: String) {
        progressDialog = AlertDialog.Builder(this)
            .setMessage(msg)
            .setCancelable(false)
            .show()
    }

    private fun logoutAkun() {
        lifecycleScope.launch {
            try {
                showLoading("Loading data...")
                val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                val token = sharedPreferences.getString("token", null)
                Log.d("Dashboard", "Dashboard JWT: $token")

                val request = Request.Builder()
                    .url("http://10.0.2.2:8080/api/v1/logout/")
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
                                    runOnUiThread {
                                        Toast.makeText(applicationContext, "Logout berhasil", Toast.LENGTH_SHORT).show()
                                        val editor = sharedPreferences.edit()
                                        editor.remove("token")
                                        editor.apply()
                                        // Navigasi ke halaman Login
                                        val intent = Intent(this@ProfilData, Login::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                        startActivity(intent)
                                        finish()
                                    }
                                } else {
                                    runOnUiThread {
                                        Toast.makeText(applicationContext, "Logout gagal", Toast.LENGTH_SHORT).show()
                                    }
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
    private fun getUserSetting() {
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

                                    runOnUiThread {
                                        val textViewName = findViewById<TextView>(R.id.NamaUser)
                                        val textViewSaldo = findViewById<TextView>(R.id.saldoTextUser)
                                        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
                                        formatRupiah.currency = Currency.getInstance("IDR")
                                        val saldoFormatted = formatRupiah.format(saldoAmount)
                                        textViewSaldo.text = saldoFormatted
                                        textViewName.text = name
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
}