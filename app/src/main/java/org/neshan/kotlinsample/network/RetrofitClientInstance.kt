package org.neshan.kotlinsample.network

import retrofit2.Retrofit
import okhttp3.OkHttpClient
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClientInstance {

    private const val BASE_URL = "https://api.neshan.org/"
    private var retrofit: Retrofit? = null

    val retrofitInstance: Retrofit?
        get() {
            val client = OkHttpClient.Builder()
                .build()
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()
            }
            return retrofit
        }
}