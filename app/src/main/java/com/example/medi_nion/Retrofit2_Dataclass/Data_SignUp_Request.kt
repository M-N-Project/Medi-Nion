package com.example.medi_nion.Retrofit2_Dataclass

data class Data_SignUp_Request(
    val nickname: String,
    val id: String,
    val passwd: String,
    val userType: String,
    val userDept: String
)
