package com.AashuDeveloper.vegiwell.Callback

import com.AashuDeveloper.vegiwell.Model.PopularCategoryModel

interface IPopularLoadCallback {

    fun onPopularLoadSuccess(popularModelList: List<PopularCategoryModel>)
    fun onPopularLoadFailed(message:String)
}