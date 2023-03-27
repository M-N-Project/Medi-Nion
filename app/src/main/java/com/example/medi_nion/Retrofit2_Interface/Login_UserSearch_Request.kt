package com.example.medi_nion.Retrofit2_Interface

import com.example.medi_nion.Retrofit2_Dataclass.Data_Login_UserSearch_Request
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface Login_UserSearch_Request {
    @FormUrlEncoded
    @POST("/UserSearch.php")
    fun LoginUserSearch(
        @Field("id") id: String
    ): Call<Data_Login_UserSearch_Request>
}