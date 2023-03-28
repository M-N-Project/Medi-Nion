package com.example.medi_nion.Retrofit2_Interface

import com.example.medi_nion.Retrofit2_Dataclass.Data_UpdateBoard
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface UpdateBoard_Request {
    @FormUrlEncoded
    @POST("/UpdateBoard.php")
    fun updateBoard (
        @Field("id") id: Any,
        @Field("board") board: Any,
        @Field("post_num") post_num: Any,
        @Field("title") title: Any,
        @Field("content") content: Any,
        @Field("time") time: Any,
        @Field("image1") image1: Any,
        @Field("image2") image2: Any
    ): Call<Data_UpdateBoard>
}
