package com.AashuDeveloper.vegiwell.Callback

import com.AashuDeveloper.vegiwell.Model.BestDealModel
import com.AashuDeveloper.vegiwell.Model.PopularCategoryModel

interface IBestDealLoadCallBack {

    fun onBestDealLoadSuccess(bestDealList: List<BestDealModel>)
    fun onBestDealLoadFailed(message:String)
}