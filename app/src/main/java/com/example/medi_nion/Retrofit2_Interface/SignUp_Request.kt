package com.example.medi_nion.Retrofit2_Interface

import retrofit2.Call
import com.example.medi_nion.Retrofit2_Dataclass.Data_SignUp_Request
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface SignUp_Request {
    @FormUrlEncoded
    @POST("/SignUP.php")
    fun getUser(
        @Field("userType") userType: String,
        @Field("userDept") userDept: String,
        @Field("nickname") nickname: String,
        @Field("id") id: String,
        @Field("passwd") passwd: String,
        @Field("userMedal") userMedal: Int
        ): Call<Data_SignUp_Request>
}