package com.AashuDeveloper.vegiwell

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import com.AashuDeveloper.vegiwell.Common.Common
import com.AashuDeveloper.vegiwell.Model.UserModel
import com.AashuDeveloper.vegiwell.Remote.ICloudfunctions

import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.auth.GoogleAuthUtil.getToken
import com.google.android.gms.auth.zzd.getToken
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import dmax.dialog.SpotsDialog
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.alert_dialog.*
import java.util.*

class Loginactivity : AppCompatActivity() {


    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var listner: FirebaseAuth.AuthStateListener
    private lateinit var dialog: AlertDialog
    private val compositeDisposable = CompositeDisposable()
    private lateinit var cloudFunctions: ICloudfunctions


    private lateinit var userRef: DatabaseReference
    private var providers : List<AuthUI.IdpConfig>?=null


    companion object {

        private val App_REQUEST_CODE = 91
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(listner)
    }

    override fun onStop() {
        if (listner != null)
            firebaseAuth.removeAuthStateListener(listner)
        compositeDisposable.clear()
        super.onStop()

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loginactivity)

        init()
    }

    private fun init() {
        providers = Arrays.asList<AuthUI.IdpConfig>(AuthUI.IdpConfig.PhoneBuilder().build())
        userRef = FirebaseDatabase.getInstance().getReference(Common.USER_REFERENCE)
        firebaseAuth = FirebaseAuth.getInstance()
        dialog = SpotsDialog.Builder().setContext(this).setCancelable(false).build()

        listner = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {


                Checkuserfromfirebase(user!!)

            } else {

                Phonelogin()
            }
        }

    }

    //       phone login



    private fun Checkuserfromfirebase(user: FirebaseUser) {
        checkconection()
        //dialog!!.show()
        userRef!!.child(user!!.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {

                    override fun onCancelled(p0: DatabaseError) {
                        Toast.makeText(this@Loginactivity, "Welcome back" + p0.message, Toast.LENGTH_SHORT).show()

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.exists()) {



                            val userModel = p0.getValue(UserModel::class.java)
                            goToHomeactivity(userModel)

                        } else {


                            showRegisterDialog(user!!)
                        }

                        dialog!!.dismiss()
                    }
                })


    }

    private fun checkconection() {

        //dialog.show()
        val manager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkinfo = manager.activeNetworkInfo
        if (null!=networkinfo)

        {

            if (networkinfo.type == ConnectivityManager.TYPE_WIFI)
            {
                Toast.makeText(this,"Wifi connected",Toast.LENGTH_SHORT).show()
            }
            else if(networkinfo.type == ConnectivityManager.TYPE_MOBILE){
                Toast.makeText(this,"Internet Connected",Toast.LENGTH_SHORT).show()
            }
        }
        else
        {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.alert_dialog)
            dialog.setCanceledOnTouchOutside(false)
            dialog.window!!.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialog.btn_try.setOnClickListener {
                recreate()
            }



            dialog.show()

        }

    }

    private fun showRegisterDialog(user: FirebaseUser) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Register")
        builder.setMessage("Please fill the Information")
        val iteamview = LayoutInflater.from(this@Loginactivity).inflate(R.layout.layout_register, null)

        val edit_name = iteamview.findViewById<EditText>(R.id.edit_name)
        val edit_address = iteamview.findViewById<EditText>(R.id.edit_address)
        val edit_phone = iteamview.findViewById<EditText>(R.id.edit_phone)

        edit_phone.setText(user!!.phoneNumber)
        builder.setView(iteamview)
        builder.setNegativeButton("CANCEL") { dialogInterface: DialogInterface, i: Int -> }
        builder.setPositiveButton("REGISTER") { dialogInterface: DialogInterface, i: Int ->

            if (TextUtils.isDigitsOnly(edit_name.text.toString())) {
                Toast.makeText(this@Loginactivity, "Please Enter Your Name ", Toast.LENGTH_SHORT).show()
                return@setPositiveButton

            } else if (TextUtils.isDigitsOnly(edit_address.text.toString())) {
                Toast.makeText(this@Loginactivity, "Enter Your Address ", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            val userModel = UserModel()
            userModel.uid = user!!.uid
            userModel.Name = edit_name.text.toString()
            userModel.Address = edit_address.text.toString()
            userModel.Phone = edit_phone.text.toString()


            userRef!!.child(user!!.uid)
                    .setValue(userModel)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            dialogInterface.dismiss()
                            Toast.makeText(this@Loginactivity,"Congratulation ! Register success!",Toast.LENGTH_SHORT).show()
                            goToHomeactivity(userModel)
                        }
                    }

        }


         val dialog = builder.create()
         dialog.show()



    }


    private fun goToHomeactivity(userModel: UserModel?) {

      /*  FirebaseInstanceId.getInstance().instanceId*/
        FirebaseMessaging.getInstance().token


            .addOnFailureListener { e ->
                Toast.makeText(this@Loginactivity, "" + e.message, Toast.LENGTH_SHORT).show()

                Common.currentuser = userModel!!

                startActivity(Intent(this@Loginactivity, Homeactivity::class.java))
                finish()
            }
            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                {
                    Common.currentuser = userModel!!

                    Common.updateToken(this@Loginactivity,task.result)
                    startActivity(Intent(this@Loginactivity, Homeactivity::class.java))
                    finish()
                }
            }
    }


    private fun Phonelogin() {
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers!!).build(), App_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == App_REQUEST_CODE)
        {
            val response = IdpResponse.fromResultIntent(data)
            if(resultCode == Activity.RESULT_OK)

            {
                val user = FirebaseAuth.getInstance().currentUser
            }
            else
            {
                Toast.makeText(this,"Failed to sign in", Toast.LENGTH_SHORT).show()
            }
        }

    }
  }

