import com.example.splashlogin.BaseResponse
import com.example.splashlogin.RegistrationResponse
import com.example.splashlogin.UserResponse
import com.example.splashlogin.model.LoginUser
import com.example.splashlogin.model.RegisterUser
import com.example.splashlogin.model.VerifyLogin
import retrofit2.Response
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

    @POST("user/activation")
    suspend fun ActivationAccount(
        @Body user: VerifyLogin
    ): Response<BaseResponse<RegistrationResponse>>
}