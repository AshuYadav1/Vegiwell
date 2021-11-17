package com.AashuDeveloper.vegiwell.Database


import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity


@Entity(tableName = "Cart", primaryKeys = ["uid", "foodId", "foodSize", "foodAddon"])
class Cartitem {


    @NonNull

    @ColumnInfo(name = "foodId")
    var foodId: String = ""

    @ColumnInfo(name = "foodName")


    var foodName: String? = null

    @ColumnInfo(name = "foodImage")
    var foodImage: String? = null


    @ColumnInfo(name = "foodPrice")
    var foodPrice: Double = 0.0

    @ColumnInfo(name = "foodQuantity")
    var foodQuantity: Int = 0

    @NonNull
    @ColumnInfo(name = "foodAddon")
    var foodAddon: String = ""

    @NonNull
    @ColumnInfo(name = "foodSize")
    var foodSize: String = ""

    @ColumnInfo(name = "userPhone")
    var userPhone: String = ""


    @ColumnInfo(name = "foodExtraPrice")
    var foodExtraPrice: Double = 0.0



    @NonNull
    @ColumnInfo(name = "uid")
    var uid: String = ""

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is Cartitem)
            return false

        val cartItem = other as Cartitem?
        return cartItem!!.foodId == this.foodId &&
                cartItem.foodAddon == this.foodAddon &&
                cartItem.foodSize == this.foodSize

    }

    override fun toString(): String {
        return "Cartitem(foodId='$foodId', foodName=$foodName, foodImage=$foodImage, foodPrice=$foodPrice, foodQuantity=$foodQuantity, foodAddon='$foodAddon', " +
                "foodSize='$foodSize', userPhone='$userPhone', foodExtraPrice=$foodExtraPrice, uid='$uid')"
    }


}