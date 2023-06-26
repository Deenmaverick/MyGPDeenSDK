package com.deenislamsdk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.deenislam.sdk.Deen
import com.deenislam.sdk.DeenCallback

class DeenActivity : AppCompatActivity(),DeenCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val msisdn:EditText = findViewById(R.id.phone_number)
        val authBtn:AppCompatButton = findViewById(R.id.login)

        authBtn.setOnClickListener {
            if(msisdn.text.isNotEmpty()){
                Deen.openDeen(this,msisdn.text.toString(),this@DeenActivity)
            }else{
                Toast.makeText(this,"Enter number", Toast.LENGTH_SHORT).show()
            }
        }



    }

    override fun onAuthSuccess() {
        Toast.makeText(this, "Auth Success Callback", Toast.LENGTH_SHORT).show()
    }

    override fun onAuthFailed() {
        Toast.makeText(this, "Auth Failed Callback", Toast.LENGTH_SHORT).show()
    }
}