package com.example.splashlogin

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("name") var name:String? = "",
    @SerializedName("username") var username:String? = "",
    @SerializedName("email") var email:String? = "",
)
