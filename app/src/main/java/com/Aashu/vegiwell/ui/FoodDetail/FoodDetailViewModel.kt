package com.AashuDeveloper.vegiwell.ui.FoodDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.AashuDeveloper.vegiwell.Common.Common
import com.AashuDeveloper.vegiwell.Model.CommentModel
import com.AashuDeveloper.vegiwell.Model.FoodModel

class FoodDetailViewModel : ViewModel() {
    private var mutableLiveDataFood:MutableLiveData<FoodModel>?=null
    private var mutableLiveDataComment:MutableLiveData<CommentModel>?=null


    init {

        mutableLiveDataComment = MutableLiveData()
    }

    fun getMutableLiveDataFood():MutableLiveData<FoodModel>{

        if(mutableLiveDataFood==null)

            mutableLiveDataFood=MutableLiveData()
        mutableLiveDataFood!!.value = Common.foodSelected
        return mutableLiveDataFood!!


    }



    fun getMutableLiveDatacomment():MutableLiveData<CommentModel>{

        if(mutableLiveDataComment==null)

            mutableLiveDataComment=MutableLiveData()

        return mutableLiveDataComment!!


    }




    fun setCommentModel(commentModel: CommentModel) {

        if (mutableLiveDataComment !=null)
        {
            mutableLiveDataComment!!.value=(commentModel)
        }

    }

    fun setFoodModel(foodModel: FoodModel) {
        if (mutableLiveDataFood != null)
            mutableLiveDataFood!!.value = foodModel
    }


}