package com.AashuDeveloper.vegiwell.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.bumptech.glide.Glide
import com.AashuDeveloper.vegiwell.Callback.IRecyclerItemClickListener
import com.AashuDeveloper.vegiwell.EventBus.PopularFoodItemClick
import com.AashuDeveloper.vegiwell.Model.PopularCategoryModel
import com.AashuDeveloper.vegiwell.R
import de.hdodenhof.circleimageview.CircleImageView
import org.greenrobot.eventbus.EventBus

class MyPopularCategoriesAdapter(internal var context:Context,
                                  internal var popularCategoryModel: List<PopularCategoryModel>) :

 RecyclerView.Adapter<MyPopularCategoriesAdapter.MyViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_popular_categories_item,parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(popularCategoryModel.get(position).image).into(holder.category_image!!)
        holder.category_name!!.setText(popularCategoryModel.get(position).name)

        holder.setlistner(object :IRecyclerItemClickListener{
            override fun onItemClick(view: View, pos: Int) {
                EventBus.getDefault()
                        .postSticky(PopularFoodItemClick(popularCategoryModel[pos]))
            }
        })
    }

    override fun getItemCount(): Int {
        return popularCategoryModel.size
    }

        inner class MyViewHolder(iteamView:View):RecyclerView.ViewHolder(iteamView), View.OnClickListener {



            var category_name :TextView?=null

            var category_image:CircleImageView?=null

            internal  var listener : IRecyclerItemClickListener?=null

            fun  setlistner(listener: IRecyclerItemClickListener)
            {
                this.listener=listener
            }


            init {

                 category_name = iteamView.findViewById(R.id.txt_category_name)as TextView
                 category_image = iteamView.findViewById(R.id.category_image) as CircleImageView

                itemView.setOnClickListener(this)
            }

            override fun onClick(p0: View?) {
                listener!!.onItemClick(p0!!,adapterPosition)

            }


        }


}