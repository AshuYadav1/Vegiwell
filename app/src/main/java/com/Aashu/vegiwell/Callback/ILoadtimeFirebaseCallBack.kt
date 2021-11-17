package com.AashuDeveloper.vegiwell.Callback

import com.AashuDeveloper.vegiwell.Model.CommentModel
import com.AashuDeveloper.vegiwell.Model.order

interface ILoadtimeFirebaseCallBack {
    fun onLoadTimeSuccess(order: order,estimatedTimeMs:Long)
    fun ontLoadTimeFailed(message:String)
}