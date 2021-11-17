package com.AashuDeveloper.vegiwell.ui.cart

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.AashuDeveloper.vegiwell.Common.Common
import com.AashuDeveloper.vegiwell.Database.CartDataSource
import com.AashuDeveloper.vegiwell.Database.CartDatabase
import com.AashuDeveloper.vegiwell.Database.Cartitem
import com.AashuDeveloper.vegiwell.Database.LocalCartDataSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class CartViewModel : ViewModel(){

    private val compositeDisposable:CompositeDisposable
    private var cartDataSource:CartDataSource?=null
    private var mutableLiveDataCartItem:MutableLiveData<List<Cartitem>>?=null

    init {
        compositeDisposable = CompositeDisposable()
    }

    fun initCartdataSource(context: Context){
        cartDataSource = LocalCartDataSource(CartDatabase.getInstance(context).cartDao())
    }

    fun getMutableLiveDataCartItem(): MutableLiveData<List<Cartitem>>{
        if (mutableLiveDataCartItem==null)
             mutableLiveDataCartItem= MutableLiveData()
        getCartItems()
        return mutableLiveDataCartItem!!

    }

    private fun getCartItems(){
         compositeDisposable.addAll(cartDataSource!!.getAllCart(Common.currentuser!!.uid!!)
                 .subscribeOn(Schedulers.io())
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribe({Cartitem ->
                     mutableLiveDataCartItem!!.value = Cartitem
                 },{t:Throwable? -> mutableLiveDataCartItem!!.value = null }))

    }

    fun onStop(){
        compositeDisposable.clear()
    }


}