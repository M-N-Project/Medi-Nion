package com.example.medi_nion.Retrofit2_Dataclass

import java.util.Objects

data class Data_SignUp_Request(
    val userType: String,
    val userDept: String,
    val nickname: String,
    val id: String,
    val passwd: String
)

data class Data_Login_Request (
    val id: String,
    val passwd: String,
)

data class Data_Login_UserSearch_Request (
    val id: String
)

data class Data_UpdateBoard ( //수정될지 검증 안해봄
    val id: Any,
    val board: Any,
    val post_num: Any,
    val title: Any,
    val content: Any,
    val time: Any,
    val image1: Any,
    val image2: Any
    )

data class Data_CreateBoard_Request (
    val update: String,
    val id: String,
    val board: String,
    val title: String,
    val content: String,
    val image1: String,
    val image2: String
        )

data class Data_Board_Request (
    val board: String
        )

data class Data_BoardDetail_Request (
    val board: String,
    val post_num: Int
        )

