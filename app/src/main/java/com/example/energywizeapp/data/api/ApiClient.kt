package com.example.energywizeapp.data.api

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiService {
    @GET("v1/latest-prices.json")
    fun getLatestPrices(): Call<ApiResponse>
}

object RetrofitClient {
    private const val BASE_URL = "https://api.porssisahko.net/"

    val apiService: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ApiService::class.java)
    }
}

