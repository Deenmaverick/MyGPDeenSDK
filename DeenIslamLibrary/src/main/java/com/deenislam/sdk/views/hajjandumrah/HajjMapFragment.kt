package com.deenislam.sdk.views.hajjandumrah

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.common.MaterialButtonHorizontalListCallback
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.views.adapters.common.MaterialButtonHorizontalAdapter
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.deenislamic.R
import com.deenislamic.service.callback.common.MaterialButtonHorizontalListCallback
import com.deenislamic.service.network.response.prayerlearning.visualization.Head
import com.deenislamic.utils.singleton.CallBackProvider
import com.deenislamic.views.adapters.common.MaterialButtonHorizontalAdapter
import com.deenislamic.views.base.BaseRegularFragment
import com.deenislamic.views.main.MainActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.button.MaterialButton


internal class HajjMapFragment : BaseRegularFragment(), OnMapReadyCallback,
    MaterialButtonHorizontalListCallback {

    private lateinit var header: RecyclerView
    private lateinit var materialButtonHorizontalAdapter: MaterialButtonHorizontalAdapter
    private var mMap: GoogleMap? = null
    private lateinit var titleBtn:MaterialButton
    private lateinit var titleTxt:AppCompatTextView
    private lateinit var txtContent:AppCompatTextView
    private lateinit var nextBtn:MaterialButton

    private lateinit var trackerTitles: Array<String>
    private lateinit var stepList: ArrayList<LatLng>
    private lateinit var bitmapDescriptorArr:Array<BitmapDescriptor>
    private lateinit var colors:IntArray
    private var currentIndex = 0

    override fun OnCreate() {
        super.OnCreate()

        CallBackProvider.setFragment(this)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainview = localInflater.inflate(R.layout.fragment_hajj_map,container,false)

        header = mainview.findViewById(R.id.header)
        titleBtn = mainview.findViewById(R.id.titleBtn)
        titleTxt = mainview.findViewById(R.id.titleTxt)
        txtContent = mainview.findViewById(R.id.txtContent)
        nextBtn = mainview.findViewById(R.id.nextBtn)

        setupActionForOtherFragment(
            action1 = 0,
            action2 = 0,
            callback = null,
            actionnBartitle = getString(R.string.hajj_map),
            backEnable = true,
            view = mainview
        )

        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //setupBackPressCallback(this)

        if (!isDetached) {
            view.postDelayed({
                loadpage()
            }, 300)
        }
        else
            loadpage()


    }

    private fun loadpage()
    {
        header.apply {
            val linearLayoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
            layoutManager = linearLayoutManager
            materialButtonHorizontalAdapter = MaterialButtonHorizontalAdapter(
                head = getHeader(),
                activeTextColor = R.color.white,
                activeBgColor =  R.color.primary
            )
            adapter = materialButtonHorizontalAdapter
        }

        trackerTitles = localContext.resources.getStringArray(R.array.hajjMapTrackerPlace)

        initAllLocation()
        setUpMapFragment()

        nextBtn.setOnClickListener {

            if(currentIndex>=0 && currentIndex<materialButtonHorizontalAdapter.itemCount-1) {
                materialButtonHorizontalAdapter.nextPrev(currentIndex + 1)
                header.smoothScrollToPosition(currentIndex + 1)
                materialButtonHorizontalListClicked(currentIndex + 1)
            }

        }
    }

    private fun getHeader():List<Head>
    {
        val data:ArrayList<Head> = arrayListOf()

        if(getLanguage() == "en")
        {
            data.add(Head(0,"Start"))
            data.add(Head(1,"Day 1"))
            data.add(Head(2,"Day 2"))
            data.add(Head(3,"Day 3"))
            data.add(Head(4,"Day 4"))
            data.add(Head(5,"Day 5"))

        }
        else
        {
            data.add(Head(0,"শুরু করুন"))
            data.add(Head(1,"দিন ১"))
            data.add(Head(2,"দিন ২"))
            data.add(Head(3,"দিন ৩"))
            data.add(Head(4,"দিন ৪"))
            data.add(Head(5,"দিন ৫"))
        }

        return data
    }



    private fun setUpMapFragment() {
        val options = GoogleMapOptions()
        options.zoomControlsEnabled(true).compassEnabled(true)
        val fragment: SupportMapFragment = SupportMapFragment.newInstance()

        val transaction = parentFragmentManager.beginTransaction().replace(R.id.flMap, fragment)
        transaction.commit()

        fragment.getMapAsync(this)
    }



    private fun initAllLocation() {
        stepList = ArrayList()
        stepList.add(LatLng(21.42558, 39.82612))
        stepList.add(LatLng(21.42207, 39.8953))
        stepList.add(LatLng(21.3548, 39.98398))
        stepList.add(LatLng(21.38587, 39.91187))
        stepList.add(LatLng(21.42079, 39.87283))
        stepList.add(LatLng(21.41887, 39.82475))
        stepList.add(LatLng(21.41487, 39.88758))
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        mMap?.moveCamera(
            CameraUpdateFactory.newLatLng(
                LatLng(
                    21.383845775184625,
                    39.898824869751024
                )
            )
        )
        this.mMap?.animateCamera(CameraUpdateFactory.zoomTo(11.0f))
        this.mMap?.mapType = 1

        markAllLocationOnInit()
    }

    @SuppressLint("InflateParams")
    private fun markAllLocationOnInit() {

         bitmapDescriptorArr = arrayOf(
            BitmapDescriptorFactory.fromBitmap(
                resizeMapIcons(
                    resources.getResourceEntryName(R.drawable.ic_hajj_map_marker_a),
                    100,
                    100
                )
            ),
            BitmapDescriptorFactory.fromBitmap(
                resizeMapIcons(
                    resources.getResourceEntryName(R.drawable.ic_hajj_map_marker_b),
                    100,
                    100
                )
            ),
            BitmapDescriptorFactory.fromBitmap(
                resizeMapIcons(
                    resources.getResourceEntryName(R.drawable.ic_hajj_map_marker_c),
                    100,
                    100
                )
            ),
            BitmapDescriptorFactory.fromBitmap(
                resizeMapIcons(
                    resources.getResourceEntryName(R.drawable.ic_hajj_map_marker_d),
                    100,
                    100
                )
            ),
            BitmapDescriptorFactory.fromBitmap(
                resizeMapIcons(
                    resources.getResourceEntryName(R.drawable.ic_hajj_map_marker_e),
                    100,
                    100
                )
            ),
            BitmapDescriptorFactory.fromBitmap(
                resizeMapIcons(
                    resources.getResourceEntryName(R.drawable.ic_hajj_map_marker_f),
                    100,
                    100
                )
            ),
            BitmapDescriptorFactory.fromBitmap(
                resizeMapIcons(
                    resources.getResourceEntryName(R.drawable.ic_hajj_map_marker_g),
                    100,
                    100
                )
            )
        )

        val markerOne = mMap!!.addMarker(
            MarkerOptions().position(stepList[0]).title(trackerTitles[0]).icon(
                bitmapDescriptorArr[0]
            )
        )
        val markerTwo = mMap!!.addMarker(
            MarkerOptions().position(stepList[1]).title(trackerTitles[1]).icon(
                bitmapDescriptorArr[1]
            )
        )
        val markerThree = mMap!!.addMarker(
            MarkerOptions().position(stepList[2]).title(trackerTitles[2]).icon(
                bitmapDescriptorArr[2]
            )
        )
        val markerFour = mMap!!.addMarker(
            MarkerOptions().position(stepList[3]).title(trackerTitles[3]).icon(
                bitmapDescriptorArr[3]
            )
        )
        val markerFive = mMap!!.addMarker(
            MarkerOptions().position(stepList[4]).title(trackerTitles[4]).icon(
                bitmapDescriptorArr[4]
            )
        )
        val markerSix = mMap!!.addMarker(
            MarkerOptions().position(stepList[5]).title(trackerTitles[5]).icon(
                bitmapDescriptorArr[5]
            )
        )
        val markerSeven = mMap!!.addMarker(
            MarkerOptions().position(stepList[6]).title(trackerTitles[6]).icon(
                bitmapDescriptorArr[6]
            )
        )

        markerOne?.tag = 0
        markerTwo?.tag = 1
        markerThree?.tag = 2
        markerFour?.tag = 3
        markerFive?.tag = 4
        markerSix?.tag = 5
        markerSeven?.tag = 6

        mMap?.setOnMarkerClickListener { mMarker ->
            val tag = mMarker.tag as Int
            showMarkerDetails(tag)
            false
        }
        showDirection(stepList)
    }

    @SuppressLint("DiscouragedApi")
    private fun resizeMapIcons(iconName: String?, width: Int, height: Int): Bitmap {
        val imageBitmap = BitmapFactory.decodeResource(
            resources,
            resources.getIdentifier(iconName, "drawable", requireContext().packageName)
        )
        return Bitmap.createScaledBitmap(imageBitmap, width, height, false)
    }

    private fun showMarkerDetails(pos: Int) {

        Log.e("showMarkerDetails",pos.toString())
        val title = localContext.resources.getStringArray(R.array.hajjMapTrackerPlace)
        val details = localContext.resources.getStringArray(R.array.hajjMapStepDetails)

        when (pos) {
            0 -> {
                titleBtn.text = localContext.getString(R.string.start_the_journey)
                titleTxt.text = localContext.getString(R.string.makkah_masjid_al_haram)
                txtContent.text = localContext.getString(R.string.hajj_map_start_details)
            }
            7 -> {
                titleBtn.text =  getHeader()[5].Title
                titleTxt.text = title[pos-1]
                txtContent.text = details[pos-1]
            }
            else -> {
                when (pos) {
                    2,3 -> titleBtn.text = getHeader()[2].Title
                    4,5 -> titleBtn.text = getHeader()[3].Title
                    6 -> titleBtn.text = getHeader()[4].Title
                    else -> titleBtn.text = getHeader()[pos].Title
                }

                titleTxt.text = title[pos]
                txtContent.text = details[pos]
            }
        }

    }

    private fun showDirection(stepList: ArrayList<LatLng>) {
         colors = intArrayOf(
            ContextCompat.getColor(requireContext(), R.color.red),
            ContextCompat.getColor(requireContext(), R.color.blue),
            ContextCompat.getColor(requireContext(), R.color.green),
            ContextCompat.getColor(requireContext(), R.color.primary),
            ContextCompat.getColor(requireContext(), R.color.accent),
            ContextCompat.getColor(requireContext(), R.color.orange),
            ContextCompat.getColor(requireContext(), R.color.brand_secondary),
        )

        for (i in 0 until stepList.size - 1) {
            val line = mMap!!.addPolyline(PolylineOptions().add(stepList[i]).add(stepList[i + 1]))
            line.width = 10f
            line.isClickable = true
            line.color = colors[i]
        }
    }

    private fun drawMarker(pointA: Int = -1, pointB: Int = -1, pointC: Int = -1) {
        mMap?.clear()

        if (stepList.isEmpty()) {
            initAllLocation()
        }

        val pointsToDraw = listOf(pointA, pointB, pointC).filter { it != -1 }

        for (i in 0 until pointsToDraw.size) {
            val point = pointsToDraw[i]
            val title = trackerTitles[point]
            val marker = mMap?.addMarker(
                MarkerOptions().position(stepList[point])
                    .title(title)
                    .icon(
                    bitmapDescriptorArr[point]
                )
            )

            marker?.tag = pointsToDraw[i]

            if (i < pointsToDraw.size - 1) {
                mMap?.addPolyline(
                    PolylineOptions()
                        .add(stepList[point], stepList[pointsToDraw[i + 1]])
                        .width(10f)
                        .clickable(true)
                        .color(colors[point])
                )
            }
        }

        if (pointA != -1) {
            animateCamera(stepList[pointA])
        }

        mMap?.setOnMarkerClickListener { mMarker ->
            val tag = mMarker.tag as Int
            showMarkerDetails(tag)
            false
        }
    }




    private fun animateCamera(latLng: LatLng) {
        val offset = 0.050 // Adjust this value as needed

        val newLatLng = LatLng(latLng.latitude - offset, latLng.longitude)

        val cameraPosition = CameraPosition.Builder()
            .target(newLatLng)
            .zoom(11.0f)
            .build()

        mMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), null)
    }


    override fun materialButtonHorizontalListClicked(absoluteAdapterPosition: Int)
    {
        materialButtonHorizontalAdapter.notifyItemChanged(currentIndex)
        currentIndex = absoluteAdapterPosition

        when(absoluteAdapterPosition)
        {
            0 -> {
                mMap?.clear()
                markAllLocationOnInit()
                animateCamera(stepList[0])
            }

            1-> drawMarker(0,1)
            2-> drawMarker(1,2,3)
            3-> drawMarker(3,4,5)
            4-> drawMarker(5,6)
            5-> drawMarker(6)
        }

        when (absoluteAdapterPosition) {
            3 -> showMarkerDetails(pos = 4)
            4 -> showMarkerDetails(pos = 6)
            5 -> showMarkerDetails(pos = 7)
            else -> showMarkerDetails(pos = absoluteAdapterPosition)
        }

        materialButtonHorizontalAdapter.notifyItemChanged(currentIndex)

        nextBtn.isEnabled = currentIndex<materialButtonHorizontalAdapter.itemCount-1

    }

}