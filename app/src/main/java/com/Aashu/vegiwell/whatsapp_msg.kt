package com.AashuDeveloper.vegiwell

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.widget.*
import androidx.core.app.ActivityCompat.startActivityForResult
import kotlinx.android.synthetic.main.activity_whatsapp_msg.*
import java.util.*

class whatsapp_msg : AppCompatActivity() {
    private val REQUEST_CODE_SPEECH_INPUT = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_whatsapp_msg)



        // Referencing the Edit Text


        val messageEditText = findViewById<EditText>(R.id.message_text)as TextView



        // Referencing the button
        val submit = findViewById<Button>(R.id.submit)

        // Setting on click listener
        submit.setOnClickListener {

            var messages1 = messageEditText.text.toString()
            // Calling the function
            sendMessage(messages1)
        }

        var voicebutton = findViewById<ImageButton>(R.id.voicebtn)

        voicebutton.setOnClickListener {
            speak()
        }
    }

    private fun speak() {


        val mIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        mIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        mIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.getDefault())
        mIntent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Please Give Oder")

        try {
            startActivityForResult(mIntent,REQUEST_CODE_SPEECH_INPUT)
        }
        catch (e:Exception){

            Toast.makeText(this,e.message,Toast.LENGTH_SHORT).show()

        }
    }





    fun sendMessage(messages1: String) {
        val phoneNumber = +918169811157
        val url = "https://api.whatsapp.com/send?phone=$phoneNumber&text=$messages1"


       // packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)

        val i = Intent(Intent.ACTION_VIEW)
        i.type = "text/plain"
        i.putExtra(Intent.EXTRA_TEXT, messages1)
        i.data = Uri.parse(url)


        // Checking whether whatsapp is installed or not
        if (i.resolveActivity(packageManager) == null) {
            packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
            Toast.makeText(this,
                    "Please install whatsapp first.",
                    Toast.LENGTH_SHORT).show()
            return
        }

        // Starting Whatsapp
        startActivity(i)
        message_text.setText("")

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(data != null)
        if(requestCode ==100 || data != null) {

             val res : ArrayList<String> = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) as ArrayList<String>
            message_text.text = res[0].toEditable()
        }
    }

    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

}
