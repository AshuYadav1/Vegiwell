package com.AashuDeveloper.vegiwell.ui.cart

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.*
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.AashuDeveloper.vegiwell.Adapter.MyCartAdapter
import com.AashuDeveloper.vegiwell.Callback.ILoadtimeFirebaseCallBack
import com.AashuDeveloper.vegiwell.Callback.IMyButtonCallback
import com.AashuDeveloper.vegiwell.Common.Common
import com.AashuDeveloper.vegiwell.Common.MySwipeHelper
import com.AashuDeveloper.vegiwell.Database.CartDataSource
import com.AashuDeveloper.vegiwell.Database.CartDatabase
import com.AashuDeveloper.vegiwell.Database.LocalCartDataSource
import com.AashuDeveloper.vegiwell.EventBus.CountCartEvent
import com.AashuDeveloper.vegiwell.EventBus.HideFABCart
import com.AashuDeveloper.vegiwell.EventBus.MenuItemBack
import com.AashuDeveloper.vegiwell.EventBus.UpdateItemInCart
import com.AashuDeveloper.vegiwell.Model.FCMResponse2
import com.AashuDeveloper.vegiwell.Model.FCMSendData
import com.AashuDeveloper.vegiwell.Model.FcmNotification
import com.AashuDeveloper.vegiwell.Model.order
import com.AashuDeveloper.vegiwell.R
import com.AashuDeveloper.vegiwell.Remote.IFCMService
import com.AashuDeveloper.vegiwell.Remote.RetrofitFCMClient
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class Cartfragment : Fragment(), ILoadtimeFirebaseCallBack {


    private var cartDataSource: CartDataSource? = null
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var recyclerViewState: Parcelable? = null
    private lateinit var cartViewModel: CartViewModel
    private lateinit var btn_place_order: Button
    lateinit var ifcmService: IFCMService
    var adapter: MyCartAdapter? = null


    var box: ImageView? = null
    var txt_empty_cart: TextView? = null
    var txt_total_price: TextView? = null
    var group_place_holder: CardView? = null
    var recycler_cart: RecyclerView? = null


    lateinit var listener: ILoadtimeFirebaseCallBack

    override fun onResume() {
        super.onResume()
        calculateTotalPrice()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true)

        EventBus.getDefault().postSticky(HideFABCart(true))
        cartViewModel =
            ViewModelProvider(this).get(CartViewModel::class.java)

        cartViewModel.initCartdataSource(requireContext())


        val root = inflater.inflate(R.layout.fragment_cart, container, false)

        initViews(root)
        cartViewModel.getMutableLiveDataCartItem().observe(viewLifecycleOwner, Observer {
            if (it == null || it.isEmpty()) {
                recycler_cart!!.visibility = View.GONE
                group_place_holder!!.visibility = View.GONE
                //txt_empty_cart!!.visibility=View.VISIBLE
                box!!.visibility = View.VISIBLE


            } else {
                recycler_cart!!.visibility = View.VISIBLE
                group_place_holder!!.visibility = View.VISIBLE
                //txt_empty_cart!!.visibility = View.GONE
                box!!.visibility = View.GONE


                adapter = MyCartAdapter(requireContext(), it)
                recycler_cart!!.adapter = adapter

            }

        })


        return root
    }

    private fun initViews(root: View) {


        setHasOptionsMenu(true)

        ifcmService = RetrofitFCMClient.getinstance().create(IFCMService::class.java)

        listener = this
        cartDataSource = LocalCartDataSource(CartDatabase.getInstance(requireContext()).cartDao())
        recycler_cart = root.findViewById(R.id.recycler_cart) as RecyclerView
        recycler_cart!!.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        recycler_cart!!.layoutManager = layoutManager
        recycler_cart!!.addItemDecoration(DividerItemDecoration(context, layoutManager.orientation))

        val swipe = object : MySwipeHelper(requireContext(), recycler_cart!!, 200) {
            override fun instantiateMyButton(
                viewHolder: RecyclerView.ViewHolder,
                buffer: MutableList<MyButton>
            ) {

                buffer.add(
                    MyButton(context!!,
                        "Delete",
                        30,
                        0,
                        Color.parseColor("#FFA500"),
                        object : IMyButtonCallback {
                            override fun onClick(pos: Int) {

                                val deleteItem = adapter!!.getItemAtPosition(pos)
                                cartDataSource!!.deleteCart(deleteItem)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(object : SingleObserver<Int> {
                                        override fun onSubscribe(d: Disposable) {

                                        }

                                        override fun onSuccess(t: Int) {
                                            adapter!!.notifyItemRemoved(pos)
                                            EventBus.getDefault().postSticky(CountCartEvent(true))
                                            total()
                                            sumCart()
                                            Toast.makeText(
                                                context,
                                                "Delete item Success",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                        override fun onError(e: Throwable) {
                                            Toast.makeText(
                                                context,
                                                "" + e.message,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                    })


                            }

                        })
                )
            }

        }

        //txt_empty_cart = root.findViewById(R.id.txt_empty_cart) as TextView
        box = root.findViewById(R.id.box) as ImageView
        txt_total_price = root.findViewById(R.id.txt_total_price) as TextView
        group_place_holder = root.findViewById(R.id.group_place_holder) as CardView
        btn_place_order = root.findViewById(R.id.btn_place_order) as Button

        btn_place_order.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Final Step!")

            val view = LayoutInflater.from(context).inflate(R.layout.layout_place_order, null)

            val edt_address = view.findViewById<View>(R.id.edt_address) as EditText
            val rdi_home = view.findViewById<View>(R.id.rdi_home_address) as RadioButton
            // val rdi_ship_to_this_address = view.findViewById<View>(R.id.rdi_ship_this_address) as RadioButton
            val rdi_cod = view.findViewById<View>(R.id.rdi_cod) as RadioButton

            // data

            edt_address.setText(Common.currentuser!!.Address!!)

            // Event

            rdi_home.setOnCheckedChangeListener { compoundButton, b ->
                if (b) {
                    edt_address.setText(Common.currentuser!!.Address!!)
                }
            }


            builder.setView(view)
            builder.setNegativeButton("No") { dialogInterface, _ -> dialogInterface.dismiss() }
                .setPositiveButton("Yes") { dialogInterface, _ ->
                    paymentCOD(edt_address.text.toString())
                }

            val dialog = builder.create()
            dialog.show()

        }


    }

    private fun total() {
        cartDataSource


    }


    private fun paymentCOD(Address: String) {
        compositeDisposable.add(cartDataSource!!.getAllCart(Common.currentuser!!.uid!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { cartItemList ->

                cartDataSource!!.sumPrice(Common.currentuser!!.uid!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : SingleObserver<Double> {
                        override fun onSubscribe(d: Disposable) {

                        }

                        override fun onSuccess(totalPrice: Double) {
                            val finalPrice = totalPrice
                            val order = order()
                            order.userId = Common.currentuser!!.uid!!
                            order.userName = Common.currentuser!!.Name!!
                            order.userPhone = Common.currentuser!!.Phone!!
                            order.shippingAddress = Address

                            order.cartItemList = cartItemList
                            order.totalPayment = totalPrice
                            order.finalPayment = finalPrice
                            order.discount = 0
                            order.isCod = true
                            order.trsansactionId = "Cash On Delivery"

                            // sumbit to firebase

                            syncLocalTimeWithServerTime(order)
                        }

                        override fun onError(t: Throwable) {

                            //Toast.makeText(context, "" + t!!.message, Toast.LENGTH_SHORT).show()
                        }

                    })
            })
    }

    private fun syncLocalTimeWithServerTime(order: order) {
        val offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset")
        offsetRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val offset = p0.getValue(Long::class.java)
                val estimatedServerTimeInMs = System.currentTimeMillis() + offset!!
                val sdf = SimpleDateFormat("MMM dd yyyy, HH:mm")
                val date = Date(estimatedServerTimeInMs)
                Log.d("Vegiwell", "" + sdf.format(date))
                listener.onLoadTimeSuccess(order, estimatedServerTimeInMs)

            }

            override fun onCancelled(p0: DatabaseError) {
                listener.ontLoadTimeFailed(p0.message)
            }

        })
    }

    private fun writeOrderToFirebase(order: order) {
        if (!order.cartItemList.isNullOrEmpty()) {
            FirebaseDatabase.getInstance()
                .getReference(Common.ORDER_REF)
                .child(Common.createOrderNumber())
                .setValue(order)
                .addOnFailureListener { e ->
                    Toast.makeText(
                        requireContext(),
                        "" + e.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        cartDataSource!!.cleancart(Common.currentuser!!.uid!!)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(object : SingleObserver<Int> {
                                override fun onSubscribe(d: Disposable) {

                                }

                                override fun onSuccess(t: Int) {
                                    val notification = FcmNotification(
                                        "New order sir ",
                                        "We got New Order From ${Common.currentuser!!.Name} ${Common.currentuser!!.Phone}"
                                    )

                                    val datasend = HashMap<String, String>()
                                    datasend.put(Common.NOTI_TITLE, "New order sir ")
                                    datasend.put(
                                        Common.NOTI_CONTENT,
                                        "We got New Order" + Common.currentuser!!.Phone
                                    )

                                    val sendData = FCMSendData(
                                        Common.getNewOrderTopic(),
                                        notification,
                                        datasend
                                    )
                                    compositeDisposable.add(
                                        ifcmService.sendNotification(sendData)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe({ t: FCMResponse2? ->
                                                //  if (t!!.success != 0)
                                                Log.i("Notification Test", t.toString())
                                                clearCart()
                                                Toast.makeText(
                                                    context!!,
                                                    "Order Placed Succesfully",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }, { t: Throwable? ->

                                                Toast.makeText(
                                                    context!!,
                                                    "Notification failed ",
                                                    Toast.LENGTH_SHORT
                                                ).show()


                                            })
                                    )


                                }

                                override fun onError(e: Throwable) {
                                    Toast.makeText(
                                        requireContext(),
                                        "" + e.message,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                            })
                    }
                }
        }
    }

    private fun sumCart() {
        cartDataSource!!.sumPrice(Common.currentuser!!.uid!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<Double> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onSuccess(t: Double) {

                    txt_total_price!!.text = StringBuilder("Total:")
                        .append(t)
                }

                override fun onError(e: Throwable) {
                    if (!e.message!!.contains(""))
                        Toast.makeText(context, "" + e.message!!, Toast.LENGTH_SHORT).show()
                }


            })
    }

    private fun clearCart() {
        cartDataSource!!.cleancart(Common.currentuser!!.uid!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<Int> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onSuccess(t: Int) {
                    //Toast.makeText(context, "Clear Cart Success", Toast.LENGTH_SHORT).show()
                    EventBus.getDefault().postSticky(CountCartEvent(true))
                }

                override fun onError(e: Throwable) {
                    Toast.makeText(context, "" + e.message, Toast.LENGTH_SHORT).show()
                }

            })
    }


    override fun onStart() {
        super.onStart()

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        cartViewModel!!.onStop()
        compositeDisposable.clear()
        EventBus.getDefault().postSticky(HideFABCart(false))
        EventBus.getDefault().removeAllStickyEvents()
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this)
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onUpdateItemInCart(event: UpdateItemInCart) {
        if (event.cartitem != null) {
            recyclerViewState = recycler_cart!!.layoutManager!!.onSaveInstanceState()
            cartDataSource!!.updateCart(event.cartitem)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<Int> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onSuccess(t: Int) {
                        calculateTotalPrice()
                        recycler_cart!!.layoutManager!!.onRestoreInstanceState(recyclerViewState)
                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(context, "[UPDATE CART]" + e.message, Toast.LENGTH_SHORT)
                            .show()
                    }


                })
        }


    }

    private fun calculateTotalPrice() {
        cartDataSource!!.sumPrice(Common.currentuser!!.uid!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<Double> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onSuccess(discount: Double) {
                    txt_total_price!!.text = StringBuilder("Total: ")
                        .append(Common.formatPrice(discount))
                }


                override fun onError(e: Throwable) {
                    //if(!e.message!!.contains(""))
                    //Toast.makeText(context,"[SUM CART]"+e.message,Toast.LENGTH_SHORT).show()
                }

            })
    }


    override fun onPrepareOptionsMenu(menu: Menu) {
        menu!!.findItem(R.id.action_settings).setVisible(false) // hide setting menu when in cart
        super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.cart_meny, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item!!.itemId == R.id.action_clear_cart) {
            /*   cartDataSource!!.cleancart(Common.currentuser!!.uid!!)
                       .subscribeOn(Schedulers.io())
                       .observeOn(AndroidSchedulers.mainThread())
                       .subscribe(object : SingleObserver<Int> {
                           override fun onSubscribe(d: Disposable) {

                           }

                           override fun onSuccess(t: Int) {
                               Toast.makeText(context, "Clear Cart Success", Toast.LENGTH_SHORT)
                               EventBus.getDefault().postSticky(CountCartEvent(true))


                           }

                           override fun onError(e: Throwable) {
                               Toast.makeText(context, "" + e.message, Toast.LENGTH_SHORT).show()
                           }


                       })*/
            clearCart()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onLoadTimeSuccess(order: order, estimatedTimeMs: Long) {
        order.createDate = (estimatedTimeMs)
        order.orderStatus = 0
        writeOrderToFirebase(order)
    }

    override fun ontLoadTimeFailed(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        EventBus.getDefault().postSticky(MenuItemBack())
        super.onDestroy()
    }

}