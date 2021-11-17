package com.AashuDeveloper.vegiwell.Database

import com.AashuDeveloper.vegiwell.Model.order
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import java.math.BigDecimal

interface CartDataSource {

    fun getAllCart(uid: String): Flowable<List<Cartitem>>

    fun countItemInCart(uid:String): Single<Int>


    fun sumPrice(uid:String) : Single<Double>


    fun getItemInCart(foodId:String,uid:String): Single<Cartitem>




    fun insertOrReplaceAll(vararg cartItems: Cartitem): Completable


    fun updateCart(cart:Cartitem) : Single<Int>


    fun deleteCart(cart: Cartitem) : Single<Int>








    fun cleancart(uid:String): Single<Int>




    fun getItemWithAllOptionInCart(uid: String,foodId: String,foodSize: String,foodAddon: String):Single<Cartitem>
    fun findCart(uid: String): Single<Cartitem>


}