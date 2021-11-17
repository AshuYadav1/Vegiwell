package com.AashuDeveloper.vegiwell.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.AashuDeveloper.vegiwell.Callback.IRecyclerItemClickListener
import com.AashuDeveloper.vegiwell.Common.Common
import com.AashuDeveloper.vegiwell.Database.*
import com.AashuDeveloper.vegiwell.EventBus.CountCartEvent
import com.AashuDeveloper.vegiwell.EventBus.FoodItemClick
import com.AashuDeveloper.vegiwell.Model.FoodModel
import com.AashuDeveloper.vegiwell.R
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus


class MyFoodListAdapter (internal var context: Context,
                           internal var foodList: List<FoodModel>) :

        RecyclerView.Adapter<MyFoodListAdapter.MyViewHolder>() {

    private var compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val cartDataSource : CartDataSource



    init{
        compositeDisposable = CompositeDisposable()
        cartDataSource = LocalCartDataSource(CartDatabase.getInstance(context).cartDao())
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(foodList.get(position).image).into(holder.img_food_image!!)
        holder.txt_food_name!!.setText(foodList.get(position).name)
        holder.txt_food_price!!.setText(StringBuilder("Rs").append(foodList.get(position).price.toString()))


        //event
         holder.setlistner(object:IRecyclerItemClickListener{
             override fun onItemClick(view: View, pos: Int) {
                 Common.foodSelected = foodList.get(pos)
                 Common.foodSelected!!.key = pos.toString()
                 EventBus.getDefault().postSticky(FoodItemClick(true,foodList.get(pos)))
             }

         })


         holder.img_cart!!.setOnClickListener {
               val cartitem = Cartitem()
             cartitem.uid = Common.currentuser!!.uid!!
             cartitem.userPhone = Common.currentuser!!.Phone!!

             cartitem.foodId = foodList.get(position).id!!
             cartitem.foodName = foodList.get(position).name!!
             cartitem.foodImage=  foodList.get(position).image!!
             cartitem.foodPrice= foodList.get(position).price!!.toDouble()
             cartitem.foodQuantity=1
             cartitem.foodExtraPrice=0.0
             cartitem.foodAddon="Default"
             cartitem.foodSize="Default"

             cartDataSource.getItemWithAllOptionInCart(Common.currentuser!!.uid!!,
                     cartitem.foodId,
                     cartitem.foodSize,
                     cartitem.foodAddon)
                     .subscribeOn(Schedulers.io())
                     .observeOn(AndroidSchedulers.mainThread())
                     .subscribe(object : SingleObserver<Cartitem>{
                         override fun onSubscribe(d: Disposable) {
                             

                         }

                         override fun onSuccess(cartItemFromDB: Cartitem) {
                             if (cartItemFromDB.equals(cartitem))
                             {
                                 cartItemFromDB.foodExtraPrice = cartitem.foodExtraPrice
                                 cartItemFromDB.foodAddon = cartitem.foodAddon
                                 cartItemFromDB.foodSize = cartitem.foodSize
                                 cartItemFromDB.foodQuantity = cartItemFromDB.foodQuantity + cartitem.foodQuantity
                                 cartDataSource.updateCart(cartItemFromDB)
                                         .subscribeOn(Schedulers.io())
                                         .observeOn(AndroidSchedulers.mainThread())
                                         .subscribe(object : SingleObserver<Int>{
                                             override fun onSubscribe(d: Disposable) {

                                             }

                                             override fun onSuccess(t: Int) {
                                                 Toast.makeText(context,"Update Cart Success",Toast.LENGTH_SHORT).show()
                                                 EventBus.getDefault().postSticky(CountCartEvent(true))

                                             }

                                             override fun onError(e: Throwable) {
                                                 Toast.makeText(context,"[Update Cart]"+e.message,Toast.LENGTH_SHORT).show()

                                             }


                                         })

                             }
                             else
                             {
                                 compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartitem)
                                         .subscribeOn(Schedulers.io())
                                         .observeOn(AndroidSchedulers.mainThread())
                                         .subscribe({
                                             Toast.makeText(context,"Add to cart Success",Toast.LENGTH_SHORT).show()
                                         },{
                                             t: Throwable? -> Toast.makeText(context,"[INSERT CART]" +t!!.message,Toast.LENGTH_SHORT).show()


                                         }))

                             }

                         }

                         override fun onError(e: Throwable) {
                             if(e.message!!.contains("empty"))

                             {
                                 compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartitem)
                                         .subscribeOn(Schedulers.io())
                                         .observeOn(AndroidSchedulers.mainThread())
                                         .subscribe({
                                             Toast.makeText(context,"Add to cart Success",Toast.LENGTH_SHORT).show()
                                         },{
                                             t: Throwable? -> Toast.makeText(context,"[INSERT CART]" +t!!.message,Toast.LENGTH_SHORT).show()


                                         }))
                             }
                             else
                                 Toast.makeText(context,"[CART ERROR]" +e.message,Toast.LENGTH_SHORT).show()

                         }


                     })














         }

    }

    fun onStop(){
        if (compositeDisposable !=null)
           compositeDisposable.clear()
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyFoodListAdapter.MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_food_item,parent,false))
    }


    override fun getItemCount(): Int {
        return  foodList.size
    }



    inner class MyViewHolder(iteamView: View): RecyclerView.ViewHolder(iteamView), View.OnClickListener {


        var txt_food_name : TextView?=null
        var txt_food_price : TextView?=null

        var img_food_image: ImageView?=null
        var img_fav: ImageView?=null
        var img_cart: ImageView?=null

        internal  var listener : IRecyclerItemClickListener?=null

        fun  setlistner(listener: IRecyclerItemClickListener)
        {
            this.listener=listener
        }



        init {

            txt_food_name = iteamView.findViewById(R.id.txt_food_name)as TextView
            txt_food_price = iteamView.findViewById(R.id.txt_food_price) as TextView
            img_food_image = iteamView.findViewById(R.id.img_food_image)as ImageView
            img_fav = iteamView.findViewById(R.id.img_fav)as ImageView
            img_cart = iteamView.findViewById(R.id.img_cart)as ImageView
            itemView.setOnClickListener(this)

        }

        override fun onClick(view: View?) {
            listener!!.onItemClick(view!!,adapterPosition)
        }


    }




}