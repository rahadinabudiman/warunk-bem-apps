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
import android.widget.TextView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.example.splashlogin.model.MasukKeranjang
import com.example.splashlogin.model.TransaksiSekarang
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

class DetailProduk : AppCompatActivity() {
    private lateinit var apiService: APIService
    private var progressDialog: AlertDialog? = null
    private val httpClient = OkHttpClient.Builder().addInterceptor(
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    ).build()

    private lateinit var tvQuantity: TextView
    private lateinit var btnPlus: Button
    private lateinit var btnMinus: Button
    private lateinit var button18: Button
    private lateinit var button19: Button
    private var quantity = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_produk)

        apiService = RetrofitHelper.getInstance().create(APIService::class.java)

        getDetailProduk()

        btnMinus = findViewById(R.id.btnMinus)
        btnPlus = findViewById(R.id.btnPlus)
        button18 = findViewById(R.id.button18)
        button19 = findViewById(R.id.button19)
        tvQuantity = findViewById(R.id.tvQuantity)

        btnMinus.setBackgroundColor(Color.WHITE)
        btnPlus.setBackgroundColor(Color.WHITE)
        button18.setBackgroundColor(Color.WHITE)
        button19.setBackgroundColor(Color.WHITE)

        button18.setOnClickListener {
            masukanKeranjang()
            val intent = Intent(this@DetailProduk, Keranjang::class.java)
            startActivity(intent)
        }

        button19.setOnClickListener {
            beliSekarang()
        }

        btnPlus.setOnClickListener {
            increaseQuantity()
        }

        btnMinus.setOnClickListener {
            decreaseQuantity()
        }

        val imageView6: ImageView = findViewById(R.id.imageView6)
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

    private fun showLoading(msg: String) {
        progressDialog = AlertDialog.Builder(this)
            .setMessage(msg)
            .setCancelable(false)
            .show()
    }

    private fun getDetailProduk() {
        lifecycleScope.launch {
            try {
                showLoading("Loading data...")
                val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                val selectedProductId = sharedPreferences.getString("selectedProductId", null)
                Log.d("DetailProduk", "Selected Product ID: $selectedProductId")

                val request = Request.Builder()
                    .url("http://10.0.2.2:8080/api/v1/produk/${selectedProductId}")
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
                                    val produkObject = jsonResponse.getJSONObject("data")

                                    val id = produkObject.getString("id")
                                    val name = produkObject.getString("name")
                                    val slug = produkObject.getString("slug")
                                    val detail = produkObject.getString("detail")
                                    val price = produkObject.getInt("price")
                                    val stock = produkObject.getInt("stock")
                                    val category = produkObject.getString("category")
                                    val image = produkObject.getString("image")

                                    runOnUiThread {
                                        val textViewNama = findViewById<TextView>(R.id.textView15)
                                        val textViewPrice = findViewById<TextView>(R.id.textView18)
                                        val imageViewProduk = findViewById<ImageView>(R.id.imageView7)

                                        textViewNama.text = name
                                        textViewPrice.text = "Rp.${NumberFormat.getInstance().format(price)}"
                                        Picasso.get().load(image).into(imageViewProduk)
                                    }
                                } else {
                                    Log.e("Detail Produk", "Failed to retrieve data: $message")
                                }
                            } catch (e: Exception) {
                                Log.e("Detail Produk", "Error parsing JSON", e)
                            }
                        } else {
                            Log.e("Detail Produk", "Failed to retrieve data: ${response.message}")
                        }

                        progressDialog?.dismiss()
                    }

                    override fun onFailure(call: Call, e: IOException) {
                        Log.e("Detail Produk", "Error retrieving data", e)
                        progressDialog?.dismiss()
                    }
                })
            } catch (e: Exception) {
                Log.e("Detail Produk", "Error retrieving data", e)
                progressDialog?.dismiss()
            }
        }
    }

    private fun masukanKeranjang(){
        val jumlah_barang = tvQuantity.text.toString().toInt()
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        val selectedProductId = sharedPreferences.getString("selectedProductId", null)

        val MasukKeranjang = MasukKeranjang(selectedProductId, jumlah_barang)

        lifecycleScope.launch {
            try {
                showLoading("Processing transaction...")
                val response = apiService.masukKeranjang("Bearer $token", MasukKeranjang)

                if (response.isSuccessful) {
                    val keranjangResponse = response.body()?.data
                    if (keranjangResponse != null) {
                        // Handle the successful transaction response
                        runOnUiThread {
                            // Perform actions after a successful transaction
                            // Example: Show a success message, update UI, etc.
                            Toast.makeText(this@DetailProduk, "Add to cart success!", Toast.LENGTH_SHORT).show()
                            // Additional actions...

                            // Redirect to the next page (replace NextActivity with your desired activity)
                            val intent = Intent(this@DetailProduk, dashbord::class.java)
                            startActivity(intent)
                        }
                    }
                } else {
                    // Handle the unsuccessful transaction response
                    val errorResponse = response.errorBody()?.string()
                    val errorMessage = extractErrorMessage(errorResponse)
                    Log.e("Transaction", "Failed to add cart: $errorMessage")
                    runOnUiThread {
                        // Perform actions for unsuccessful transaction
                        // Example: Show an error message, handle errors, etc.
                        Toast.makeText(this@DetailProduk, "Add Cart failed: $errorMessage", Toast.LENGTH_SHORT).show()
                        // Additional actions...
                    }
                }
            } catch (e: Exception) {
                // Handle any exceptions that occur during the API request
                Log.e("Transaction", "Error processing cart", e)
                runOnUiThread {
                    // Perform actions for transaction error
                    // Example: Show an error message, handle errors, etc.
                    Toast.makeText(this@DetailProduk, "Add cart failed. Please try again.", Toast.LENGTH_SHORT).show()
                    // Additional actions...
                }
            } finally {
                progressDialog?.dismiss()
            }
        }
    }

    private fun beliSekarang() {
        val jumlah_barang = tvQuantity.text.toString().toInt()
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        val selectedProductId = sharedPreferences.getString("selectedProductId", null)

        val transaksi = TransaksiSekarang(selectedProductId, jumlah_barang)

        lifecycleScope.launch {
            try {
                showLoading("Processing transaction...")
                val response = apiService.transaksiSekarang("Bearer $token", transaksi)

                if (response.isSuccessful) {
                    val transaksiResponse = response.body()?.data
                    if (transaksiResponse != null) {
                        // Handle the successful transaction response
                        runOnUiThread {
                            // Perform actions after a successful transaction
                            // Example: Show a success message, update UI, etc.
                            Toast.makeText(this@DetailProduk, "Transaction successful!", Toast.LENGTH_SHORT).show()
                            // Additional actions...

                            // Redirect to the next page (replace NextActivity with your desired activity)
                            val intent = Intent(this@DetailProduk, dashbord::class.java)
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
                        Toast.makeText(this@DetailProduk, "Transaction failed: $errorMessage", Toast.LENGTH_SHORT).show()
                        // Additional actions...
                    }
                }
            } catch (e: Exception) {
                // Handle any exceptions that occur during the API request
                Log.e("Transaction", "Error processing transaction", e)
                runOnUiThread {
                    // Perform actions for transaction error
                    // Example: Show an error message, handle errors, etc.
                    Toast.makeText(this@DetailProduk, "Transaction failed. Please try again.", Toast.LENGTH_SHORT).show()
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
