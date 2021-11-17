package com.AashuDeveloper.vegiwell.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.AashuDeveloper.vegiwell.Callback.IRecyclerItemClickListener
import com.AashuDeveloper.vegiwell.Common.Common
import com.AashuDeveloper.vegiwell.EventBus.CategoryClick
import com.AashuDeveloper.vegiwell.Model.CategoryModel
import com.AashuDeveloper.vegiwell.Model.PopularCategoryModel
import com.AashuDeveloper.vegiwell.R
import de.hdodenhof.circleimageview.CircleImageView
import org.greenrobot.eventbus.EventBus

class MyCategoriesAdapter (internal var context: Context,
                           internal var  categoryList: List<CategoryModel>) :

        RecyclerView.Adapter<MyCategoriesAdapter.MyViewHolder>() {
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(categoryList.get(position).image).into(holder.category_image!!)
        holder.category_name!!.setText(categoryList.get(position).name)

        //event

        holder.setlistner(object : IRecyclerItemClickListener{
            override fun onItemClick(view: View, pos: Int) {
                Common.categoryselected = categoryList.get(pos)
                EventBus.getDefault().postSticky(CategoryClick(true,categoryList.get(pos)))
            }

        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCategoriesAdapter.MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_category_item,parent,false))
    }


    override fun getItemCount(): Int {
        return  categoryList.size
    }


    override fun getItemViewType(position: Int): Int {
        return if (categoryList.size==1)
         Common.DEFAULT_COLUMN_COUNT
        else(
                if (categoryList.size %2 == 0)
                    Common.DEFAULT_COLUMN_COUNT
                else
                     if (position > 1 && position == categoryList.size-1) Common.FULL_WIDTH_COLUMN else Common.DEFAULT_COLUMN_COUNT
        )
    }

    fun getCategoryList(): List<CategoryModel> {
        return categoryList

    }

    inner class MyViewHolder(iteamView: View):RecyclerView.ViewHolder(iteamView), View.OnClickListener {
        override fun onClick(view: View?) {
                 listener!!.onItemClick(view!!,adapterPosition)
        }



        var category_name : TextView?=null

        var category_image: ImageView?=null
        internal  var listener : IRecyclerItemClickListener?=null

        fun  setlistner(listener: IRecyclerItemClickListener)
        {
            this.listener=listener
        }

        init {

            category_name = iteamView.findViewById(R.id.category_name)as TextView
            category_image = iteamView.findViewById(R.id.category_image) as ImageView
            itemView.setOnClickListener(this)
        }




    }




}