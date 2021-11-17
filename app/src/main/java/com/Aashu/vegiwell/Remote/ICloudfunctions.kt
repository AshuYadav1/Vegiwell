package com.AashuDeveloper.vegiwell.Remote

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface ICloudfunctions {
    @GET (value = "")
    fun GetCustomToken(@Query("access_token")accesstoken:String):Observable<ResponseBody>
}