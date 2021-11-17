package com.AashuDeveloper.vegiwell.ui.foodlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.AashuDeveloper.vegiwell.Common.Common
import com.AashuDeveloper.vegiwell.Model.FoodModel

class FoodListViewModel: ViewModel() {


    private var mutablefoodModelListData : MutableLiveData<List<FoodModel>>?=null

    fun getMutableFoodModelListData() : MutableLiveData<List<FoodModel>>{

        if(mutablefoodModelListData == null)
            mutablefoodModelListData = MutableLiveData()

        mutablefoodModelListData!!.value = Common.categoryselected!!.foods
        return mutablefoodModelListData!!
    }
}