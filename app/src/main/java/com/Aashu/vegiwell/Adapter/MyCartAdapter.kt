package com.AashuDeveloper.vegiwell.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.AashuDeveloper.vegiwell.Database.*
import com.bumptech.glide.Glide
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton
import com.AashuDeveloper.vegiwell.EventBus.UpdateItemInCart
import com.AashuDeveloper.vegiwell.Model.FoodModel
import com.AashuDeveloper.vegiwell.R
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.layout_cart_item.view.*
import org.greenrobot.eventbus.EventBus

 class MyCartAdapter(internal var context: Context,
                    internal var cartItems: List<Cartitem>) :

        RecyclerView.Adapter<MyCartAdapter.MyViewHolder>()  {


    internal var compositeDisposable:CompositeDisposable
    internal var cartDataSource: CartDataSource

    init {
        compositeDisposable = CompositeDisposable()
        cartDataSource = LocalCartDataSource(CartDatabase.getInstance(context).cartDao())



    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_cart_item,parent,false))

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(cartItems[position].foodImage)
                .into(holder.img_cart)
         holder.txt_food_name.text = StringBuilder(cartItems[position].foodName!!)
         holder.txt_food_price.text = StringBuilder("").append(cartItems[position].foodPrice + cartItems[position].foodExtraPrice)

         holder.number_button.number = cartItems[position].foodQuantity.toString()
          // Event
        
         holder.number_button.setOnValueChangeListener { view, oldValue, newValue ->
             cartItems[position].foodQuantity = newValue
             EventBus.getDefault().postSticky(UpdateItemInCart  (cartItems[position]))
         }
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

     fun getItemAtPosition(pos: Int): Cartitem {
         return cartItems[pos]
     }

     inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        lateinit var img_cart: ImageView
        lateinit var txt_food_name: TextView
        lateinit var txt_food_price:TextView
        lateinit var number_button: ElegantNumberButton



        init {

            img_cart = itemView.findViewById(R.id.img_cart) as ImageView
            txt_food_name=itemView.findViewById(R.id.txt_food_name)as TextView
            txt_food_price=itemView.findViewById(R.id.txt_food_price) as TextView
            number_button = itemView.findViewById(R.id.number_button)as ElegantNumberButton

        }
    }


}