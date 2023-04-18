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
        @Field("userMedal") userMedal: Int,
        @Field("identity1") identity1: String,
        @Field("identity2") identity2: String,
        @Field("identity_check") identity_check: String,
        @Field("img1") img1: String,
        @Field("img2") img2: String
        ): Call<Data_SignUp_Request>
}