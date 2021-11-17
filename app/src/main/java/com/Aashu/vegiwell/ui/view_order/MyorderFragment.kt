package com.AashuDeveloper.vegiwell.ui.view_order

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.Unbinder
import com.AashuDeveloper.vegiwell.Adapter.MyOrderAdapter
import com.AashuDeveloper.vegiwell.Callback.ILoadOrderCallbackListener
import com.AashuDeveloper.vegiwell.Callback.IMyButtonCallback
import com.AashuDeveloper.vegiwell.Common.Common
import com.AashuDeveloper.vegiwell.Common.MySwipeHelper
import com.AashuDeveloper.vegiwell.Database.CartDataSource
import com.AashuDeveloper.vegiwell.EventBus.MenuItemBack
import com.AashuDeveloper.vegiwell.Model.order
import com.AashuDeveloper.vegiwell.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dmax.dialog.SpotsDialog
import org.greenrobot.eventbus.EventBus
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MyorderFragment : Fragment(), ILoadOrderCallbackListener {

    private var MyorderViewmodel: MyorderViewModel? = null
    private var unbinder: Unbinder? = null

    internal lateinit var dialog: AlertDialog
    private var cartDataSource: CartDataSource? = null
    internal lateinit var recycler_order: RecyclerView

    internal lateinit var listener: ILoadOrderCallbackListener
    var adapter: MyOrderAdapter? = null

    private val LOG_TAG = MyorderFragment::class.simpleName

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        MyorderViewmodel = ViewModelProvider(this).get(MyorderViewModel::class.java!!)
        val root = inflater.inflate(R.layout.fragment_view_order, container, false)
        initViews(root)
        loadOrderFromFirebase()

        MyorderViewmodel!!.mutableLiveDataOrderList.observe(viewLifecycleOwner, Observer {
            Log.i(LOG_TAG, it.toString())
            Collections.reverse(it!!)
            val adapter = MyOrderAdapter(requireContext(), it!!.toMutableList())
            recycler_order!!.adapter = adapter
        })

        return root
    }

    private fun loadOrderFromFirebase() {
        dialog!!.show()


        FirebaseDatabase.getInstance().getReference(Common.ORDER_REF)
                .orderByChild("userId")
                .equalTo(Common.currentuser!!.uid!!)
                .limitToLast(100)

                .addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(p0: DataSnapshot) {
                        val orderList = ArrayList<order>()

                        for (orderSnapShot in p0.children) {


                            val order = orderSnapShot.getValue(order::class.java)
                            Log.i(order?.cartItemList?.size.toString(), "cheked")


                            order!!.ordernumber = orderSnapShot.key

                            orderList.add(order!!)
                        }

                        listener.onLoadOrderSuccess(orderList)

                    }

                    override fun onCancelled(error: DatabaseError) {
                        listener!!.onLoadOrderFailed(error.message!!)
                    }

                })


        /* .get().addOnCompleteListener {
                  if (it.isSuccessful) {
                      val dataSnapShot = it.result
                      val orderList = ArrayList<order>()

                      dataSnapShot?.let {
                          for (orderSnapShot in dataSnapShot.children) {

                              val order = orderSnapShot.getValue(order::class.java)
                              Log.i(order?.cartItemList?.size.toString(), "cheked")

                              order!!.ordernumber = orderSnapShot.key

                              orderList.add(order)
                          }

                          listener.onLoadOrderSuccess(orderList)
                      }
                  } else listener!!.onLoadOrderFailed(it.exception?.message!!)
              }*/

    }

    private fun initViews(root: View?) {
        listener = this

        dialog = SpotsDialog.Builder().setContext(requireContext()).setCancelable(false).build()
        recycler_order = root!!.findViewById(R.id.recycler_order) as RecyclerView
        recycler_order!!.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(requireContext())
        recycler_order!!.layoutManager = layoutManager
        recycler_order!!.addItemDecoration(DividerItemDecoration(requireContext(), layoutManager.orientation))

        val swipe = object : MySwipeHelper(requireContext(), recycler_order!!, 250) {
            override fun instantiateMyButton(viewHolder: RecyclerView.ViewHolder, buffer: MutableList<MyButton>) {
                buffer.add(MyButton(context!!,
                        "Delete",
                        0,
                        R.drawable.ic_baseline_delete_24,

                        Color.parseColor("#DC1E2B"),
                        object : IMyButtonCallback {
                            override fun onClick(pos: Int) {
                                val order = (recycler_order.adapter as MyOrderAdapter).getItemPosition(pos)

                                val builder = androidx.appcompat.app.AlertDialog.Builder(context!!)

                                builder.setTitle("Delete Order")
                                        .setMessage("sure you want to Delete ?")
                                        .setNegativeButton("No") { dialogInterface, i ->
                                            dialogInterface.dismiss()
                                        }
                                        .setPositiveButton("Yes") { dialogInterface, i ->
                                            FirebaseDatabase.getInstance()
                                                    .getReference(Common.ORDER_REF)
                                                    .child(order.ordernumber!!)
                                                    .removeValue()
                                                    .addOnFailureListener { e ->
                                                        Toast.makeText(context!!, e.message, Toast.LENGTH_SHORT).show()
                                                    }


                                                    .addOnSuccessListener {



                                                                   adapter?.removeItem(pos)
                                                                    adapter?.notifyItemRemoved(pos)


                                                       /* (recycler_order.adapter as MyOrderAdapter).removeItem(pos)
                                                        (recycler_order.adapter as MyOrderAdapter).notifyItemRemoved(pos)*/
                                                        dialogInterface.dismiss()
                                                        Toast.makeText(requireContext(), "Order Deleted Succesfully", Toast.LENGTH_SHORT).show()

                                                    }

                                        }

                                val dialog = builder.create()
                                dialog.show()


                            }

                        }))

                buffer.add(MyButton(context!!,
                        "Cancel Order",
                        30,
                        0,
                        Color.parseColor("#FFA500"),
                        object : IMyButtonCallback {
                            override fun onClick(pos: Int) {

                                val order = (recycler_order.adapter as MyOrderAdapter).getItemPosition(pos)
                                if (order.orderStatus == 0) {
                                    val builder = androidx.appcompat.app.AlertDialog.Builder(context!!)
                                    builder.setTitle("Cancel Order")
                                            .setMessage(" Are You Sure ?")
                                            .setNegativeButton("No") { dialogInterface, i ->
                                                dialogInterface.dismiss()
                                            }
                                            .setPositiveButton("Yes") { dialogInterface, i ->
                                                val update_data = HashMap<String, Any>()
                                                update_data.put("orderStatus", -1)
                                                FirebaseDatabase.getInstance()
                                                        .getReference(Common.ORDER_REF)
                                                        .child(order.ordernumber!!)
                                                        .updateChildren(update_data)
                                                        .addOnFailureListener { e ->
                                                            Toast.makeText(context!!, e.message, Toast.LENGTH_SHORT).show()
                                                        }
                                                        .addOnSuccessListener {
                                                            order.orderStatus = -1
                                                            (recycler_order.adapter as MyOrderAdapter).setItemAtPosition(pos, order)
                                                            (recycler_order.adapter as MyOrderAdapter).notifyItemChanged(pos)


                                                            Toast.makeText(context!!, "Cancel Order Successfully", Toast.LENGTH_SHORT).show()


                                                        }


                                            }

                                    val dialog = builder.create()
                                    dialog.show()
                                } else {
                                    Toast.makeText(context!!, StringBuilder("Your order status was changed to:")
                                            .append(Common.convertStatusToText(order.orderStatus))
                                            .append(",so you can't cancel it"), Toast.LENGTH_SHORT).show()


                                }


                            }

                        }))


            }


        }

    }


    override fun onLoadOrderSuccess(orderList: List<order>) {
        dialog.dismiss()

        MyorderViewmodel!!.setMutableLIveDataOrderList(orderList)
    }

    override fun onLoadOrderFailed(message: String) {
        dialog!!.dismiss()
        Toast.makeText(requireContext()!!, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        EventBus.getDefault().postSticky(MenuItemBack())
        super.onDestroy()
    }
}