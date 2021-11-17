package com.AashuDeveloper.vegiwell.Callback

import com.AashuDeveloper.vegiwell.Model.order

interface ILoadOrderCallbackListener {

    fun onLoadOrderSuccess(orderList: List<order>)
    fun onLoadOrderFailed(message:String)
}