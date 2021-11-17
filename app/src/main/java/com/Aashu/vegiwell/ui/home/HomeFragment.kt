package com.AashuDeveloper.vegiwell.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.AashuDeveloper.vegiwell.Adapter.MyBestDealsAdapter
import com.AashuDeveloper.vegiwell.Adapter.MyPopularCategoriesAdapter
import com.AashuDeveloper.vegiwell.R

class HomeFragment : Fragment(), View.OnClickListener {


    private lateinit var homeViewModel: HomeViewModel


    var recyclerView: RecyclerView? = null
    var layoutAnimationController: LayoutAnimationController? = null
    var image_click: ImageButton? = null


    //var viewPager: LoopingViewPager?=null

    private var imageClickListener: ImageClickListener? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {


        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)



        initView(root)

        homeViewModel.popularList.observe(viewLifecycleOwner, Observer {
            val listData = it
            val adapter = MyPopularCategoriesAdapter(requireContext(), listData)

            recyclerView!!.adapter = adapter
            recyclerView!!.layoutAnimation = layoutAnimationController


        })

        homeViewModel.bestDealList.observe(viewLifecycleOwner, Observer {

            val adapter = MyBestDealsAdapter(requireContext(), it, false)
            //viewPager!!.adapter = adapter
        })


        image_click?.setOnClickListener(this)


        return root
    }


    private fun initView(root: View) {



        layoutAnimationController = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_item_from_left)
        //viewPager = root.findViewById(R.id.viewpager)as LoopingViewPager


        recyclerView = root.findViewById(R.id.recycler_popular) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        image_click = root.findViewById(R.id.image_click) as ImageButton
        image_click!!.setOnClickListener(this)


    }


    override fun onClick(v: View?) {              //

        v?.let {
            when (it.id) {
                R.id.image_click -> {
                    val testing = ""
                    imageClickListener?.onImageClick()
                }
                else -> {

                }
            }
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        //  if (context is ImageClickListener)
        imageClickListener = context as ImageClickListener
    }

    interface ImageClickListener {
        fun onImageClick()
    }
}


//override fun onResume() {
// super.onResume()
// viewPager!!.resumeAutoScroll()


// override fun onPause() {
//viewPager!!.pauseAutoScroll()
//super.onPause()

//}


