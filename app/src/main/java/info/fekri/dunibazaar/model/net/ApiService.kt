package info.fekri.dunibazaar.model.net

import com.google.gson.JsonObject
import info.fekri.dunibazaar.model.data.LogInResponse
import info.fekri.dunibazaar.model.repository.TokenInMemory
import info.fekri.dunibazaar.util.BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("signUp")
    suspend fun signUp(@Body jsonObject: JsonObject): LogInResponse

    @POST("signIn")
    suspend fun signIn(@Body jsonObject: JsonObject): LogInResponse

    @GET("refreshToken")
    fun refreshToken(): Call<LogInResponse>

}

fun createApiService(): ApiService {

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor {

            val oldRequest = it.request()

            val newRequest = oldRequest.newBuilder()
            if (TokenInMemory.token != null)
                newRequest.addHeader("Authorization", TokenInMemory.token!!)

            newRequest.addHeader("Accept", "application/json")
            newRequest.method(oldRequest.method, oldRequest.body)

            return@addInterceptor it.proceed(newRequest.build())

        }.build()

    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    return retrofit.create(ApiService::class.java)
}