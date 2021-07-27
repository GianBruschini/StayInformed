package com.gian.stayinformed.service

import com.gian.stayinformed.interfaces.CovidApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Covid19ApiSingleton {
    private var retrofit: Retrofit? = null
    private val BASE_URL = "https://api.covid19api.com/"

    fun getService(): CovidApi? {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
        }
        return retrofit!!.create(CovidApi::class.java)
    }
}