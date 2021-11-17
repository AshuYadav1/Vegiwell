package com.AashuDeveloper.vegiwell.Database

import androidx.room.*
import com.AashuDeveloper.vegiwell.Model.order
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import java.math.BigDecimal
import java.math.BigInteger


@Dao

interface CartDao {
    @Query("SELECT * FROM Cart WHERE uid=:uid")
    fun getAllCart(uid:String): Flowable<List<Cartitem>>


    @Query("SELECT COUNT(foodQuantity) FROM Cart WHERE uid=:uid")
    fun CountItemInCart(uid:String): Single<Int>

    @Query("SELECT SUM((foodPrice+foodExtraPrice)* foodQuantity) as value FROM Cart WHERE uid=:uid")
    fun sumPrice(uid:String) : Single<Double>


    @Query("SELECT * FROM Cart WHERE uid=:uid")
    fun findCart(uid: String): Single<Cartitem>



    @Query("SELECT * FROM Cart WHERE foodId=:foodId AND uid=:uid")
    fun getItemInCart(foodId:String,uid:String):Single<Cartitem>


    @Insert(onConflict = OnConflictStrategy.REPLACE)

    fun InsertOrReplaceAll(vararg cartItems: Cartitem):Completable

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateCart(cart:Cartitem) : Single<Int>

    @Delete
    fun deleteCart(cart:Cartitem) : Single<Int>





    @Query("DELETE FROM Cart WHERE uid=:uid")
    fun cleancart(uid:String):Single<Int>

    @Query("SELECT * FROM Cart WHERE foodId=:foodId AND uid=:uid And foodSize=:foodSize AND foodAddon=:foodAddon")
    fun getItemWithAllOptionInCart(uid: String,foodId: String,foodSize: String,foodAddon: String):Single<Cartitem>



}