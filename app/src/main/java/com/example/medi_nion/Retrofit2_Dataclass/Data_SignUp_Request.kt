package com.example.medi_nion.Retrofit2_Dataclass

data class Data_SignUp_Request (
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