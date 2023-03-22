package com.example.medi_nion.`interface`

import com.example.medi_nion.dataclass.Data_SignUp_Request
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
        @Field("passwd") passwd: String,
        @Field("userType") userType: String,
        @Field("userDept") userDept: String
    ): Call<Data_SignUp_Request>
}