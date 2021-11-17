package com.AashuDeveloper.vegiwell.ui.FoodDetail

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.andremion.counterfab.CounterFab
import com.bumptech.glide.Glide
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton
import com.AashuDeveloper.vegiwell.Common.Common
import com.AashuDeveloper.vegiwell.Database.CartDataSource
import com.AashuDeveloper.vegiwell.Database.CartDatabase
import com.AashuDeveloper.vegiwell.Database.Cartitem
import com.AashuDeveloper.vegiwell.Database.LocalCartDataSource
import com.AashuDeveloper.vegiwell.EventBus.CountCartEvent
import com.AashuDeveloper.vegiwell.EventBus.MenuItemBack
import com.AashuDeveloper.vegiwell.Model.CommentModel
import com.AashuDeveloper.vegiwell.Model.FoodModel
import com.AashuDeveloper.vegiwell.Model.order
import com.AashuDeveloper.vegiwell.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import com.google.gson.Gson

import dmax.dialog.SpotsDialog
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

import org.greenrobot.eventbus.EventBus
import kotlin.math.roundToInt

class FoodDetailFragment : Fragment(), TextWatcher {




    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(charSequence:  CharSequence?, start: Int, before: Int, count: Int) {
        chip_group_addon!!.clearCheck()
        chip_group_addon!!.removeAllViews()

        for (addonModel in Common.foodSelected!!.addon!!)
        {
            if(addonModel.name!!.toLowerCase().contains(charSequence.toString().toLowerCase()))
            {
                val chip = layoutInflater.inflate(R.layout.layout_chip,null,false)as Chip
                chip.text=StringBuilder(addonModel!!.name!!).append("(+Rs").append(addonModel.price).append(")").toString()
                chip.setOnCheckedChangeListener{compoundButton, b->
                    if (b){
                        if (Common.foodSelected!!.userSelectedAddon==null)
                            Common.foodSelected!!.userSelectedAddon = ArrayList()
                        Common.foodSelected!!.userSelectedAddon!!.add(addonModel)
                    }
                }
                chip_group_addon!!.addView(chip)
            }
        }

    }

    override fun afterTextChanged(s: Editable?) {

    }

    private var compositeDisposable: CompositeDisposable = CompositeDisposable()
    private lateinit var cartDataSource : CartDataSource

    private lateinit var foodDetailViewModel: FoodDetailViewModel
    private lateinit var addonBottomSheetDialog:BottomSheetDialog



    private var img_food:ImageView?=null
    private var btnCart: CounterFab?=null
    private var btnRating: FloatingActionButton?=null
    private var food_name : TextView?=null
    private var food_description:TextView?=null
    private var food_text:TextView?=null
    private var food_price:TextView?=null
    private var discount_price:TextView?=null
    private var number_btn: ElegantNumberButton?=null
    private var rating_bar : RatingBar?=null
    private  var clickme:ImageView?=null
    private var btnShow: Button?=null
    private var radi_group_size : RadioGroup?=null
    private var img_add_on:ImageView?=null
    private var chip_group_user_selected_from_addon:ChipGroup?=null
    private var chip_group_addon:ChipGroup?=null
    private var edt_search_addon:EditText?=null

    private var waitingDialog:android.app.AlertDialog?=null



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        foodDetailViewModel =
            ViewModelProvider(this).get(FoodDetailViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_food_detail, container, false)
        
        initViews(root)

