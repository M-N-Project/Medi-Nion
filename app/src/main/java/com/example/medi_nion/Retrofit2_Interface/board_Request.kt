package com.example.medi_nion.Retrofit2_Interface

import com.example.medi_nion.Retrofit2_Dataclass.Data_Board_Request
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface board_Request {
    @FormUrlEncoded
    @POST("/Board.php")
    fun Board_Retrofit (
        @Field("board") board: String,
        @Field("userType") userType: String,
        @Field("userDept") userDept: String
    ): Call<Data_Board_Request>
}