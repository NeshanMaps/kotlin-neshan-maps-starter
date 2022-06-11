package org.neshan.kotlinsample.network

import retrofit2.http.GET
import org.neshan.kotlinsample.model.address.NeshanAddress
import retrofit2.Call
import retrofit2.http.Headers
import retrofit2.http.Query

interface ReverseService {
    // TODO: replace "YOUR_API_KEY" with your api key
    @Headers("Api-Key: service.kREahwU7lND32ygT9ZgPFXbwjzzKukdObRZsnUAJ")
    @GET("/v2/reverse")
    fun getReverse(@Query("lat") lat: Double?, @Query("lng") lng: Double?): Call<NeshanAddress>
}