        foodDetailViewModel.getMutableLiveDataFood().observe(viewLifecycleOwner, Observer {
           displayInfo(it)



            foodDetailViewModel.getMutableLiveDatacomment().observe(viewLifecycleOwner,{
            /*    sumbitRatingToFirebase(it)*/

            })
        })
        return root
    }

   
   /* private fun sumbitRatingToFirebase(commentModel:  CommentModel?) {

        waitingDialog!!.show()

        FirebaseDatabase.getInstance()
                .getReference(Common.COMMENT_REF)
                .child(Common.foodSelected!!.id!!)
                .push()
                .setValue(commentModel)
                .addOnCompleteListener {  task->
                    if (task.isSuccessful)
                    {

                    }
                      waitingDialog!!.dismiss()
                }


    }

    private fun addRatingToFood(ratingValue: Double) {

        FirebaseDatabase.getInstance()
            .getReference(Common.CATEGORY_REF)
            .child(Common.categoryselected!!.menu_id!!)
            .child("foods")//select food array
            .child(Common.foodSelected!!.key!!)
            .addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    waitingDialog!!.dismiss()
                    Toast.makeText(context!!," "+p0.message,Toast.LENGTH_SHORT).show()
                }
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    if (dataSnapshot.exists())
                    {
                        val foodModel = dataSnapshot.getValue(FoodModel::class.java)
                        foodModel!!.key = Common.foodSelected!!.key

                        // to apply rating

                        val sumRating  = foodModel.ratingvalue!!.toDouble() +  (ratingValue)
                        val ratingCount = foodModel.ratingCount +1



                        val updateData = HashMap<String,Any > ()
                        updateData["ratingValue"] = sumRating
                        updateData["ratingCount"] = ratingCount


                        foodModel.ratingCount = ratingCount
                        foodModel.ratingvalue = sumRating


                        dataSnapshot.ref
                            .updateChildren(updateData)
                            .addOnCompleteListener { task->
                                waitingDialog!!.dismiss()
                                if (task.isSuccessful)
                                {
                                    Common.foodSelected = foodModel
                                    foodDetailViewModel!!.setFoodModel(foodModel)
                                    Toast.makeText(context!!,"Thank you ",Toast.LENGTH_SHORT).show()
                                }

                            }
                    }
                    else
                        waitingDialog!!.dismiss()




                }



            })

    }*/

    private fun  displayInfo(it: FoodModel?) {

        Glide.with(requireContext()).load(it!!.image).into(img_food!!)
        food_name!!.text = StringBuilder(it!!.name!!)
        food_description!!.text = StringBuilder(it!!.description!!)
        food_price!!.text = StringBuilder(it!!.price!!.toString())
        discount_price!!.text =StringBuilder(it!!.discount!!.toString())


        //rating_bar!!.rating = it!!.ratingvalue.toFloat() / it!!.ratingCount

        for (sizeModel in it!!.size)
        {
            val radioButton = RadioButton(context)
            radioButton.setOnCheckedChangeListener{ compoundButton , b->
                if (b)
                    Common.foodSelected!!.userSelectedSize = sizeModel
                  CalculateTotalPrice()

            }
            val params = LinearLayout.LayoutParams( 0 ,
                    LinearLayout.LayoutParams.WRAP_CONTENT,1.0f)
            radioButton.layoutParams = params
            radioButton.text = sizeModel.name
            radioButton.tag = sizeModel.price

            radi_group_size!!.addView(radioButton)
        }

        if(radi_group_size!!.childCount > 0)
        {
            val radioButton = radi_group_size!!.getChildAt( 0) as RadioButton
            radioButton.isChecked=  true
        }

    }

    private fun CalculateTotalPrice() {

        var totalPrice = Common.foodSelected!!.price.toDouble()
        var displayPrice = 0.0
        var checkedvalue = 0.0
        var temp = 0.0


        if (Common.foodSelected!!.userSelectedAddon != null && Common.foodSelected!!.userSelectedAddon!!.size > 0)
        {
            for (addonModel in  Common.foodSelected!!.userSelectedAddon!!)
                totalPrice+= addonModel.price!!.toDouble()
        }

         if (Common.foodSelected!!.userSelectedSize !=null)
               totalPrice += Common.foodSelected!!.userSelectedSize!!.price!!.toDouble()


        displayPrice = totalPrice * number_btn!!.number.toInt()

        displayPrice = (displayPrice * 100.0).roundToInt() /100.0

        checkedvalue = Common.foodSelected!!.discount.toDouble()
        temp = displayPrice - checkedvalue


        food_price!!.text = StringBuilder("").append(Common.formatPrice(temp)).toString()


            val cross = StringBuilder("₹ ").append(Common.formatPrice(displayPrice)).toString()
          val spannableString1 = SpannableString(cross)
          spannableString1.setSpan(StrikethroughSpan(),0,cross.length,0)
           discount_price!!.text = spannableString1

    }


    private fun initViews(root: View?) {

        (activity as AppCompatActivity) . supportActionBar!!.setTitle(Common.foodSelected!!.name)
        cartDataSource = LocalCartDataSource(CartDatabase.getInstance(requireContext()).cartDao())

        addonBottomSheetDialog = BottomSheetDialog(requireContext(),R.style.DialogStyle)
        val layout_user_selected_addon = layoutInflater.inflate(R.layout.layout_addon_display,null)
        chip_group_addon = layout_user_selected_addon.findViewById(R.id.chip_group_addon) as ChipGroup
        edt_search_addon= layout_user_selected_addon.findViewById(R.id.edt_search) as EditText

        addonBottomSheetDialog.setContentView(layout_user_selected_addon)

        addonBottomSheetDialog.setOnDismissListener { dialogInterface ->
            displayUserSelectedAddon()
            CalculateTotalPrice()
        }




        waitingDialog = SpotsDialog.Builder().setContext(requireContext()).setCancelable(false).build()

        btnCart = root!!.findViewById(R.id.btnCart) as CounterFab
        img_food = root!!.findViewById(R.id.img_food) as ImageView
        //clickme = root!!.findViewById(R.id.clickme)as ImageView
        //btnRating= root!!.findViewById(R.id.btn_rating) as FloatingActionButton
        food_name= root!!.findViewById(R.id.food_name) as TextView
        food_description= root!!.findViewById(R.id.food_description) as TextView
        food_price= root!!.findViewById(R.id.food_price) as TextView
        discount_price = root!!.findViewById(R.id.discount_price) as TextView
        number_btn= root!!.findViewById(R.id.number_btn) as ElegantNumberButton
       // rating_bar= root!!.findViewById(R.id.rating_bar) as RatingBar
        btnShow = root!!.findViewById(R.id.btn_show_Comment)as Button
        radi_group_size = root!!.findViewById(R.id.radi_group_size)as RadioGroup
        img_add_on = root!!.findViewById(R.id.img_add_button)as ImageView
        chip_group_user_selected_from_addon = root!!.findViewById(R.id.chip_group_user_selected_from_addon)as ChipGroup









        img_add_on!!.setOnClickListener {
            if (Common.foodSelected!!.addon !=null)
            {
                displayAllAddon()
                addonBottomSheetDialog.show()
            }
        }


        btnShow!!.setOnClickListener{
            val cartitem = Cartitem()


            cartitem.uid = Common.currentuser!!.uid!!
            cartitem.userPhone = Common.currentuser!!.Phone!!

            cartitem.foodId = Common.foodSelected!!.id!!
            cartitem.foodName = Common.foodSelected!!.name!!
            cartitem.foodImage=  Common.foodSelected!!.image!!
           /* cartitem.foodPrice= Common.foodSelected!!.price.toDouble()*/




            /// Test case passing data inn room

            var totalPrice = Common.foodSelected!!.price.toDouble()
            var displayPrice = 0.0
            var checkedvalue = 0.0
            var temp = 0.0

            if (Common.foodSelected!!.userSelectedAddon != null && Common.foodSelected!!.userSelectedAddon!!.size > 0)
            {
                for (addonModel in  Common.foodSelected!!.userSelectedAddon!!)
                    totalPrice+= addonModel.price!!.toDouble()
            }

            if (Common.foodSelected!!.userSelectedSize !=null)
                totalPrice += Common.foodSelected!!.userSelectedSize!!.price!!.toDouble()


            /*displayPrice = totalPrice * number_btn!!.number.toInt()*/
              displayPrice = totalPrice
            displayPrice = (displayPrice * 100.0).roundToInt() /100.0

            checkedvalue = Common.foodSelected!!.discount.toDouble()
            temp = displayPrice - checkedvalue
            cartitem.foodPrice= temp



            cartitem.foodQuantity=number_btn!!.number.toInt()
         /*  // cartitem.foodExtraPrice=Common.calculateExtraPrice(Common.foodSelected!!.userSelectedSize,Common.foodSelected!!.userSelectedAddon)*/
            if(Common.foodSelected!!.userSelectedAddon !=null)
                cartitem.foodAddon = Gson().toJson(Common.foodSelected!!.userSelectedAddon)
            else
                cartitem.foodAddon = "Default"
            if(Common.foodSelected!!.userSelectedSize !=null)
                cartitem.foodSize = Gson().toJson(Common.foodSelected!!.userSelectedSize)
            else
                cartitem.foodSize ="Default"
            cartDataSource.getItemWithAllOptionInCart(Common.currentuser!!.uid!!,
                cartitem.foodId,
                cartitem.foodSize,
                cartitem.foodAddon)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<Cartitem> {
                    override fun onSubscribe(d: Disposable) {


                    }

                    override fun onSuccess(cartItemFromDB: Cartitem) {
                        if (cartItemFromDB.equals(cartitem))
                        {
                            cartItemFromDB.foodExtraPrice = cartitem.foodExtraPrice
                            cartItemFromDB.foodAddon = cartitem.foodAddon
                            cartItemFromDB.foodSize = cartitem.foodSize
                            cartItemFromDB.foodQuantity = cartItemFromDB.foodQuantity + cartitem.foodQuantity
                            cartDataSource.updateCart(cartItemFromDB)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(object : SingleObserver<Int> {
                                    override fun onSubscribe(d: Disposable) {

                                    }

                                    override fun onSuccess(t: Int) {
                                        Toast.makeText(context,"Added To cart ",Toast.LENGTH_SHORT).show()
                                        EventBus.getDefault().postSticky(CountCartEvent(true))
                                         
                                    }

                                    override fun onError(e: Throwable) {
                                        Toast.makeText(context,"[Update Cart]"+e.message,Toast.LENGTH_SHORT).show()

                                    }


                                })

                        }
                        else
                        {
                            compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartitem)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    Toast.makeText(context," to cart Success",Toast.LENGTH_SHORT).show()
                                    EventBus.getDefault().postSticky(CountCartEvent(true))
                                },{
                                        t: Throwable? -> Toast.makeText(context,"[INSERT CART]" +t!!.message,Toast.LENGTH_SHORT).show()


                                }))

                        }

                    }

                    override fun onError(e: Throwable) {
                        if(e.message!!.contains("empty"))

                        {
                            compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartitem)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    Toast.makeText(context,"Add to cart Success",Toast.LENGTH_SHORT).show()
                                    EventBus.getDefault().postSticky(CountCartEvent(true))
                                },{
                                        t: Throwable? -> Toast.makeText(context,"[INSERT CART]" +t!!.message,Toast.LENGTH_SHORT).show()


                                }))
                        }
                        else
                            Toast.makeText(context,"[CART ERROR]" +e.message,Toast.LENGTH_SHORT).show()

                    }


                })




        }






        btnCart!!.setOnClickListener{
            val cartitem = Cartitem()
            cartitem.uid = Common.currentuser!!.uid!!
            cartitem.userPhone = Common.currentuser!!.Phone!!

            cartitem.foodId = Common.foodSelected!!.id!!
            cartitem.foodName = Common.foodSelected!!.name!!
            cartitem.foodImage=  Common.foodSelected!!.image!!
            //cartitem.foodPrice= Common.foodSelected!!.price!!.toDouble()
            // test case
            var totalPrice1 = Common.foodSelected!!.price.toDouble()
            var displayPrice1= 0.0
            var checkedvalue1 = 0.0
            var temp1 = 0.0

            if (Common.foodSelected!!.userSelectedAddon != null && Common.foodSelected!!.userSelectedAddon!!.size > 0)
            {
                for (addonModel in  Common.foodSelected!!.userSelectedAddon!!)
                    totalPrice1 += addonModel.price!!.toDouble()
            }

            if (Common.foodSelected!!.userSelectedSize !=null)
                totalPrice1 += Common.foodSelected!!.userSelectedSize!!.price!!.toDouble()


            displayPrice1 = totalPrice1 * number_btn!!.number.toInt()

            displayPrice1 = (displayPrice1 * 100.0).roundToInt() /100.0

            checkedvalue1 = Common.foodSelected!!.discount.toDouble()
            temp1 = displayPrice1 - checkedvalue1

            //food_price!!.text = StringBuilder("").append(Common.formatPrice(temp1)).toString()

            cartitem.foodPrice = temp1

            cartitem.foodQuantity=number_btn!!.number.toInt()
            cartitem.foodExtraPrice=Common.calculateExtraPrice(Common.foodSelected!!.userSelectedSize,Common.foodSelected!!.userSelectedAddon)
            if(Common.foodSelected!!.userSelectedAddon !=null)
                cartitem.foodAddon = Gson().toJson(Common.foodSelected!!.userSelectedAddon)
            else
                cartitem.foodAddon = " Default"
            if(Common.foodSelected!!.userSelectedSize !=null)
                cartitem.foodSize = Gson().toJson(Common.foodSelected!!.userSelectedSize)
               else
                cartitem.foodSize ="Default"
            cartDataSource.getItemWithAllOptionInCart(Common.currentuser!!.uid!!,
                    cartitem.foodId,
                    cartitem.foodSize,
                    cartitem.foodAddon)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : SingleObserver<Cartitem> {
                        override fun onSubscribe(d: Disposable) {


                        }

                        override fun onSuccess(cartItemFromDB: Cartitem) {
                            if (cartItemFromDB.equals(cartitem))
                            {
                                cartItemFromDB.foodExtraPrice = cartitem.foodExtraPrice
                                cartItemFromDB.foodAddon = cartitem.foodAddon
                                cartItemFromDB.foodSize = cartitem.foodSize
                                cartItemFromDB.foodQuantity = cartItemFromDB.foodQuantity + cartitem.foodQuantity
                                cartDataSource.updateCart(cartItemFromDB)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(object : SingleObserver<Int> {
                                            override fun onSubscribe(d: Disposable) {

                                            }

                                            override fun onSuccess(t: Int) {
                                                Toast.makeText(context,"Adding to cart ",Toast.LENGTH_SHORT).show()
                                                EventBus.getDefault().postSticky(CountCartEvent(true))

                                            }

                                            override fun onError(e: Throwable) {
                                                Toast.makeText(context,"[Update Cart]"+e.message,Toast.LENGTH_SHORT).show()

                                            }


                                        })

                            }
                            else
                            {
                                compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartitem)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe({
                                            Toast.makeText(context,"Add to cart Success",Toast.LENGTH_SHORT).show()
                                            EventBus.getDefault().postSticky(CountCartEvent(true))
                                        },{
                                            t: Throwable? -> Toast.makeText(context,"[INSERT CART]" +t!!.message,Toast.LENGTH_SHORT).show()


                                        }))

                            }

                        }

                        override fun onError(e: Throwable) {
                            if(e.message!!.contains("empty"))

                            {
                                compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartitem)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe({
                                            Toast.makeText(context,"Add to cart Success",Toast.LENGTH_SHORT).show()
                                            EventBus.getDefault().postSticky(CountCartEvent(true))
                                        },{
                                            t: Throwable? -> Toast.makeText(context,"[INSERT CART]" +t!!.message,Toast.LENGTH_SHORT).show()


                                        }))
                            }
                            else
                                Toast.makeText(context,"[CART ERROR]" +e.message,Toast.LENGTH_SHORT).show()

                        }


                    })




        }

    }



    private fun displayAllAddon() {
        if (Common.foodSelected!!.addon!!.size >0 )
        {
            chip_group_addon!!.clearCheck()
            chip_group_addon!!.removeAllViews()

            for (addonModel in Common.foodSelected!!.addon!!)
            {


                    val chip = layoutInflater.inflate(R.layout.layout_chip,null,false)as Chip
                    chip.text=StringBuilder(addonModel!!.name!!).append("(+Rs").append(addonModel.price).append(")").toString()
                    chip.setOnCheckedChangeListener{compoundButton, b->
                        if (b){
                            if (Common.foodSelected!!.userSelectedAddon==null)
                                Common.foodSelected!!.userSelectedAddon = ArrayList()
                            Common.foodSelected!!.userSelectedAddon!!.add(addonModel)
                        }
                    }
                    chip_group_addon!!.addView(chip)

            }


            edt_search_addon!!.addTextChangedListener(this)
        }
    }


    private fun displayUserSelectedAddon() {
        if (Common.foodSelected!!.userSelectedAddon != null && Common.foodSelected!!.userSelectedAddon!!.size > 0)
        {
            chip_group_user_selected_from_addon!!.removeAllViews()
            for (addonModel in  Common.foodSelected!!.userSelectedAddon!!)
            {
                val chip = layoutInflater.inflate(R.layout.layout_chip_with_delete,null,false) as Chip
                chip.text = StringBuilder(addonModel!!.name!!).append("(+Rs").append(addonModel.price).append(")").toString()
                chip.isClickable = false
                chip.setOnCloseIconClickListener { view ->
                    chip_group_user_selected_from_addon!!.removeView(view)
                    Common.foodSelected!!.userSelectedAddon!!.remove(addonModel)
                    CalculateTotalPrice()
                }
                chip_group_user_selected_from_addon!!.addView(chip)

             }
        }else
            chip_group_user_selected_from_addon!!.removeAllViews()


    }



    private fun Nameoncake() {

        var builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Name on Cake")
        builder.setMessage("Please fill Information")


        val itemView = LayoutInflater.from(context).inflate(R.layout.layout_rating_comment,null)


        //val ratingBar = itemView.findViewById<RatingBar>(R.id.rating_bar)
        val edit_Comment = itemView.findViewById<EditText>(R.id.edit_comment)


        builder.setView(itemView)

        builder.setNegativeButton("CANCEL") { dialogInterface: DialogInterface, _: Int -> }
        builder.setPositiveButton("Ok") { dialogInterface: DialogInterface, _: Int ->
         val commentModel = CommentModel()
         commentModel.name = Common.currentuser!!.Name
          //commentModel.comment = edit_Comment.text.toString()
          commentModel.uid = Common.currentuser!!.Phone

            val o = order()
            o.naam = edit_Comment.text.toString()
            //commentModel.ratingValue = ratingBar.rating

          /*  val serverTimestamp = HashMap<String,Any>()
            serverTimestamp["TimeStamp"] = ServerValue.TIMESTAMP
            commentModel.commentTimeStamp=(serverTimestamp)
*/

            foodDetailViewModel!!.setCommentModel(commentModel)
        }

        val dialog = builder.create()
        dialog.show()

    }

    override fun onDestroy() {
        EventBus.getDefault().postSticky(MenuItemBack())
        super.onDestroy()
    }


}
