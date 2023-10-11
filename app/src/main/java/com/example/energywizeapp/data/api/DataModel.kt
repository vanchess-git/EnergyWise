package com.example.energywizeapp.data.api

import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("prices") val prices: List<PriceData>
)

data class PriceData(
    @SerializedName("price") val price: Double,
    @SerializedName("startDate") val startDate: String,
    @SerializedName("endDate") val endDate: String
)