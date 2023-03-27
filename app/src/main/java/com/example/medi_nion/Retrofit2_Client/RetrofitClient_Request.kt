package com.example.medi_nion.Retrofit2_Client

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitClient_Request {
    private var instance: Retrofit? = null
    private val gson: Gson = GsonBuilder().setLenient().create()

    fun getInstance(): Retrofit {
        if(instance == null) {
            Log.d("newjeans", "wowowowo")
            instance = Retrofit.Builder()
                .baseUrl("http://seonho.dothome.co.kr/")
                .client(OkHttpClient()) //있으나 없으나,,
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
        return instance!!
    }
}
