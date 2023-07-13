package com.example.splashlogin

import APIService
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.splashlogin.BaseResponse
import com.example.splashlogin.model.LoginUser
import com.example.splashlogin.UserResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Login : AppCompatActivity() {
    private lateinit var apiService: APIService
    private var progressDialog: AlertDialog? = null
    private var authToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize apiService
        apiService = RetrofitHelper.getInstance().create(APIService::class.java)

        val button = findViewById<Button>(R.id.button)
        button.setBackgroundColor(Color.WHITE)

        button.setOnClickListener {
            // Masukkan email dan password yang diinput oleh pengguna
            val email = findViewById<EditText>(R.id.editTextText).text.toString()
            val password = findViewById<EditText>(R.id.editTextTextPassword).text.toString()

            // Buat objek User dengan email dan password
            val user = LoginUser(email, password)

            // Lakukan proses login menggunakan CoroutineScope
            loginUser(user)
        }

        val button5 = findViewById<Button>(R.id.button5)
        button5.setBackgroundColor(Color.WHITE)

        button5.setOnClickListener {
            val intent = Intent(this@Login, RegistrasiForm::class.java)
            startActivity(intent)
        }
    }

    private fun showLoading(msg: String) {
        progressDialog = AlertDialog.Builder(this)
            .setMessage(msg)
            .setCancelable(false)
            .show()
    }

    private fun loginUser(user: LoginUser) {
        lifecycleScope.launch {
            try {
                showLoading("Logging in, please wait...")

                val response = withContext(Dispatchers.IO) { apiService.loginUser(user) }

                if (response.isSuccessful) {
                    // Berhasil login
                    val responseData = response.body()
                    if (responseData != null) {
                        val userResponse = responseData.data
                        if (userResponse != null) {
                            val token = userResponse.token
                            saveTokenToPreferences(token)
                            displayTokenFromPreferences()

                            val intent = Intent(this@Login, VerifikasiLog::class.java)
                            startActivity(intent)
                        } else {
                            // Response tidak valid atau data null
                            Log.e("Login", "Response tidak valid atau data null")
                        }
                    } else {
                        // Response tidak valid atau data null
                        Log.e("Login", "Response tidak valid atau data null")
                    }
                } else {
                    // Login gagal
                    Log.e("Login", "Login failed: ${response.code()}")

                    // Tampilkan pesan respons error
                    val errorResponse = response.errorBody()?.string()
                    val errorMessage = extractErrorMessage(errorResponse)
                    showErrorAlert(errorMessage)
                }
            } catch (e: Exception) {
                // Error saat melakukan login
                Log.e("Login", "Error during login", e)

                // Tampilkan alert error
                showErrorAlert("An error occurred during login.")
            } finally {
                progressDialog?.dismiss()
            }
        }
    }

    private fun extractErrorMessage(errorResponse: String?): String {
        try {
            val error = Gson().fromJson(errorResponse, ErrorResponse::class.java)
            return error.errors
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "Unknown error"
    }

    private fun showErrorAlert(errorMessage: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(errorMessage)
        builder.setPositiveButton("OK") { dialog, which -> dialog.dismiss() }
        val dialog = builder.create()
        dialog.show()
    }

    private fun saveTokenToPreferences(token: String?) {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("token", token)
        editor.apply()
        authToken = token
    }

    private fun displayTokenFromPreferences() {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        Log.d("Token", "JWT Token: $token")
    }
}