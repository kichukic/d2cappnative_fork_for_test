package com.infolitz.cartitinfo.helper

import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface RetrofitService {

   /* @POST("user/register")
    fun getProfile(@Body profileReq: ModelProfileReqData): Call<ProfileListData>

    @GET("infolitztest")
    fun getAllLanguages(
        @Header("Authorization") token: String
    ): Call<LangListData>

    @GET("question/{textType}")
    fun getAllQuesAndAns(
        @Path("textType") textType: String,
        @Header("Authorization") token: String
    ): Call<ModelQuesAndAns>

    @POST("user/submit-result")
    fun sentDataToDb(
        @Header("Authorization") token: String,
        @Body mainReq: ModelQuesAndAns
    ): Call<ModelSendDataToDbResData>

    @POST("user/update-profile/{id}")
    fun getUpdateProfile(
        @Path("id") id: String,
        @Body updateProfileRequest: ModelUpdateProfileReqData
    ): Call<UpdateProfileListData>
*/
    companion object {
        private val stethoClient: OkHttpClient
            get() = OkHttpClient.Builder()
                .addNetworkInterceptor(StethoInterceptor())
                .build()

        private fun okClient(): OkHttpClient {
            return OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.MINUTES)
                .writeTimeout(10, TimeUnit.MINUTES)
                .retryOnConnectionFailure(true)
                .readTimeout(10, TimeUnit.MINUTES)
                .build()
        }

        fun getInstance(): RetrofitService {
            return Retrofit.Builder()
                .baseUrl("http://192.46.210.81:9001/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(stethoClient)
                .client(okClient())
                .build()
                .create(RetrofitService::class.java)
        }
    }
}