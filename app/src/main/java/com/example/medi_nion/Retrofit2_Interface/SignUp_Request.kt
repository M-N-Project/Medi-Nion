package com.example.medi_nion.Retrofit2_Interface

import com.example.medi_nion.Retrofit2_Dataclass.Data_SignUp_Request
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface SignUp_Request {
    @FormUrlEncoded
    @POST("SignUP.php")
    fun getUser(
        @Field("nickname") nickname: String,
        @Field("id") id: String,
        @Field("passwd") passwd :String,
        @Field("userType") userType :String,
        @Field("userDept") userDept :String,
        @Field("businessChan") businessChan: Int
    ): Call<Data_SignUp_Request>
}