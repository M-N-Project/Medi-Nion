package com.example.medi_nion.Retrofit2_Interface

import com.example.medi_nion.Retrofit2_Dataclass.Data_BoardDetail_Request
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface BoardDetail_Request {
    @FormUrlEncoded
    @POST("/postInfoDetail.php")
    fun BoardDetail(
        @Field("board") board: String,
        @Field("post_num") post_num: String
    ): Call<Data_BoardDetail_Request>
}