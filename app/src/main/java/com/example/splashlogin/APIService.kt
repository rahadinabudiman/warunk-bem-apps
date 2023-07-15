import com.example.splashlogin.BaseResponse
import com.example.splashlogin.DashboardData
import com.example.splashlogin.Produk
import com.example.splashlogin.RegistrationResponse
import com.example.splashlogin.TransaksiSekarangResponse
import com.example.splashlogin.UserResponse
import com.example.splashlogin.model.LoginUser
import com.example.splashlogin.model.MasukKeranjang
import com.example.splashlogin.model.RegisterUser
import com.example.splashlogin.model.TransaksiSekarang
import com.example.splashlogin.model.VerifyLogin
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface APIService {

    @GET("user/{id}")
    suspend fun getUserByID(@Path("id") id: String): Response<BaseResponse<UserResponse>>

    @GET("produk/{id}")
    suspend fun getProdukById(@Path("id") id: String): Response<BaseResponse<Produk>>

    @POST("transaksi")
    suspend fun transaksiSekarang(
        @Header("Authorization") token_auth:String?,
        @Body user: TransaksiSekarang
    ): Response<BaseResponse<TransaksiSekarangResponse>>

    @POST("keranjang")
    suspend fun masukKeranjang(
        @Header("Authorization") token_auth:String?,
        @Body user: MasukKeranjang
    ): Response<BaseResponse<TransaksiSekarangResponse>>

    @Headers("Content-Type: application/json")
    @GET("user")
    suspend fun getUsers(): Response<BaseResponse<List<UserResponse>>>

     @POST("user")
     suspend fun createUser(@Body user: RegisterUser): Response<BaseResponse<RegistrationResponse>>
//
//    @PUT("user/{id}")
//    suspend fun updateUser(@Path("id") id: String, @Body user: User): Response<BaseResponse<UserResponse>>

    @DELETE("user/{id}")
    suspend fun deleteUser(@Path("id") id: String): Response<BaseResponse<Any>>

    // Authentikasi Routing
    @POST("login")
    suspend fun loginUser(@Body user: LoginUser): Response<BaseResponse<UserResponse>>

    @POST("user/verify")
    suspend fun VerifyLogin(
        @Header("Authorization") token_auth:String?,
        @Body user: VerifyLogin
    ): Response<BaseResponse<UserResponse>>

    @GET("dashboard")
    suspend fun Dashboard(
        @Header("Authorization") token_auth:String?
    ): Response<BaseResponse<DashboardData>>

    @POST("user/activation")
    suspend fun ActivationAccount(
        @Body user: VerifyLogin
    ): Response<BaseResponse<RegistrationResponse>>

    companion object {
        private const val BASE_URL = "http://10.0.2.2:8080/api/v1/"

        fun create(): APIService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(APIService::class.java)
        }
    }
}