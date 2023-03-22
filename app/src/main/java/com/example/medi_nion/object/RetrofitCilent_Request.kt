package com.example.medi_nion.`object`

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitCilent_Request {
    private var instance: Retrofit? = null
    private val gson = GsonBuilder().setLenient().create()
    private val IP_Address = "seonho.dothome.co.kr"

    fun getInstance(): Retrofit {
        if(instance == null) {
            instance = Retrofit.Builder()
                .baseUrl("http://$IP_Address/SignUP.php")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
        return instance!!
    }
}