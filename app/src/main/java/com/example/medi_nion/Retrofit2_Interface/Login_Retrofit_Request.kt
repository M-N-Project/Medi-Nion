package com.example.medi_nion.Retrofit2_Interface;

import com.example.medi_nion.Retrofit2_Dataclass.Data_Login_Request;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

interface Login_Retrofit_Request {
    @FormUrlEncoded
    @POST("/Login.php")
    fun Login(
        @Field("id") id: String,
        @Field("passwd") passwd: String
    ): Call<Data_Login_Request>
}