package com.AashuDeveloper.vegiwell.Adapter

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.AashuDeveloper.vegiwell.Model.CategoryModel
import com.AashuDeveloper.vegiwell.Model.CommentModel
import com.AashuDeveloper.vegiwell.R
import kotlinx.android.synthetic.main.layout_comment_item.view.*

class MyCommentAdapter(internal var context: Context,
                        internal var commentList:List<CommentModel>) :
        RecyclerView.Adapter<MyCommentAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_comment_item,parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val timestamp = commentList.get(position).commentTimeStamp!!["TimeStamp"]!!.toString().toLong()
        holder.txt_comment_date!!.text = DateUtils.getRelativeTimeSpanString(timestamp)
        holder.txt_comment_name!!.text = commentList.get(position).name
        holder.txt_comment!!.text=commentList.get(position).comment

    }

    override fun getItemCount(): Int {
       return commentList.size
    }







    inner class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        var txt_comment_name : TextView?=null
        var txt_comment_date : TextView?=null
        var txt_comment      : TextView?=null
        var rating_bar       : RatingBar?=null


        init {
            txt_comment = itemView.findViewById(R.id.txt_comment) as TextView
            txt_comment_name = itemView.findViewById(R.id.txt_comment_name) as TextView
            txt_comment_date = itemView.findViewById(R.id.txt_comment_date) as TextView
            rating_bar =itemView.findViewById(R.id.rating_bar) as RatingBar
            
        }

    }



}