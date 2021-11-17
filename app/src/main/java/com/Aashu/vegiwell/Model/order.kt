package com.AashuDeveloper.vegiwell.Model

import com.AashuDeveloper.vegiwell.Database.Cartitem

class order {

    var userId: String? = null
    var userName: String? = null
    var userPhone: String? = null
    var shippingAddress: String? = null
    var trsansactionId: String? = null
    var totalPayment: Double = 0.toDouble()
    var finalPayment: Double = 0.toDouble()
    var isCod: Boolean = false
    var discount: Int = 0
    var naam:String?=null
    var cartItemList: List<Cartitem>? = null
    var createDate: Long = 0
    var ordernumber: String? = null
    var orderStatus: Int = 0

    override fun toString(): String {
        return "order(userId=$userId, userName=$userName, userPhone=$userPhone, shippingAddress=$shippingAddress, trsansactionId=$trsansactionId, " +
                "totalPayment=$totalPayment, finalPayment=$finalPayment, isCod=$isCod, discount=$discount, cartItemList=$cartItemList, createDate=$createDate," +
                " ordernumber=$ordernumber, orderStatus=$orderStatus,naam=$naam)"
    }


}

data class OrderItem(val item: Cartitem, val order: order)