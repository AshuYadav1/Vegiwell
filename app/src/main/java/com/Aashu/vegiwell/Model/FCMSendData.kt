package com.AashuDeveloper.vegiwell.Model

data class FCMSendData(val to: String,
                       val notification: FcmNotification,
                       var data: Map<String, String>)

data class FcmNotification(
        val title: String,
        val body: String,
)