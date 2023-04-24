package com.example.medi_nion

import android.os.Message

data class SaraminDTO (var message: Message? = null) {
    data class Message(var result: Result? = null) {
        data class Result(var translatedText: String? = null)
    }
}