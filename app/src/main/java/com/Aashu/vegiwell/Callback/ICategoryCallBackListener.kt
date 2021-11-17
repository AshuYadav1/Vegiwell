package com.AashuDeveloper.vegiwell.Callback

import com.AashuDeveloper.vegiwell.Model.CategoryModel
import com.AashuDeveloper.vegiwell.Model.PopularCategoryModel

interface ICategoryCallBackListener {

    fun onCategoryLoadSuccess(categoryList: List<CategoryModel>)
    fun onCategoryLoadFailed(message:String)
}