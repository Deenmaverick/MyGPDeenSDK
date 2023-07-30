package com.deenislam.sdk.views.compass

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.deenislam.sdk.R
import com.deenislam.sdk.utils.MAKKAH_LATITUDE
import com.deenislam.sdk.utils.MAKKAH_LONGITUDE
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

const val PERMISSION_CODE = 100
internal class CompassFragment : BaseRegularFragment(),SensorEventListener {

    private lateinit var locationTxt:AppCompatTextView
    private lateinit var compassDial:ConstraintLayout
    private lateinit var compassKaaba:AppCompatImageView
    private lateinit var degreeTxt:AppCompatTextView
    private lateinit var distanceTxt:AppCompatTextView
    private var locationListener:LocationListener ? =null
    private var locationManager:LocationManager ? =null
    private lateinit var mSensorManager: SensorManager

    private lateinit var accuracy:AppCompatTextView


    private var dialog:Dialog ? = null
    var bearing: Double? = null


    override fun OnCreate() {
        super.OnCreate()
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
        mSensorManager = context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainView = layoutInflater.inflate(R.layout.fragment_compass,container,false)

        //init view
        locationTxt = mainView.findViewById(R.id.locationTxt)
        compassDial = mainView.findViewById(R.id.compassDial)
        compassKaaba = mainView.findViewById(R.id.compassKaaba)
        degreeTxt = mainView.findViewById(R.id.degreeTxt)
        distanceTxt = mainView.findViewById(R.id.distanceTxt)

        setupActionForOtherFragment(0,0,null,"Qibla Compass",true,mainView)
        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        askLocationPermission(false)
        showCalibrateDialog()
    }


    @SuppressLint("MissingPermission")
    private fun locationPermissionResult(bol:Boolean)
    {
        val currentTime = Date()
        val sdf = SimpleDateFormat("hh:mm aa", Locale.getDefault())
        val getTime =  sdf.format(currentTime)

        locationTxt.text = "Time: $getTime, Location: ..."
        if(bol) {
            locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
             locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    val latitude = location.latitude
                    val longitude = location.longitude

                    val bearingPoint =  getBearingBetweenTwoPoints(latitude,longitude)

                    bearing = if (bearingPoint > 0) {
                        bearingPoint
                    } else {
                        90 + bearingPoint
                    }

                    initKaabaDistance(latitude,longitude)

                    Log.e("onLocationChanged",latitude.toString())

                    lifecycleScope.launch(Dispatchers.IO)
                    {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            //Fetch address from location
                            geocoder.getFromLocation(
                                latitude,
                                longitude,
                                1,
                                object : Geocoder.GeocodeListener {
                                    override fun onGeocode(addresses: MutableList<Address>) {

                                        lifecycleScope.launch(Dispatchers.Main)
                                        {
                                            val state = addresses[0].adminArea

                                            locationTxt.text = "Time: $getTime, Location: $state"
                                            locationListener?.let {
                                                locationManager?.removeUpdates(it)
                                            }
                                        }


                                    }

                                    override fun onError(errorMessage: String?) {
                                        super.onError(errorMessage)

                                    }

                                })
                        } else {
                            try {
                                val addresses: List<Address> =
                                    geocoder.getFromLocation(
                                        latitude,
                                        longitude,
                                        1
                                    ) as List<Address>

                                if (addresses.isNotEmpty()) {
                                    lifecycleScope.launch(Dispatchers.Main)
                                    {
                                        val state = addresses[0].adminArea

                                        locationTxt.text = "Time: $getTime, Location: $state"
                                        locationListener?.let {
                                            locationManager?.removeUpdates(it)
                                        }
                                    }
                                    // Do something with the state information here
                                }
                            } catch (e: IOException) {
                                // Handle Geocoder exceptions here
                            }
                        }
                    }

                }

                @Deprecated("Deprecated in Java")
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

                override fun onProviderEnabled(provider: String) {
                }

