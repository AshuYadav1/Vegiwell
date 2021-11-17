package com.AashuDeveloper.vegiwell.Remote


import com.AashuDeveloper.vegiwell.Model.FCMResponse2
import com.AashuDeveloper.vegiwell.Model.FCMSendData
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface IFCMService {
    @Headers(
            "Content-Type:application/json",
            "Authorization: key=AAAASFFYQcE:APA91bGNWPD5Qn9kikC0jadsY4qW_6Bnm6zmRsJkyNdH_ldaMSuT_71UBcWBOIq04MGkjBZ_kVid0srhhPiGqjvckg0P4LyxSPoTYakqRSu8MROuZX-1K1WO2sc_xI85SLZeuMUwSkxJ"
    )
    @POST("fcm/send")
    fun sendNotification(@Body body: FCMSendData): Observable<FCMResponse2>

}