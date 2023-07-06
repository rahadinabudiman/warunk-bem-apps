package com.example.splashlogin

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface APIService {

    @GET("/user/{id}")
    suspend fun getUserByID(@Path("id") id:String): Response<BaseResponse<UserResponse>>

    @GET("user")
    fun getUsers(): Response<BaseResponse<List<UserResponse>>>

//    @POST("user")
//    fun createUser(@Body user: User): Response<BaseResponse<UserResponse>>
//
//    @PUT("user/{id}")
//    fun updateUser(@Path("id") id: Int, @Body user: User): Response<BaseResponse<UserResponse>>

    @DELETE("user/{id}")
    fun deleteUser(@Path("id") id: Int): Response<BaseResponse<Any>>
}