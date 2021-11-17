package com.AashuDeveloper.vegiwell.ui.view_order

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.AashuDeveloper.vegiwell.Model.order

class MyorderViewModel : ViewModel() {

    val mutableLiveDataOrderList:MutableLiveData<List<order>>
    init {
        mutableLiveDataOrderList = MutableLiveData()
    }

    fun setMutableLIveDataOrderList(orderList:List<order>)
    {
        mutableLiveDataOrderList.value=orderList
    }
}