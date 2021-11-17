package com.AashuDeveloper.vegiwell

import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class OnboardingitemsAdapter(private  val  Onboardingitems : List<OnboardingItem>):
      RecyclerView.Adapter<OnboardingitemsAdapter.OnboardingItemViewHolder>()
   {

       override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingItemViewHolder {
         return OnboardingItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.onboarding_item_container,parent,false)
                 )

       }



       override fun onBindViewHolder(holder: OnboardingItemViewHolder, position: Int) {
          holder.bind(Onboardingitems [position])
       }

       override fun getItemCount(): Int {
         return Onboardingitems.size
       }

       inner class OnboardingItemViewHolder(View:View):RecyclerView.ViewHolder(View){
         private val imageonboarding = View.findViewById<ImageView>(R.id.imageonboarding)
         private val texttitle = View.findViewById<TextView>(R.id.TextTitle)
         private val textdescription = View.findViewById<TextView>(R.id.textdescription)


         fun bind(OnboardingItem : OnboardingItem){

             imageonboarding.setImageResource(OnboardingItem.onboardingImage)
             texttitle.text=OnboardingItem.Title
             textdescription.text=OnboardingItem.Description
         }

    }
}