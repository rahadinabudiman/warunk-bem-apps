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

data class DashboardData(
    @SerializedName("status_code") var statusCode: Int,
    @SerializedName("message") var message: String,
    @SerializedName("data") var data: String,
    @SerializedName("saldo") var saldo: Saldo?,
    @SerializedName("profil") var profil: Profil?,
    @SerializedName("produk") var produk: List<Produk>?
)

data class Saldo(
    @SerializedName("id") var id: String,
    @SerializedName("created_at") var createdAt: String,
    @SerializedName("updated_at") var updatedAt: String,
    @SerializedName("user_id") var userId: String,
    @SerializedName("amount") var amount: Int
)

data class Profil(
    @SerializedName("id") var id: String,
    @SerializedName("created_at") var createdAt: String,
    @SerializedName("updated_at") var updatedAt: String,
    @SerializedName("name") var name: String,
    @SerializedName("email") var email: String,
    @SerializedName("username") var username: String,
    @SerializedName("password") var password: String,
    @SerializedName("verified") var verified: Boolean,
    @SerializedName("loginverif") var loginVerif: Int,
    @SerializedName("verification") var verification: Int,
    @SerializedName("activation_code") var activationCode: Int,
    @SerializedName("role") var role: String
)

data class Produk(
    @SerializedName("id") var id: String,
    @SerializedName("created_at") var createdAt: String,
    @SerializedName("updated_at") var updatedAt: String,
    @SerializedName("slug") var slug: String,
    @SerializedName("name") var name: String,
    @SerializedName("detail") var detail: String,
    @SerializedName("price") var price: Int,
    @SerializedName("stock") var stock: Int,
    @SerializedName("category") var category: String,
    @SerializedName("image") var image: String
)