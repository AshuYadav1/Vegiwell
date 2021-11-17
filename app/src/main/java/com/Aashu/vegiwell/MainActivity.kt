package com.AashuDeveloper.vegiwell

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    private lateinit var OnboardingitemsAdapter:OnboardingitemsAdapter
    private lateinit var indicatorContainer : LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        setOnboardingiteams()


    }
    private fun setOnboardingiteams() {


        OnboardingitemsAdapter = OnboardingitemsAdapter(

                listOf(
                        OnboardingItem(
                                onboardingImage = R.drawable.sssss,
                                Title = "Fresh Vegetable",
                                Description = "We Provide Fresh Vegetable Direct From Mandi ."
                        ),

                        OnboardingItem(
                                onboardingImage = R.drawable.s_,
                                Title = "Fast Delivery",
                                Description = "We provide fast Delivery Within 2Hr  at your door "
                        ),

                        OnboardingItem(
                                onboardingImage = R.drawable.t_,
                                Title = "Easy Payment",
                                Description = "Online and COD option are Available"
                        ),


                        )

        )


        val Onboardingviewpager = findViewById<ViewPager2>(R.id.OnboardingViewpager)
        Onboardingviewpager.adapter=OnboardingitemsAdapter
        findViewById<ImageView>(R.id.imagenext).setOnClickListener {
             if (Onboardingviewpager.currentItem +1 < OnboardingitemsAdapter.itemCount){
                 Onboardingviewpager.currentItem+=1
             }
            else{
                Loginactivity()
             }
        }

        findViewById<TextView>(R.id.Textskip).setOnClickListener {
            Loginactivity()
        }
        findViewById<MaterialButton>(R.id.buttonGetStarted).setOnClickListener {


            Loginactivity()
        }
    }

  private fun Loginactivity(){

      startActivity(Intent(applicationContext,Loginactivity::class.java))

      finish()
  }

}