package com.AashuDeveloper.vegiwell.Model

class FoodModel {
    var key : String?=null
    var name:String?=null
    var image:String?=null
    var id:String?=null
    var description:String?=null
    var price:Long=0
    var nameoncake:String?=null
    var discount:Long=0
    var addon:List<AddonModel> = ArrayList<AddonModel>()
    var size:List<SizeModel> =ArrayList<SizeModel>()
    var btn : Int?=null

    var ratingvalue : Double = 0.toDouble()
    var  ratingCount : Long = 0.toLong()
    var userSelectedAddon:MutableList<AddonModel>?=null
    var userSelectedSize:SizeModel?=null
}