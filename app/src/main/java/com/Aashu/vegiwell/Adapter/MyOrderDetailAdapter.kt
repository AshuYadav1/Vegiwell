package com.AashuDeveloper.vegiwell.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.AashuDeveloper.vegiwell.Database.Cartitem
import com.AashuDeveloper.vegiwell.Model.AddonModel
import com.AashuDeveloper.vegiwell.Model.SizeModel
import com.AashuDeveloper.vegiwell.R
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MyOrderDetailadapter(internal var context: Context,
                           internal var cartItemList: MutableList<Cartitem>) : RecyclerView.Adapter<MyOrderDetailadapter.MyViewHolder>() {

    val gson: Gson = Gson()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txt_food_name: TextView? = null
        var txt_food_quantity: TextView? = null
        var txt_food_addon: TextView? = null
        var txt_food_size: TextView? = null
        var img_carts: ImageView? = null
        var txt_price: TextView? = null

        init {
            img_carts = itemView.findViewById(R.id.img_carts) as ImageView
            txt_food_name = itemView.findViewById(R.id.txt_food_name) as TextView
            txt_food_size = itemView.findViewById(R.id.txt_food_size) as TextView
            txt_food_quantity = itemView.findViewById(R.id.txt_food_quantity) as TextView
            txt_food_addon = itemView.findViewById(R.id.txt_food_addon) as TextView
            txt_price= itemView.findViewById(R.id.txt_price)as TextView

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_order_detail_item, parent, false))

    }

    override fun getItemCount(): Int {
        return cartItemList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(cartItemList[position].foodImage)
                .into(holder.img_carts!!)
        holder.txt_food_name!!.setText(StringBuilder().append(cartItemList[position].foodName))
        holder.txt_food_quantity!!.setText(StringBuilder("Quantity: ").append(cartItemList[position].foodQuantity))
        holder.txt_price!!.setText(StringBuilder("Price: ").append(cartItemList[position].foodPrice))



        if (cartItemList[position].foodSize
                        .equals("Default"))
            holder.txt_food_size!!.setText(java.lang.StringBuilder("Size: Default"))
        else {
            val sizeModel = gson.fromJson<SizeModel>(cartItemList[position].foodSize, SizeModel::class.java)
            holder.txt_food_size!!.setText(java.lang.StringBuilder("Size: ").append(sizeModel.name))
        }


        if (cartItemList[position].foodAddon.trim() != "Default") {
            // This is just a test
            val json = cartItemList[position].foodAddon
            Log.i(MyOrderDetailadapter::class.simpleName, "Addon Json: $json")
            val addonModels: List<AddonModel> = gson.fromJson(cartItemList[position].foodAddon,
                    object : TypeToken<List<AddonModel?>?>() {}.type)

            val addonString = StringBuilder()
            if (addonModels.isNotEmpty()) {
                for (addonModel in addonModels) addonString.append(addonModel.name).append(",")
                addonString.delete(addonString.length - 1, addonString.length)
                holder.txt_food_addon!!.setText(StringBuilder("Addon: ").append(addonString))

            }

        } else
            holder.txt_food_addon!!.setText(StringBuilder("Addon: Default"))


    }


}