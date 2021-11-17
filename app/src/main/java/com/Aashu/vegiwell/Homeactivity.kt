package com.AashuDeveloper.vegiwell

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.AashuDeveloper.vegiwell.Common.Common
import com.AashuDeveloper.vegiwell.Database.CartDataSource
import com.AashuDeveloper.vegiwell.Database.CartDatabase
import com.AashuDeveloper.vegiwell.Database.LocalCartDataSource
import com.AashuDeveloper.vegiwell.EventBus.*
import com.AashuDeveloper.vegiwell.Model.CategoryModel
import com.AashuDeveloper.vegiwell.Model.FoodModel
import com.AashuDeveloper.vegiwell.ui.home.HomeFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dmax.dialog.SpotsDialog
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.fragment_food_list.*
import kotlinx.android.synthetic.main.fragment_home.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class Homeactivity : AppCompatActivity(), HomeFragment.ImageClickListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var cartDataSource: CartDataSource
    private lateinit var navController: NavController
    private var drawer: DrawerLayout? = null
    private var navView: NavigationView? = null
    private var dialog: android.app.AlertDialog? = null

    private var menuItemClick = -1





    override fun onResume() {
        super.onResume()
        countCartitem()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homeactivity)

        dialog = SpotsDialog.Builder().setContext(this).setCancelable(false).build()

        cartDataSource = LocalCartDataSource(CartDatabase.getInstance(this).cartDao())

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val whatsappbtn: FloatingActionButton = findViewById(R.id.whatsapp_btn)
        whatsappbtn.setOnClickListener {
            startActivity(Intent(this, whatsapp_msg::class.java))
        }

        val callbutton: FloatingActionButton = findViewById(R.id.call_btn)
        callbutton.setOnClickListener{
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + "+918169811157")
            startActivity(dialIntent)
        }


        val fab: FloatingActionButton = findViewById(R.id.fab_btn)
        fab.setOnClickListener { view ->

            navController.navigate(R.id.nav_cart)


        }


        drawer = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
                setOf(
                        R.id.nav_home, R.id.nav_Menu, R.id.nav_food_detail, R.id.nav_cart, R.id.nav_sign_out, R.id.about_us
                ), drawer
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView?.setupWithNavController(navController)


        var headerView = navView?.getHeaderView(0)
        var txt_user = headerView?.findViewById<TextView>(R.id.txt_user)


           Common.setSpanString("Hello, ", Common.currentuser!!.Name,txt_user)
          /*  Common.currentuser?.Name?.let { name -> Common.setSpanString("Hello, ", name, txt_user) }*/
        navView?.setNavigationItemSelectedListener { p0 ->

            if (menuItemClick != p0.itemId) {
                p0.isChecked = true
                menuItemClick = p0.itemId

                if (p0.itemId == R.id.nav_sign_out) {
                    signout()
                } else if (p0.itemId == R.id.nav_home) {

                    navController.navigate(R.id.nav_home)
                } else if (p0.itemId == R.id.nav_cart) {

                    navController.navigate(R.id.nav_cart)
                } else if (p0.itemId == R.id.nav_Menu) {

                    navController.navigate(R.id.nav_Menu)
                } else if (p0.itemId == R.id.nav_about_us) {
                    navController.navigate(R.id.nav_about_us)
                    p0.isChecked = false
                    menuItemClick = -1
                } else if (p0.itemId == R.id.nav_Myorder) {
                    navController.navigate(R.id.nav_Myorder)
                }
            }
            /*     if (p0.itemId == R.id.nav_sign_out) {
                     signout()
                 } else if (p0.itemId == R.id.nav_home) {
                     if (menuItemClick != p0.itemId)
                         navController.navigate(R.id.nav_home)
                 } else if (p0.itemId == R.id.nav_cart) {
                     if (menuItemClick != p0.itemId)
                         navController.navigate(R.id.nav_cart)
                 } else if (p0.itemId == R.id.nav_Menu) {
                     if (menuItemClick != p0.itemId)
                         navController.navigate(R.id.nav_Menu)
                 } else if (p0.itemId == R.id.nav_about_us) //
                 {
                     if (menuItemClick != p0.itemId)
                         navController.navigate(R.id.nav_about_us)
                 } else if (p0.itemId == R.id.nav_Myorder) {
                     if (menuItemClick != p0.itemId)
                         navController.navigate(R.id.nav_Myorder)
                 }*/


            drawer!!.closeDrawers()
            true
        }

        countCartitem()
    }


    private fun signout() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Sign out")
                .setMessage("Do you really  want to exit? ")
                .setNegativeButton("CANCEL", { dialogInterface, _ -> dialogInterface.dismiss() })
                .setPositiveButton("Ok") { dialogInterface, _ ->
                    Common.foodSelected = null
                    Common.categoryselected = null
                    Common.currentuser = null
                    FirebaseAuth.getInstance().signOut()

                    val intent = Intent(this@Homeactivity, Loginactivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }

        val dialog = builder.show()
        dialog.show()


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.homeactivity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.action_settings) {
            val link = Uri.parse("https://www.instagram.com/vegiwell/")
            val linkIntent = Intent(Intent.ACTION_VIEW, link)
            startActivity(linkIntent)

             true
        } else
            super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()

    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().removeAllStickyEvents()
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onCategorySelected(event: CategoryClick) {
        if (event.isSuccess) {
            //Toast.makeText(this,"Click to "+event.category.name,Toast.LENGTH_SHORT).show()
            findNavController(R.id.nav_host_fragment).navigate(R.id.nav_food_list)
        }
    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onFoodSelected(event: FoodItemClick) {
        if (event.isSuccess) {
            //Toast.makeText(this,"Click to "+event.category.name,Toast.LENGTH_SHORT).show()
            findNavController(R.id.nav_host_fragment).navigate(R.id.nav_food_detail)
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onHideFABEvent(event: HideFABCart) {
        if (event.isHide) {
            //Toast.makeText(this,"Click to "+event.category.name,Toast.LENGTH_SHORT).show()
            fab_btn.hide()
        } else
            fab_btn.show()
    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onCountCartEvent(event: CountCartEvent) {
        if (event.isSuccess) {
            //Toast.makeText(this,"Click to "+event.category.name,Toast.LENGTH_SHORT).show()
            countCartitem()
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onPopularFoodItemClick(event: PopularFoodItemClick) {
        if (event.PopularCategoryModel != null) {
            dialog!!.show()
            FirebaseDatabase.getInstance()
                    .getReference("Category")

                     //.child(event.PopularCategoryModel!!.category_id!!)
                     .child(event.PopularCategoryModel!!.menu_id!!)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(p0: DataSnapshot) {
                            if (p0.exists()) {
                                Common.categoryselected = p0.getValue(CategoryModel::class.java)
                                Common.categoryselected!!.menu_id = p0.key

                                // load food
                                FirebaseDatabase.getInstance()
                                        .getReference("Category")
                                        .child(event.PopularCategoryModel!!.menu_id!!)
                                       .child("foods")
                                        .orderByChild("id")
                                        .equalTo(event.PopularCategoryModel.food_id)
                                        .limitToLast(1)
                                        .addListenerForSingleValueEvent(object : ValueEventListener {
                                            override fun onDataChange(p0: DataSnapshot) {
                                                if (p0.exists()) {
                                                    for (foodSnapShot in p0.children) {
                                                        Common.foodSelected =
                                                                foodSnapShot.getValue(FoodModel::class.java)
                                                        Common.foodSelected!!.key = foodSnapShot.key


                                                    }
                                                    navController!!.navigate(R.id.nav_food_list)
                                                } else {

                                                    Toast.makeText(this@Homeactivity, "Item doesn't exists", Toast.LENGTH_SHORT).show()
                                                }
                                                dialog!!.dismiss()

                                            }

                                            override fun onCancelled(p0: DatabaseError) {
                                                dialog!!.dismiss()
                                                Toast.makeText(this@Homeactivity, "" + p0.message, Toast.LENGTH_SHORT).show()
                                            }

                                        })
                            } else {
                                dialog!!.dismiss()
                                Toast.makeText(this@Homeactivity, "Item doesn't exists", Toast.LENGTH_SHORT).show()
                            }

                        }

                        override fun onCancelled(p0: DatabaseError) {
                            dialog!!.dismiss()
                            Toast.makeText(this@Homeactivity, "" + p0.message, Toast.LENGTH_SHORT).show()

                        }

                    })
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public fun onMenuItemBack(event: MenuItemBack) {
        menuItemClick = -1
        if (supportFragmentManager.backStackEntryCount > 0)
            supportFragmentManager.popBackStack()
    }

    private fun countCartitem() {

        cartDataSource.countItemInCart(Common.currentuser!!.uid!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<Int> {


                    override fun onSuccess(t: Int) {
                        fab_btn.count = t

                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        if (!e.message!!.contains("Query returned empty"))
                            Toast.makeText(this@Homeactivity, "[COUNT CART]" + e.message, Toast.LENGTH_SHORT).show()
                        else
                            fab_btn.count = 0

                    }

                })


    }

    override fun onImageClick() {
        // navView?.menu?.findItem(2)?.isChecked = true
        Toast.makeText(applicationContext, "Opening category", Toast.LENGTH_SHORT).show()
        navController.navigate(R.id.nav_Menu)
    }
}