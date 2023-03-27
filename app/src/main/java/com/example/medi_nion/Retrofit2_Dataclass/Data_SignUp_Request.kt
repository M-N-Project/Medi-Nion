package com.example.medi_nion.Retrofit2_Dataclass

import android.provider.ContactsContract.CommonDataKinds.Nickname
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.Objects

//data class Data_SignUp_Request(
//    val student: List<Data_SignUp_Request>
//
//)

data class Data_SignUp_Request(
    @SerializedName("userType")
        @Expose val userType: String,
    @SerializedName("userDept")
        @Expose val userDept: String,
    @SerializedName("nickname")
        @Expose val nickname: String,
    @SerializedName("id")
        @Expose val id: String,
    @SerializedName("passwd")
        @Expose val passwd: String,
//    @SerializedName("businessChan")
//        @Expose val businessChan: Int

//    val userType: Any,
//    val userDept: Any,
//    val nickname: Any,
//    val id: Any,
//    val passwd: Any,
//    val businessChan: Int
)

