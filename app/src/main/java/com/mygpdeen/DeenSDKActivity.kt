package com.mygpdeen

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.deenislamic.sdk.DeenSDKCallback
import com.deenislamic.sdk.DeenSDKCore
import com.deenislamic.sdk.views.gphome.GPHome
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DeenSDKActivity : AppCompatActivity(),DeenSDKCallback {

    private lateinit var  gphome: GPHome
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val msisdn: EditText = findViewById(R.id.phone_number)
        val authBtn: AppCompatButton = findViewById(R.id.login)
        val initSDKbtn: AppCompatButton = findViewById(R.id.initSDKbtn)
        val tasbeehBtn: AppCompatButton = findViewById(R.id.tasbeeh)
        val forbiddenBtn: AppCompatButton = findViewById(R.id.prayertime)
        val prayernotifyon: AppCompatButton = findViewById(R.id.prayernotifyon)
        val prayernotifyoff: AppCompatButton = findViewById(R.id.prayernotifyoff)
        val checkNotifyBtn: AppCompatButton = findViewById(R.id.checkNotifyBtn)
        val languageBtn: AppCompatButton = findViewById(R.id.languageBtn)
        val ijtemaBtn: AppCompatButton = findViewById(R.id.ijtemaBtn)
        val sehriAlarmBtn: AppCompatButton = findViewById(R.id.sehriAlarmBtn)
        val iftarAlarmBtn: AppCompatButton = findViewById(R.id.iftarAlarmBtn)
        val qurbaniBtn: AppCompatButton = findViewById(R.id.qurbaniBtn)
        val hajjumrahBtn: AppCompatButton = findViewById(R.id.hajjumrahBtn)

        val enBtn: AppCompatButton = findViewById(R.id.enBtn)
        val bnBtn: AppCompatButton = findViewById(R.id.bnBtn)

        val loadGPHomeBtn: AppCompatButton = findViewById(R.id.loadGPHomeBtn)
        gphome = findViewById(R.id.gphome)

        gphome.initWithMsisdn(
            context = this,
            token = "8801783355888",
            language = "en",
            callback = this
        )

        //DeenSDKCore.authSDK(this,"8801942247803",this)

          enBtn.setOnClickListener {
            if(DeenSDKCore.GetDeenToken().isNotEmpty() && this@DeenSDKActivity::gphome.isInitialized)
                gphome.changeLanguage("en")
        }

        bnBtn.setOnClickListener {
            if(DeenSDKCore.GetDeenToken().isNotEmpty() && this@DeenSDKActivity::gphome.isInitialized)
                gphome.changeLanguage("bn")
        }

        sehriAlarmBtn.setOnClickListener {
            if(msisdn.text.isNotEmpty()){
                DeenSDKCore.setSehriAlaram("5:26 AM")
            }else{
                Toast.makeText(this,"Enter number", Toast.LENGTH_SHORT).show()
            }
        }

        iftarAlarmBtn.setOnClickListener {
            if(msisdn.text.isNotEmpty()){
                DeenSDKCore.setIftarAlarm("5:33 PM")
            }else{
                Toast.makeText(this,"Enter number", Toast.LENGTH_SHORT).show()
            }
        }

        qurbaniBtn.setOnClickListener {
            if(msisdn.text.isNotEmpty()){
                DeenSDKCore.openFromRC("qurbani")
            }else{
                Toast.makeText(this,"Enter number", Toast.LENGTH_SHORT).show()
            }
        }

        hajjumrahBtn.setOnClickListener {
            if(msisdn.text.isNotEmpty()){
                DeenSDKCore.openFromRC("hajjandumrah")
            }else{
                Toast.makeText(this,"Enter number", Toast.LENGTH_SHORT).show()
            }
        }

        ijtemaBtn.setOnClickListener {
            if(msisdn.text.isNotEmpty()){
                DeenSDKCore.openFromRC("live_ijtema")
            }else{
                Toast.makeText(this,"Enter number", Toast.LENGTH_SHORT).show()
            }
        }

        languageBtn.setOnClickListener {
            //setLocale(this,"bn")
            if(msisdn.text.isNotEmpty()){
                DeenSDKCore.clearAllPrayerNotification(
                    context = this,
                    callback = this@DeenSDKActivity
                )
            }else{
                Toast.makeText(this,"Enter number", Toast.LENGTH_SHORT).show()
            }
        }

        initSDKbtn.setOnClickListener {
            if(msisdn.text.isNotEmpty()){
                DeenSDKCore.authSDK(
                    this,
                    msisdn.text.toString(),
                    this@DeenSDKActivity
                )
            }else{
                Toast.makeText(this,"Enter number", Toast.LENGTH_SHORT).show()
            }
        }

        loadGPHomeBtn.setOnClickListener {
            if(msisdn.text.isNotEmpty()){
                //gphome.loadapi()
            }else{
                Toast.makeText(this,"Enter number", Toast.LENGTH_SHORT).show()
            }
        }


        authBtn.setOnClickListener {
            if(msisdn.text.isNotEmpty()){
                DeenSDKCore.openDeen()
            }else{
                Toast.makeText(this,"Enter number", Toast.LENGTH_SHORT).show()
            }
        }

        tasbeehBtn.setOnClickListener {
            if(msisdn.text.isNotEmpty()){
                DeenSDKCore.openFromRC("tasbeeh")
            }else{
                Toast.makeText(this,"Enter number", Toast.LENGTH_SHORT).show()
            }
        }

        forbiddenBtn.setOnClickListener {
            if(msisdn.text.isNotEmpty()){
                DeenSDKCore.openFromRC("prayer_time")
            }else{
                Toast.makeText(this,"Enter number", Toast.LENGTH_SHORT).show()
            }
        }

        prayernotifyon.setOnClickListener {
            if(msisdn.text.isNotEmpty()){
                DeenSDKCore.prayerNotification(
                    isEnabled = true,
                    context = this,
                    callback = this@DeenSDKActivity
                )
            }else{
                Toast.makeText(this,"Enter number", Toast.LENGTH_SHORT).show()
            }
        }

        prayernotifyoff.setOnClickListener {
            if(msisdn.text.isNotEmpty()){
                DeenSDKCore.prayerNotification(
                    isEnabled = false,
                    context = this,
                    callback = this@DeenSDKActivity
                )
            }else{
                Toast.makeText(this,"Enter number", Toast.LENGTH_SHORT).show()
            }
        }

        checkNotifyBtn.setOnClickListener {
            val baseContext = this
            if(msisdn.text.isNotEmpty()){
                CoroutineScope(Dispatchers.Main).launch {
                    if(DeenSDKCore.isPrayerNotificationEnabled(baseContext))
                        Toast.makeText(applicationContext,"Notification is enabled", Toast.LENGTH_SHORT).show()
                    else
                    Toast.makeText(applicationContext,"Notification is disabled", Toast.LENGTH_SHORT).show()

                }
            }else{
                Toast.makeText(this,"Enter number", Toast.LENGTH_SHORT).show()
            }
        }

        //testcrash()

    }




    override fun onDestroy() {
        super.onDestroy()
        DeenSDKCore.destroySDK()
    }

    override fun onResume() {
        super.onResume()
        Log.e("APP_ACTIVITY","BL")
    }

    override fun onDeenSDKInitSuccess() {

        Toast.makeText(this, "Auth Success Callback", Toast.LENGTH_SHORT).show()
    }

    override fun onDeenSDKInitFailed() {
        Toast.makeText(this, "Auth Failed Callback", Toast.LENGTH_SHORT).show()
    }

    override fun onDeenSDKOperationSuccess() {
        Toast.makeText(this, "Deen operaton Success Callback", Toast.LENGTH_SHORT).show()
    }

    override fun onDeenSDKOperationFailed() {
        Toast.makeText(this, "Deen operaton Failed Callback", Toast.LENGTH_SHORT).show()
    }

    override fun onDeenSDKRCFailed() {
        Toast.makeText(this, "RC code failed", Toast.LENGTH_SHORT).show()
    }

    override fun DeenPrayerNotificationOn() {
        Toast.makeText(this, "Prayer notification enable Callback", Toast.LENGTH_SHORT).show()
    }

    override fun DeenPrayerNotificationOff() {
        Toast.makeText(this, "Prayer notification disable Callback", Toast.LENGTH_SHORT).show()
    }

    override fun DeenPrayerNotificationFailed() {
        Toast.makeText(this, "Prayer notification failed Callback", Toast.LENGTH_SHORT).show()
    }

    override fun deenLanguageChangeListner(language: String) {
        if(this::gphome.isInitialized)
            gphome.changeLanguage(language)
    }
}

/*
interface MyInterface {
    @JvmDefault
    fun myDefaultMethod() {
        println("Default method implementation")
    }
}*/
