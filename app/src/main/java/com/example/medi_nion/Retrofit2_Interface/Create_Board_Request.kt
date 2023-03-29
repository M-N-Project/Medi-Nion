package com.example.medi_nion.Retrofit2_Interface

import com.example.medi_nion.Retrofit2_Dataclass.Data_CreateBoard_Request
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface Create_Board_Request {
    @FormUrlEncoded
    @POST("/createBoard.php")
    fun Create_Board(
        @Field("update") update: String,
        @Field("id") id: String,
        @Field("board") board: String,
        @Field("title") title: String,
        @Field("content") content: String,
        @Field("image1") image1: String,
        @Field("image2") image2: String
    ): Call<Data_CreateBoard_Request>
}