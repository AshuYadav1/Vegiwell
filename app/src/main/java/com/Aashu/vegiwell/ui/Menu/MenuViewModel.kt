package com.AashuDeveloper.vegiwell.ui.Menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.AashuDeveloper.vegiwell.Callback.ICategoryCallBackListener
import com.AashuDeveloper.vegiwell.Common.Common
import com.AashuDeveloper.vegiwell.Model.BestDealModel
import com.AashuDeveloper.vegiwell.Model.CategoryModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MenuViewModel : ViewModel(), ICategoryCallBackListener {
    override fun onCategoryLoadSuccess(categoryList: List<CategoryModel>) {
        categoriesListMutable!!.value = categoryList
    }

    override fun onCategoryLoadFailed(message: String) {
        messageError. value = message
    }



    private var categoriesListMutable : MutableLiveData<List<CategoryModel>>?=null
    private var messageError:MutableLiveData<String> = MutableLiveData()
    private val categoryCallBackListener: ICategoryCallBackListener

    init {
        categoryCallBackListener = this
    }

    fun  getCategoryList():MutableLiveData<List<CategoryModel>>{
        if (categoriesListMutable == null)
        {
            categoriesListMutable = MutableLiveData()
            loadCategory()
        }
        return categoriesListMutable!!

    }

     fun getMessageError() : MutableLiveData<String>{
         return  messageError
     }

   fun loadCategory() {
        val tempList = ArrayList<CategoryModel>()
        val categoryRef = FirebaseDatabase.getInstance().getReference(Common.CATEGORY_REF)
        categoryRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                for (itemSnapShot in p0!!.children)
                {
                    val model = itemSnapShot.getValue<CategoryModel>(CategoryModel::class.java )
                    model!!.menu_id = itemSnapShot.key
                    tempList.add(model!!)
                }
               categoryCallBackListener.onCategoryLoadSuccess(tempList)

            }

            override fun onCancelled(p0: DatabaseError) {
                categoryCallBackListener.onCategoryLoadFailed((p0.message))
            }

        })
    }


}