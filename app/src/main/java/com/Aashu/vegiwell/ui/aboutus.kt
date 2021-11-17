package com.Aashu.vegiwell.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.AashuDeveloper.vegiwell.R
import com.shashank.sony.fancyaboutpagelib.FancyAboutPage

class aboutus : Fragment() {





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


         val root = inflater.inflate(R.layout.fragment_aboutus, container, false)
        initviews(root)

        return  root

    }

    private fun initviews(root: View?) {


        val fancyAboutPage = root!!.findViewById<FancyAboutPage>(R.id.fancyaboutpage)

        fancyAboutPage.setCover(R.drawable.s_)
        fancyAboutPage.setName("Vegiwell")
        fancyAboutPage.setDescription("Vegiwell We Deliver vegetables cakes and Many More.")
        fancyAboutPage.setAppIcon(R.drawable.newimage)
        fancyAboutPage.setAppName("Vegiwell")
        fancyAboutPage.setVersionNameAsAppSubTitle("5.0.1")
        fancyAboutPage.setAppDescription(
            """
   Get Vegetables, Fruits, cakes safely with every Order on Vegiwell (Oder Accept Only In Vasai and Nallasopara ) products including farm-fresh fruits and vegetables, groceries,cakes more at the best prices. Enjoy hassle-free online grocery shopping and contactless home delivery at just a click of a button.

FEATURES AND SERVICES
♦ Enjoy low prices and great offers: Buy at low prices with great offers including discounts
♦ Fast and Secure Checkout ( COD Available)
♦ Order Delivered within 2 Hr
♦ Accept Order Through whatsapp Also..
    """.trimIndent()
        )
        fancyAboutPage.addEmailLink("mail.vegiwell@gmail.com")
        fancyAboutPage.addFacebookLink("https://www.facebook.com/profile.php?id=100070499647534")



    }



}