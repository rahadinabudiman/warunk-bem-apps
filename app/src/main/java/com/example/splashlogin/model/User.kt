package com.example.splashlogin.model

data class LoginUser(
    val email: String, val password: String
)

data class VerifyLogin(
    val code: Int
)

data class RegisterUser(
    val name: String,
    val email: String,
    val username: String,
    val password: String,
    val passwordconfirm: String,
)

data class TransaksiSekarang(
    val produk_id: String?,
    val total: Int
)

data class MasukKeranjang(
    val produk_id: String?,
    val total: Int
)