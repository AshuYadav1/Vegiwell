package com.AashuDeveloper.vegiwell.Adapter

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.AashuDeveloper.vegiwell.Callback.IRecyclerItemClickListener
import com.bumptech.glide.Glide
import com.AashuDeveloper.vegiwell.Common.Common
import com.AashuDeveloper.vegiwell.Database.Cartitem
import com.AashuDeveloper.vegiwell.Model.OrderItem
import com.AashuDeveloper.vegiwell.Model.order
import com.AashuDeveloper.vegiwell.R
import kotlinx.android.synthetic.main.layout_order_item.view.*
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*

class MyOrderItemAdapter(private val context: Context,
                         private val orderList:MutableList<OrderItem>):
        RecyclerView.Adapter<MyOrderItemAdapter.MyViewHolder>() {

      internal  var calender : Calendar
      internal var simpleDateFormat : SimpleDateFormat

      init {
          calender = Calendar.getInstance()
          simpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")

      }

    inner  class MyViewHolder(itemView:View)  : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal  var img_order: ImageView?=null
        internal var text_order_date: TextView?=null
        internal var txt_order_status:TextView?=null
        internal var txt_order_number:TextView?=null
        internal var txt_order_comment:TextView?=null


        init {
            img_order = itemView.findViewById(R.id.img_order) as ImageView
            text_order_date = itemView.findViewById(R.id.text_order_date) as TextView
            txt_order_status = itemView.findViewById(R.id.txt_order_status) as TextView
            txt_order_number = itemView.findViewById(R.id.txt_order_number) as TextView
            txt_order_comment = itemView.findViewById(R.id.txt_order_comment) as TextView

            itemView.setOnClickListener(this)




        }
        internal var iRecyclerItemClickListENER:IRecyclerItemClickListener ?=null

        fun setListner(IRecyclerItemClickListENER: IRecyclerItemClickListener){

            this.iRecyclerItemClickListENER = IRecyclerItemClickListENER

        }

        override fun onClick(p0: View?) {
            iRecyclerItemClickListENER!!.onItemClick(p0!!,adapterPosition)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

     return MyViewHolder(LayoutInflater.from(context!!)
             .inflate(R.layout.layout_order_item,parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val orderItem = orderList[position]

        val order = orderItem.order
        val cartItem = orderItem.item

        //This line
      Glide.with(context)
              .load(cartItem.foodImage)
              .into(holder.img_order!!)

        calender.timeInMillis =order.createDate
        val date = Date(order.createDate)
        holder.text_order_date!!.text = StringBuilder(Common.getDateOfWeek(calender.get(Calendar.DAY_OF_WEEK)))
                .append(" ")
                .append(simpleDateFormat.format(date))
                holder.txt_order_number!!.text = StringBuilder("Order Number:   ") . append(order.ordernumber)
             holder.txt_order_comment!!.text = StringBuilder("price :") . append(order.totalPayment)
             holder.txt_order_status!!.text = StringBuilder("Status: ") .append(Common.convertStatusToText(order.orderStatus))
        holder.setListner(object :IRecyclerItemClickListener{
            override fun onItemClick(view: View, pos: Int) {
                showDialog(order.cartItemList)
            }

        })
    }
    private fun showDialog(cartItemList: List<Cartitem>?) {
        val layout_dialog = LayoutInflater.from(context).inflate(R.layout.layout_dialog_order_detail,null)
        val builder = AlertDialog.Builder(context)
        builder.setView(layout_dialog)

        val btn_ok = layout_dialog.findViewById<View>(R.id.btn_ok) as Button
        val recycler_order_detail = layout_dialog.findViewById<View>(R.id.recycler_order_detail)as RecyclerView
        recycler_order_detail.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        recycler_order_detail.layoutManager = layoutManager
        recycler_order_detail.addItemDecoration(DividerItemDecoration(context,layoutManager.orientation))
        val adapter = MyOrderDetailadapter(context ,cartItemList!!.toMutableList())
        recycler_order_detail.adapter = adapter

        val dialog = builder.create()
        dialog.show()

        // custom dialog
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.GREEN)) /// do transparent
        dialog.window!!.setGravity(Gravity.CENTER)


        btn_ok.setOnClickListener{

            dialog.dismiss()

        }

    }

    override fun getItemCount(): Int {
       return orderList.size
    }





    fun getItemPosition(position: Int):OrderItem{
        return  orderList[position]
    }

    fun  setItemAtPosition(position: Int,order: OrderItem)
    {
        orderList[position] = order
    }

    fun removeItem(pos: Int) {
        orderList.removeAt(pos)

    }


}