package com.example.medi_nion.Retrofit2_Interface

import com.example.medi_nion.Retrofit2_Dataclass.Data_Nickname
import retrofit2.Call
import com.example.medi_nion.Retrofit2_Dataclass.Data_SignUp_Request
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface SignUp_Nickname {
    @FormUrlEncoded
    @POST("/php/SignUP.php")
    fun getUser(
        @Field("nickname") nickname: String
    ): Call<Data_Nickname>
}