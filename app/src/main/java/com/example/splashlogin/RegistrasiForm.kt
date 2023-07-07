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
import com.example.splashlogin.model.RegisterUser
import com.example.splashlogin.RegistrationResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class RegistrasiForm : AppCompatActivity() {
    private lateinit var apiService: APIService
    private var progressDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrasi_form)

        // Initialize apiService
        apiService = RetrofitHelper.getInstance().create(APIService::class.java)

        val button = findViewById<Button>(R.id.button3)
        button.setBackgroundColor(Color.WHITE)

        button.setOnClickListener {
            // Masukkan data registrasi yang diinput oleh pengguna
            val name = findViewById<EditText>(R.id.editTextText).text.toString()
            val email = findViewById<EditText>(R.id.editTextText6).text.toString()
            val username = findViewById<EditText>(R.id.editTextText3).text.toString()
            val password = findViewById<EditText>(R.id.editTextText4).text.toString()
            val passwordConfirm = findViewById<EditText>(R.id.editTextText5).text.toString()

            // Buat objek RegisterUser dengan data registrasi
            val registerUser = RegisterUser(name, email, username, password, passwordConfirm)

            // Lakukan proses registrasi menggunakan CoroutineScope
            registerUser(registerUser)
        }
    }

    private fun showLoading(msg: String) {
        progressDialog = AlertDialog.Builder(this)
            .setMessage(msg)
            .setCancelable(false)
            .show()
    }

    private fun registerUser(user: RegisterUser) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response = apiService.createUser(user)
                if (response.isSuccessful) {
                    val baseResponse = response.body()
                    if (baseResponse != null) {
                        val registrasionResponse = baseResponse.data
                        if (registrasionResponse != null) {
                            val message = registrasionResponse.message
                            showAlertDialog("Success", message)

                            // Lakukan navigasi ke halaman berikutnya
                            val intent = Intent(this@RegistrasiForm, VerifikasiRegister::class.java)
                            startActivity(intent)
                        } else {
                            showAlertDialog("Error", "RegistrasionResponse data is null")
                        }
                    } else {
                        showAlertDialog("Error", "Response body is empty")
                    }
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val errorMessage = Gson().fromJson(errorResponse, RegistrationResponse::class.java)
                    val error = errorMessage?.errors ?: errorMessage?.message ?: "Unknown error occurred"
                    showAlertDialog("Error", error)
                }
            } catch (e: Exception) {
                showAlertDialog("Error", e.message ?: "Unknown error occurred")
            }
        }
    }

    private fun showAlertDialog(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}