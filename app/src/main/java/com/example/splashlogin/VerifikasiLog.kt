package com.example.splashlogin

import APIService
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.splashlogin.BaseResponse
import com.example.splashlogin.UserResponse
import com.example.splashlogin.model.VerifyLogin
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class VerifikasiLog : AppCompatActivity() {
    private lateinit var apiService: APIService
    private var progressDialog: AlertDialog? = null
    private var authToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verifikasi_log)

        val otpBox1 = findViewById<EditText>(R.id.otpBox1)
        val otpBox2 = findViewById<EditText>(R.id.otpBox2)
        val otpBox3 = findViewById<EditText>(R.id.otpBox3)
        val otpBox4 = findViewById<EditText>(R.id.otpBox4)
        val otpBox5 = findViewById<EditText>(R.id.otpBox5)
        val otpBox6 = findViewById<EditText>(R.id.otpBox6)

        otpBox1.addTextChangedListener(createTextWatcher(otpBox1, otpBox2))
        otpBox2.addTextChangedListener(createTextWatcher(otpBox1, otpBox3))
        otpBox3.addTextChangedListener(createTextWatcher(otpBox2, otpBox4))
        otpBox4.addTextChangedListener(createTextWatcher(otpBox3, otpBox5))
        otpBox5.addTextChangedListener(createTextWatcher(otpBox4, otpBox6))
        otpBox6.addTextChangedListener(createTextWatcher(otpBox5, otpBox6))

        // Initialize apiService
        apiService = RetrofitHelper.getInstance().create(APIService::class.java)

        val button = findViewById<Button>(R.id.button6)
        button.setBackgroundColor(Color.WHITE)

        button.setOnClickListener {
            val otpCode = getOtpCode()
            val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            val token = sharedPreferences.getString("token", null)
            val verifyLogin = VerifyLogin(otpCode)
            // Verify the login using CoroutineScope
            lifecycleScope.launch {
                try {
                    showLoading("Verifying code, please wait...")

                    val requestBody = RequestBody.create("application/json".toMediaType(), Gson().toJson(verifyLogin))
                    VerifyLoginUser(token, verifyLogin)
                    Log.d("Form", "FORM: $requestBody")
                    displayTokenFromPreferences()
                } catch (e: Exception) {
                    // Error saat melakukan verifikasi
                    Log.e("Verifikasi", "Error during verification", e)
                } finally {
                    progressDialog?.dismiss()
                }
            }
        }
    }

    private fun showLoading(msg: String) {
        progressDialog = AlertDialog.Builder(this)
            .setMessage(msg)
            .setCancelable(false)
            .show()
    }

    private fun getOtpCode(): Int {
        val otpBox1 = findViewById<EditText>(R.id.otpBox1).text.toString()
        val otpBox2 = findViewById<EditText>(R.id.otpBox2).text.toString()
        val otpBox3 = findViewById<EditText>(R.id.otpBox3).text.toString()
        val otpBox4 = findViewById<EditText>(R.id.otpBox4).text.toString()
        val otpBox5 = findViewById<EditText>(R.id.otpBox5).text.toString()
        val otpBox6 = findViewById<EditText>(R.id.otpBox6).text.toString()

        val otpCode = "$otpBox1$otpBox2$otpBox3$otpBox4$otpBox5$otpBox6".toIntOrNull() ?: 0

        return otpCode
    }

    private fun saveTokenToPreferences(token: String?) {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("token", token)
        editor.apply()
        authToken = token
    }

    private fun VerifyLoginUser(token: String?, verifyLogin: VerifyLogin) {
        val client = OkHttpClient.Builder()
            .build()

        val requestBody = RequestBody.create("application/json".toMediaType(), Gson().toJson(verifyLogin))

        val request = Request.Builder()
            .url("http://10.0.2.2:8080/api/v1/user/verify/")
            .header("Authorization", "Bearer $token")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Verifikasi Login", "Error during Verifikasi Login", e)
            }

            override fun onResponse(call: Call, response: okhttp3.Response) {
                val responseBody = response.body?.string()
                if (response.isSuccessful) {
                    // Parse the response and perform actions based on the data
                    val baseResponse = Gson().fromJson<BaseResponse<VerifikasiResponse>>(
                        responseBody,
                        object : TypeToken<BaseResponse<VerifikasiResponse>>() {}.type
                    )
                    if (baseResponse != null) {
                        if (baseResponse.status_code == 200) {
                            // Verifikasi berhasil
                            val VerifikasiResponse = baseResponse.data
                            if (VerifikasiResponse != null) {
                                // Verifikasi berhasil, lakukan tindakan sesuai kebutuhan
                                Log.e("Verifikasi", "Verifikasi berhasil: $VerifikasiResponse")

                                // Simpan token ke Shared Preferences atau Cookie Android
                                saveTokenToPreferences(token)
                                Log.d("Token", "JWT Token Response: $token")

                                // Lakukan navigasi ke halaman berikutnya
                                val intent = Intent(this@VerifikasiLog, dashbord::class.java)
                                startActivity(intent)
                            } else {
                                // Response tidak valid atau data null
                                Log.e("Verifikasi", "Response tidak valid atau data null")
                            }
                        } else {
                            // Handle other status codes if needed
                            Log.e("Verifikasi Login", "Status code: ${baseResponse.status_code}")
                        }
                    } else {
                        // Response tidak valid atau data null
                        Log.e("Verifikasi Login", "Response tidak valid atau data null")
                    }
                } else {
                    // Verifikasi gagal
                    val statusCode = response.code
                    val errorMessage = response.message
                    Log.e("Verifikasi Login", "$statusCode $errorMessage")
                    // Tampilkan toast bahwa verifikasi gagal
                    runOnUiThread {
                        Toast.makeText(this@VerifikasiLog, "Verifikasi gagal", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }


    private fun displayTokenFromPreferences() {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        Log.d("Token", "JWT Token: $token")
    }

    private fun createTextWatcher(previousEditText: EditText, nextEditText: EditText): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No implementation needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No implementation needed
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrBlank()) {
                    previousEditText.requestFocus()
                } else if (s.length == 1) {
                    nextEditText.requestFocus()
                }
            }
        }
    }
}