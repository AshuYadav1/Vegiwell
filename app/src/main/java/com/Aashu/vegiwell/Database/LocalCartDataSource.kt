package com.AashuDeveloper.vegiwell.Database

import com.AashuDeveloper.vegiwell.Model.order
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import java.math.BigDecimal

class LocalCartDataSource(private val cartDao: CartDao,
                         ) : CartDataSource{

    override fun getItemWithAllOptionInCart(uid: String, foodId: String, foodSize: String, foodAddon: String): Single<Cartitem> {
        return cartDao.getItemWithAllOptionInCart(uid, foodId, foodSize, foodAddon)
    }
    override fun getAllCart(uid: String): Flowable<List<Cartitem>> {
        return cartDao.getAllCart(uid)
    }

    override fun countItemInCart(uid: String): Single<Int> {
        return cartDao.CountItemInCart(uid)

    }
    override fun findCart(uid: String): Single<Cartitem>{
        return cartDao.findCart(uid)
    }

    override fun sumPrice(uid: String): Single<Double> {
        return cartDao.sumPrice(uid)
    }



    override fun getItemInCart(foodId: String, uid: String): Single<Cartitem> {
       return cartDao.getItemInCart(foodId,uid)
    }

    override fun insertOrReplaceAll(vararg cartItems: Cartitem): Completable {
        return  cartDao.InsertOrReplaceAll(*cartItems)
    }

    override fun updateCart(cart: Cartitem): Single<Int> {
          return  cartDao.updateCart(cart)
    }

    override fun deleteCart(cart: Cartitem): Single<Int> {
        return cartDao.deleteCart(cart)

    }




    override fun cleancart(uid: String): Single<Int> {
        return cartDao.cleancart(uid)

    }



}