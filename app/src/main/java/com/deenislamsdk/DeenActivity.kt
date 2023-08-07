package com.deenislamsdk

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.deenislam.sdk.Deen
import com.deenislam.sdk.DeenCallback

class DeenActivity : AppCompatActivity(),DeenCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val msisdn:EditText = findViewById(R.id.phone_number)
        val authBtn:AppCompatButton = findViewById(R.id.login)
        val tasbeehBtn:AppCompatButton = findViewById(R.id.tasbeeh)
        val forbiddenBtn:AppCompatButton = findViewById(R.id.prayertime)

        authBtn.setOnClickListener {
            if(msisdn.text.isNotEmpty()){
                Deen.openDeen(this,msisdn.text.toString(),this@DeenActivity)
            }else{
                Toast.makeText(this,"Enter number", Toast.LENGTH_SHORT).show()
            }
        }

        tasbeehBtn.setOnClickListener {
            if(msisdn.text.isNotEmpty()){
                Deen.openTasbeeh(this,msisdn.text.toString(),this@DeenActivity)
            }else{
                Toast.makeText(this,"Enter number", Toast.LENGTH_SHORT).show()
            }
        }

        forbiddenBtn.setOnClickListener {
            if(msisdn.text.isNotEmpty()){
                Deen.openPrayerTime(this,msisdn.text.toString(),this@DeenActivity)
            }else{
                Toast.makeText(this,"Enter number", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        Deen.destroySDK()
    }

    override fun onAuthSuccess() {
        Toast.makeText(this, "Auth Success Callback", Toast.LENGTH_SHORT).show()
    }

    override fun onAuthFailed() {
        Toast.makeText(this, "Auth Failed Callback", Toast.LENGTH_SHORT).show()
    }
}