                override fun onProviderDisabled(provider: String) {
                }
            }

           /* val isGpsEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)

            if (isGpsEnabled) {
                // GPS provider is not available, handle this case accordingly
            }*/

            locationListener?.let {
                locationManager?.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000,
                    100f,
                    it
                )

                locationManager?.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    1000,
                    100f,
                    it
                )
            }

        }

    }

    fun showSettingDialog(mContext: Context) {
        MaterialAlertDialogBuilder(mContext, com.google.android.material.R.style.MaterialAlertDialog_Material3)
            .setTitle("Location Permission")
            .setMessage("Location permission is required to show accurate content, Please allow location permission from setting")
            .setPositiveButton("Ok") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:${mContext.packageName}")
                mContext.startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun askLocationPermission(initialCheck:Boolean=false)
    {
        if (ContextCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                locationPermissionResult(false)

                if(!initialCheck) {

                    showSettingDialog(requireContext())

                }



            } else {

                locationPermissionResult(false)
                // No explanation needed, we can request the permission.
                if(!initialCheck)
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                        PERMISSION_CODE
                    )

            }
        } else {
            // Permission has already been granted
            locationPermissionResult(true)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {

        when (requestCode) {
            PERMISSION_CODE -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                    locationPermissionResult(true)
                } else {

                    locationPermissionResult(false)
                }
                return
            }

            else -> {
                locationPermissionResult(false)
            }
        }
    }


    override fun onResume() {
        super.onResume()
        askLocationPermission(true)
        view?.requestLayout()
        val mSensor = mSensorManager.getDefaultSensor(3)
        if (mSensor != null) {
            mSensorManager.registerListener(this, mSensor, 1)
        }
    }

    override fun onPause() {
        super.onPause()
        mSensorManager.unregisterListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        locationListener?.let {
            locationManager?.removeUpdates(it)
        }
    }

    private fun initKaabaDistance(latitude: Double, longitude: Double)
    {
        val distance = getDistance(
            latitude,
            longitude,
            MAKKAH_LATITUDE,
            MAKKAH_LONGITUDE
        ).toDouble()
        val distanceOfMakkah = "The distance from Kaaba is ${DecimalFormat("##.##").format(distance / 1000)} km"
        distanceTxt.text = distanceOfMakkah
    }

    fun getDistance(lat_a: Double, lng_a: Double, lat_b: Double, lng_b: Double): Int {
        var miter = 0
        try {
            val earthRadius = 3958.75
            val latDiff = Math.toRadians(lat_b - lat_a)
            val lngDiff = Math.toRadians(lng_b - lng_a)
            val a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
                    Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                    Math.sin(lngDiff / 2) * Math.sin(lngDiff / 2)
            val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
            val distance = earthRadius * c
            val meterConversion = 1609
            miter = (distance * meterConversion.toFloat().toInt()).toInt()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return miter
    }

    fun degreesToRadians(degrees: Double): Double {
        return degrees * (3.1416 / 180.0)
    }

    fun radiansToDegrees(radians: Double): Double {
        return radians * (180.0 / 3.1416)
    }

    fun getBearingBetweenTwoPoints(longitude: Double,latitude: Double): Double {
        val lat1 = degreesToRadians(degrees = longitude)
        val lon1 = degreesToRadians(degrees = latitude)

        val lat2 = degreesToRadians(degrees = MAKKAH_LATITUDE)
        val lon2 = degreesToRadians(degrees = MAKKAH_LONGITUDE)

        val dLon = lon2 - lon1

        val y = Math.sin(dLon) * Math.cos(lat2)
        val x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon)
        val radiansBearing = Math.atan2(y, x)
        return radiansToDegrees(radiansBearing)
    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {

        Log.e("onSensorChanged","OK")

        val degree = Math.round(sensorEvent?.values!!.get(0))
        compassDial.rotation = -degree.toFloat()

        //compassKaaba.rotation = -bearing?.toFloat()!!

        val degreeTxt = "${degree.toString() + 0x00B0.toChar()} from the geographical north"

        this.degreeTxt.text = degreeTxt

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

        when (p1) {
            SensorManager.SENSOR_STATUS_UNRELIABLE -> {
                // The sensor's accuracy is unreliable.
                if(this::accuracy.isInitialized)
                {
                    accuracy.text = "Unreliable"
                    accuracy.setTextColor(ContextCompat.getColor(requireContext(),R.color.brand_error))
                }
            }
            SensorManager.SENSOR_STATUS_ACCURACY_LOW -> {
                // The sensor's accuracy is low.
                if(this::accuracy.isInitialized)
                {
                    accuracy.text = "Low"
                    accuracy.setTextColor(ContextCompat.getColor(requireContext(),R.color.brand_error))
                }
            }
            SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> {
                // The sensor's accuracy is medium.
                if(this::accuracy.isInitialized)
                {
                    accuracy.text = "Medium"
                    accuracy.setTextColor(ContextCompat.getColor(requireContext(),R.color.yellow))
                }

            }
            SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> {
                // The sensor's accuracy is high.
                if(this::accuracy.isInitialized)
                {
                    accuracy.text = "High"
                    accuracy.setTextColor(ContextCompat.getColor(requireContext(),R.color.primary))
                }
            }
        }

    }

    private fun showCalibrateDialog()
    {
        dialog = BottomSheetDialog(requireContext())

        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_compass_calibrate, null)

        accuracy = view.findViewById(R.id.accuracy)
        val okBtn:MaterialButton = view.findViewById(R.id.okBtn)

        dialog?.setCancelable(false)

        dialog?.setContentView(view)

        dialog?.show()

        okBtn.setOnClickListener {
            dialog?.dismiss()
        }
    }

}