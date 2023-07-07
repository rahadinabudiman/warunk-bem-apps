package com.example.splashlogin

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("name") var name: String = "",
    @SerializedName("username") var username: String = "",
    @SerializedName("email") var email: String = "",
    @SerializedName("token") var token: String = "",
    @SerializedName("status_code") var status_code: Int = 0
)

data class VerifikasiResponse(
    @SerializedName("message")
    var message: String = ""
)

data class RegistrationResponse(
    @SerializedName("status_code") var statusCode: Int,
    @SerializedName("message") var message: String,
    @SerializedName("data") var data: String,
    @SerializedName("errors") var errors:String
)