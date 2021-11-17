package com.AashuDeveloper.vegiwell.ui.Menu

import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import androidx.appcompat.widget.SearchView
import android.icu.text.Transliterator
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils

import android.view.animation.LayoutAnimationController
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast


import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.AashuDeveloper.vegiwell.Adapter.MyBestDealsAdapter
import com.AashuDeveloper.vegiwell.Adapter.MyCategoriesAdapter
import com.AashuDeveloper.vegiwell.Common.Common
import com.AashuDeveloper.vegiwell.Common.SpacesItemDecoration
import com.AashuDeveloper.vegiwell.EventBus.MenuItemBack
import com.AashuDeveloper.vegiwell.Model.CategoryModel
import com.AashuDeveloper.vegiwell.R
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.fragment_category.*
import org.greenrobot.eventbus.EventBus

class MenuFragment : Fragment() {

    private lateinit var menuViewModel: MenuViewModel
    private lateinit var dialog: AlertDialog
    private var adapter: MyCategoriesAdapter? = null
    private var recycler_menu : RecyclerView?=null
    var layoutAnimationController: LayoutAnimationController?=null





    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        menuViewModel =
                ViewModelProvider(this).get(MenuViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_category, container, false)

        initViews(root)
        menuViewModel.getMessageError().observe(viewLifecycleOwner, Observer {

            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()


        })

        menuViewModel.getCategoryList().observe(viewLifecycleOwner, Observer {
            dialog.dismiss()
            adapter = MyCategoriesAdapter(requireContext(), it)
            recycler_menu!!.adapter = adapter
            recycler_menu!!.layoutAnimation = layoutAnimationController

        })



        return root
    }


    private fun initViews(root : View) {
        setHasOptionsMenu(true)

        dialog = SpotsDialog.Builder().setContext(context)
                .setCancelable(false).build()

        dialog.show()
         layoutAnimationController = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_item_from_left)
        recycler_menu = root.findViewById(R.id.recycler_menu) as RecyclerView
        recycler_menu!!.setHasFixedSize(true)
        val layoutManager = GridLayoutManager(context, 2)
        layoutManager.orientation = RecyclerView.VERTICAL
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {

                return if (adapter != null) {
                    when (adapter!!.getItemViewType(position)) {
                        Common.DEFAULT_COLUMN_COUNT -> 1
                        Common.FULL_WIDTH_COLUMN -> 2
                        else -> -1
                    }
                } else
                    -1

            }


        }
        recycler_menu!!.layoutManager = layoutManager
        recycler_menu!!.addItemDecoration(SpacesItemDecoration(5))
    }

   /* override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu,menu)

        val menuItem = menu.findItem(R.id.action_search)
        val searchManager = requireActivity().getSystemService(Context.SEARCH_SERVICE)as SearchManager
        val searchView = menuItem.actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))

        // event

        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(s: String?): Boolean {
                startSearch(s!!)
              return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

        val closeButton = searchView.findViewById<View>(R.id.search_close_btn) as ImageView
        val ed = searchView.findViewById<View>(R.id.search_src_text) as EditText

        // clear text
        ed.setText("")
        //claer query

        searchView.setQuery("",false)

        searchView.onActionViewCollapsed()
        menuItem.collapseActionView()
        menuViewModel.loadCategory()
    }

    private fun startSearch(s: String) {
        val resultCategory = ArrayList<CategoryModel>()

        for (i in 0 until adapter!!.getCategoryList().size)
        {
           val categoryModel = adapter!!.getCategoryList()[i]
            if (categoryModel.name!!.toLowerCase().contains(s))
                resultCategory.add(categoryModel)
        }
         menuViewModel.getCategoryList().value = resultCategory




    }*/

    override fun onDestroy() {
        EventBus.getDefault().postSticky(MenuItemBack())
        super.onDestroy()
    }
}




