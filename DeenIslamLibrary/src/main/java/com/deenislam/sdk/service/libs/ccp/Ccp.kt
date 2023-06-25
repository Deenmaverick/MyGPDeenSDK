package com.deenislam.sdk.service.libs.ccp

import android.app.Dialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

internal class Ccp {

    private var instance:Ccp ? =null
    private var dialog: Dialog? = null
    private lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder
    private lateinit var customAlertDialogView : View
    private var ccp_list:ArrayList<CcpModel> = arrayListOf()
    private lateinit var ccp_adapter:CcpAdapter


    fun getInstance():Ccp
    {
        if(instance == null)
            instance = Ccp()
        return instance as Ccp
    }

    fun initCcp(context: Context,callback: CcpAdapterCallback)
    {
        materialAlertDialogBuilder = MaterialAlertDialogBuilder(context,R.style.MaterialAlertDialog_Rounded)
        customAlertDialogView = LayoutInflater.from(context)
            .inflate(R.layout.dialog_ccp, null, false)

        // Initialize and assign variable
        val search_ccp = customAlertDialogView.findViewById<TextInputEditText>(R.id.search_ccp)
        val countryList = customAlertDialogView.findViewById<RecyclerView>(R.id.countryList)
        val dismissBtn = customAlertDialogView.findViewById<ImageButton>(R.id.closeBtn)

            ccp_adapter = CcpAdapter(load_ccp_list(),callback)

            countryList.adapter = ccp_adapter

        dismissBtn?.setOnClickListener {
            dialog?.dismiss()
        }

        search_ccp?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    ccp_adapter.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // show dialog

        dialog = materialAlertDialogBuilder
            .setView(customAlertDialogView)
            .setCancelable(false)
            .show()
    }

    private fun load_ccp_list():ArrayList<CcpModel>
    {

            ccp_list.add(CcpModel("ad", "376", "Andorra"))
            ccp_list.add(CcpModel("ae", "971", "United Arab Emirates (UAE)"))
            ccp_list.add(CcpModel("af", "93", "Afghanistan"))
            ccp_list.add(CcpModel("ag", "1", "Antigua and Barbuda"))
            ccp_list.add(CcpModel("ai", "1", "Anguilla"))
            ccp_list.add(CcpModel("al", "355", "Albania"))
            ccp_list.add(CcpModel("am", "374", "Armenia"))
            ccp_list.add(CcpModel("ao", "244", "Angola"))
            ccp_list.add(CcpModel("aq", "672", "Antarctica"))
            ccp_list.add(CcpModel("ar", "54", "Argentina"))
            ccp_list.add(CcpModel("as", "1", "American Samoa"))
            ccp_list.add(CcpModel("at", "43", "Austria"))
            ccp_list.add(CcpModel("au", "61", "Australia"))
            ccp_list.add(CcpModel("aw", "297", "Aruba"))
            ccp_list.add(CcpModel("ax", "358", "Åland Islands"))
            ccp_list.add(CcpModel("az", "994", "Azerbaijan"))
            ccp_list.add(CcpModel("ba", "387", "Bosnia And Herzegovina"))
            ccp_list.add(CcpModel("bb", "1", "Barbados"))
            ccp_list.add(CcpModel("bd", "880", "Bangladesh"))
            ccp_list.add(CcpModel("be", "32", "Belgium"))
            ccp_list.add(CcpModel("bf", "226", "Burkina Faso"))
            ccp_list.add(CcpModel("bg", "359", "Bulgaria"))
            ccp_list.add(CcpModel("bh", "973", "Bahrain"))
            ccp_list.add(CcpModel("bi", "257", "Burundi"))
            ccp_list.add(CcpModel("bj", "229", "Benin"))
            ccp_list.add(CcpModel("bl", "590", "Saint Barthélemy"))
            ccp_list.add(CcpModel("bm", "1", "Bermuda"))
            ccp_list.add(CcpModel("bn", "673", "Brunei Darussalam"))
            ccp_list.add(CcpModel("bo", "591", "Bolivia, Plurinational State Of"))
            ccp_list.add(CcpModel("br", "55", "Brazil"))
            ccp_list.add(CcpModel("bs", "1", "Bahamas"))
            ccp_list.add(CcpModel("bt", "975", "Bhutan"))
            ccp_list.add(CcpModel("bw", "267", "Botswana"))
            ccp_list.add(CcpModel("by", "375", "Belarus"))
            ccp_list.add(CcpModel("bz", "501", "Belize"))
            ccp_list.add(CcpModel("ca", "1", "Canada"))
            ccp_list.add(CcpModel("cc", "61", "Cocos (keeling) Islands"))
            ccp_list.add(
                CcpModel(
                    "cd",
                    "243",
                    "Congo, The Democratic Republic Of The"

                )
            )
            ccp_list.add(CcpModel("cf", "236", "Central African Republic"))
            ccp_list.add(CcpModel("cg", "242", "Congo"))
            ccp_list.add(CcpModel("ch", "41", "Switzerland"))
            ccp_list.add(CcpModel("ci", "225", "Côte D'ivoire"))
            ccp_list.add(CcpModel("ck", "682", "Cook Islands"))
            ccp_list.add(CcpModel("cl", "56", "Chile"))
            ccp_list.add(CcpModel("cm", "237", "Cameroon"))
            ccp_list.add(CcpModel("cn", "86", "China"))
            ccp_list.add(CcpModel("co", "57", "Colombia"))
            ccp_list.add(CcpModel("cr", "506", "Costa Rica"))
            ccp_list.add(CcpModel("cu", "53", "Cuba"))
            ccp_list.add(CcpModel("cv", "238", "Cape Verde"))
            ccp_list.add(CcpModel("cw", "599", "Curaçao"))
            ccp_list.add(CcpModel("cx", "61", "Christmas Island"))
            ccp_list.add(CcpModel("cy", "357", "Cyprus"))
            ccp_list.add(CcpModel("cz", "420", "Czech Republic"))
            ccp_list.add(CcpModel("de", "49", "Germany"))
            ccp_list.add(CcpModel("dj", "253", "Djibouti"))
            ccp_list.add(CcpModel("dk", "45", "Denmark"))
            ccp_list.add(CcpModel("dm", "1", "Dominica"))
            ccp_list.add(CcpModel("do", "1", "Dominican Republic"))
            ccp_list.add(CcpModel("dz", "213", "Algeria"))
            ccp_list.add(CcpModel("ec", "593", "Ecuador"))
            ccp_list.add(CcpModel("ee", "372", "Estonia"))
            ccp_list.add(CcpModel("eg", "20", "Egypt"))
            ccp_list.add(CcpModel("er", "291", "Eritrea"))
            ccp_list.add(CcpModel("es", "34", "Spain"))
            ccp_list.add(CcpModel("et", "251", "Ethiopia"))
            ccp_list.add(CcpModel("fi", "358", "Finland"))
            ccp_list.add(CcpModel("fj", "679", "Fiji"))
            ccp_list.add(CcpModel("fk", "500", "Falkland Islands (malvinas)"))
            ccp_list.add(CcpModel("fm", "691", "Micronesia, Federated States Of"))
            ccp_list.add(CcpModel("fo", "298", "Faroe Islands"))
            ccp_list.add(CcpModel("fr", "33", "France"))
            ccp_list.add(CcpModel("ga", "241", "Gabon"))
            ccp_list.add(CcpModel("gb", "44", "United Kingdom"))
            ccp_list.add(CcpModel("gd", "1", "Grenada"))
            ccp_list.add(CcpModel("ge", "995", "Georgia"))
            ccp_list.add(CcpModel("gf", "594", "French Guyana"))
            ccp_list.add(CcpModel("gh", "233", "Ghana"))
            ccp_list.add(CcpModel("gi", "350", "Gibraltar"))
            ccp_list.add(CcpModel("gl", "299", "Greenland"))
            ccp_list.add(CcpModel("gm", "220", "Gambia"))
            ccp_list.add(CcpModel("gn", "224", "Guinea"))
            ccp_list.add(CcpModel("gp", "450", "Guadeloupe"))
            ccp_list.add(CcpModel("gq", "240", "Equatorial Guinea"))
            ccp_list.add(CcpModel("gr", "30", "Greece"))
            ccp_list.add(CcpModel("gt", "502", "Guatemala"))
            ccp_list.add(CcpModel("gu", "1", "Guam"))
            ccp_list.add(CcpModel("gw", "245", "Guinea-bissau"))
            ccp_list.add(CcpModel("gy", "592", "Guyana"))
            ccp_list.add(CcpModel("hk", "852", "Hong Kong"))
            ccp_list.add(CcpModel("hn", "504", "Honduras"))
            ccp_list.add(CcpModel("hr", "385", "Croatia"))
            ccp_list.add(CcpModel("ht", "509", "Haiti"))
            ccp_list.add(CcpModel("hu", "36", "Hungary"))
            ccp_list.add(CcpModel("id", "62", "Indonesia"))
            ccp_list.add(CcpModel("ie", "353", "Ireland"))
            ccp_list.add(CcpModel("il", "972", "Israel"))
            ccp_list.add(CcpModel("im", "44", "Isle Of Man"))
            ccp_list.add(CcpModel("is", "354", "Iceland"))
            ccp_list.add(CcpModel("in", "91", "India"))
            ccp_list.add(CcpModel("io", "246", "British Indian Ocean Territory"))
            ccp_list.add(CcpModel("iq", "964", "Iraq"))
            ccp_list.add(CcpModel("ir", "98", "Iran, Islamic Republic Of"))
            ccp_list.add(CcpModel("it", "39", "Italy"))
            ccp_list.add(CcpModel("je", "44", "Jersey "))
            ccp_list.add(CcpModel("jm", "1", "Jamaica"))
            ccp_list.add(CcpModel("jo", "962", "Jordan"))
            ccp_list.add(CcpModel("jp", "81", "Japan"))
            ccp_list.add(CcpModel("ke", "254", "Kenya"))
            ccp_list.add(CcpModel("kg", "996", "Kyrgyzstan"))
            ccp_list.add(CcpModel("kh", "855", "Cambodia"))
            ccp_list.add(CcpModel("ki", "686", "Kiribati"))
            ccp_list.add(CcpModel("km", "269", "Comoros"))
            ccp_list.add(CcpModel("kn", "1", "Saint Kitts and Nevis"))
            ccp_list.add(CcpModel("kp", "850", "North Korea"))
            ccp_list.add(CcpModel("kr", "82", "South Korea"))
            ccp_list.add(CcpModel("kw", "965", "Kuwait"))
            ccp_list.add(CcpModel("ky", "1", "Cayman Islands"))
            ccp_list.add(CcpModel("kz", "7", "Kazakhstan"))
            ccp_list.add(CcpModel("la", "856", "Lao People's Democratic Republic"))
            ccp_list.add(CcpModel("lb", "961", "Lebanon"))
            ccp_list.add(CcpModel("lc", "1", "Saint Lucia"))
            ccp_list.add(CcpModel("li", "423", "Liechtenstein"))
            ccp_list.add(CcpModel("lk", "94", "Sri Lanka"))
            ccp_list.add(CcpModel("lr", "231", "Liberia"))
            ccp_list.add(CcpModel("ls", "266", "Lesotho"))
            ccp_list.add(CcpModel("lt", "370", "Lithuania"))
            ccp_list.add(CcpModel("lu", "352", "Luxembourg"))
            ccp_list.add(CcpModel("lv", "371", "Latvia"))
            ccp_list.add(CcpModel("ly", "218", "Libya"))
            ccp_list.add(CcpModel("ma", "212", "Morocco"))
            ccp_list.add(CcpModel("mc", "377", "Monaco"))
            ccp_list.add(CcpModel("md", "373", "Moldova, Republic Of"))
            ccp_list.add(CcpModel("me", "382", "Montenegro"))
            ccp_list.add(CcpModel("mf", "590", "Saint Martin"))
            ccp_list.add(CcpModel("mg", "261", "Madagascar"))
            ccp_list.add(CcpModel("mh", "692", "Marshall Islands"))
            ccp_list.add(CcpModel("mk", "389", "Macedonia (FYROM)"))
            ccp_list.add(CcpModel("ml", "223", "Mali"))
            ccp_list.add(CcpModel("mm", "95", "Myanmar"))
            ccp_list.add(CcpModel("mn", "976", "Mongolia"))
            ccp_list.add(CcpModel("mo", "853", "Macau"))
            ccp_list.add(CcpModel("mp", "1", "Northern Mariana Islands"))
            ccp_list.add(CcpModel("mq", "596", "Martinique"))
            ccp_list.add(CcpModel("mr", "222", "Mauritania"))
            ccp_list.add(CcpModel("ms", "1", "Montserrat"))
            ccp_list.add(CcpModel("mt", "356", "Malta"))
            ccp_list.add(CcpModel("mu", "230", "Mauritius"))
            ccp_list.add(CcpModel("mv", "960", "Maldives"))
            ccp_list.add(CcpModel("mw", "265", "Malawi"))
            ccp_list.add(CcpModel("mx", "52", "Mexico"))
            ccp_list.add(CcpModel("my", "60", "Malaysia"))
            ccp_list.add(CcpModel("mz", "258", "Mozambique"))
            ccp_list.add(CcpModel("na", "264", "Namibia"))
            ccp_list.add(CcpModel("nc", "687", "New Caledonia"))
            ccp_list.add(CcpModel("ne", "227", "Niger"))
            ccp_list.add(CcpModel("nf", "672", "Norfolk Islands"))
            ccp_list.add(CcpModel("ng", "234", "Nigeria"))
            ccp_list.add(CcpModel("ni", "505", "Nicaragua"))
            ccp_list.add(CcpModel("nl", "31", "Netherlands"))
            ccp_list.add(CcpModel("no", "47", "Norway"))
            ccp_list.add(CcpModel("np", "977", "Nepal"))
            ccp_list.add(CcpModel("nr", "674", "Nauru"))
            ccp_list.add(CcpModel("nu", "683", "Niue"))
            ccp_list.add(CcpModel("nz", "64", "New Zealand"))
            ccp_list.add(CcpModel("om", "968", "Oman"))
            ccp_list.add(CcpModel("pa", "507", "Panama"))
            ccp_list.add(CcpModel("pe", "51", "Peru"))
            ccp_list.add(CcpModel("pf", "689", "French Polynesia"))
            ccp_list.add(CcpModel("pg", "675", "Papua New Guinea"))
            ccp_list.add(CcpModel("ph", "63", "Philippines"))
            ccp_list.add(CcpModel("pk", "92", "Pakistan"))
            ccp_list.add(CcpModel("pl", "48", "Poland"))
            ccp_list.add(CcpModel("pm", "508", "Saint Pierre And Miquelon"))
            ccp_list.add(CcpModel("pn", "870", "Pitcairn Islands"))
            ccp_list.add(CcpModel("pr", "1", "Puerto Rico"))
            ccp_list.add(CcpModel("ps", "970", "Palestine"))
            ccp_list.add(CcpModel("pt", "351", "Portugal"))
            ccp_list.add(CcpModel("pw", "680", "Palau"))
            ccp_list.add(CcpModel("py", "595", "Paraguay"))
            ccp_list.add(CcpModel("qa", "974", "Qatar"))
            ccp_list.add(CcpModel("re", "262", "Réunion"))
            ccp_list.add(CcpModel("ro", "40", "Romania"))
            ccp_list.add(CcpModel("rs", "381", "Serbia"))
            ccp_list.add(CcpModel("ru", "7", "Russian Federation"))
            ccp_list.add(CcpModel("rw", "250", "Rwanda"))
            ccp_list.add(CcpModel("sa", "966", "Saudi Arabia"))
            ccp_list.add(CcpModel("sb", "677", "Solomon Islands"))
            ccp_list.add(CcpModel("sc", "248", "Seychelles"))
            ccp_list.add(CcpModel("sd", "249", "Sudan"))
            ccp_list.add(CcpModel("se", "46", "Sweden"))
            ccp_list.add(CcpModel("sg", "65", "Singapore"))
            ccp_list.add(
                CcpModel(
                    "sh",
                    "290",
                    "Saint Helena, Ascension And Tristan Da Cunha"

                )
            )
            ccp_list.add(CcpModel("si", "386", "Slovenia"))
            ccp_list.add(CcpModel("sk", "421", "Slovakia"))
            ccp_list.add(CcpModel("sl", "232", "Sierra Leone"))
            ccp_list.add(CcpModel("sm", "378", "San Marino"))
            ccp_list.add(CcpModel("sn", "221", "Senegal"))
            ccp_list.add(CcpModel("so", "252", "Somalia"))
            ccp_list.add(CcpModel("sr", "597", "Suriname"))
            ccp_list.add(CcpModel("ss", "211", "South Sudan"))
            ccp_list.add(CcpModel("st", "239", "Sao Tome And Principe"))
            ccp_list.add(CcpModel("sv", "503", "El Salvador"))
            ccp_list.add(CcpModel("sx", "1", "Sint Maarten"))
            ccp_list.add(CcpModel("sy", "963", "Syrian Arab Republic"))
            ccp_list.add(CcpModel("sz", "268", "Swaziland"))
            ccp_list.add(CcpModel("tc", "1", "Turks and Caicos Islands"))
            ccp_list.add(CcpModel("td", "235", "Chad"))
            ccp_list.add(CcpModel("tg", "228", "Togo"))
            ccp_list.add(CcpModel("th", "66", "Thailand"))
            ccp_list.add(CcpModel("tj", "992", "Tajikistan"))
            ccp_list.add(CcpModel("tk", "690", "Tokelau"))
            ccp_list.add(CcpModel("tl", "670", "Timor-leste"))
            ccp_list.add(CcpModel("tm", "993", "Turkmenistan"))
            ccp_list.add(CcpModel("tn", "216", "Tunisia"))
            ccp_list.add(CcpModel("to", "676", "Tonga"))
            ccp_list.add(CcpModel("tr", "90", "Turkey"))
            ccp_list.add(CcpModel("tt", "1", "Trinidad &amp; Tobago"))
            ccp_list.add(CcpModel("tv", "688", "Tuvalu"))
            ccp_list.add(CcpModel("tw", "886", "Taiwan"))
            ccp_list.add(CcpModel("tz", "255", "Tanzania, United Republic Of"))
            ccp_list.add(CcpModel("ua", "380", "Ukraine"))
            ccp_list.add(CcpModel("ug", "256", "Uganda"))
            ccp_list.add(CcpModel("us", "1", "United States"))
            ccp_list.add(CcpModel("uy", "598", "Uruguay"))
            ccp_list.add(CcpModel("uz", "998", "Uzbekistan"))
            ccp_list.add(CcpModel("va", "379", "Holy See (vatican City State)"))
            ccp_list.add(CcpModel("vc", "1", "Saint Vincent &amp; The Grenadines"))
            ccp_list.add(CcpModel("ve", "58", "Venezuela, Bolivarian Republic Of"))
            ccp_list.add(CcpModel("vg", "1", "British Virgin Islands"))
            ccp_list.add(CcpModel("vi", "1", "US Virgin Islands"))
            ccp_list.add(CcpModel("vn", "84", "Vietnam"))
            ccp_list.add(CcpModel("vu", "678", "Vanuatu"))
            ccp_list.add(CcpModel("wf", "681", "Wallis And Futuna"))
            ccp_list.add(CcpModel("ws", "685", "Samoa"))
            ccp_list.add(CcpModel("xk", "383", "Kosovo"))
            ccp_list.add(CcpModel("ye", "967", "Yemen"))
            ccp_list.add(CcpModel("yt", "262", "Mayotte"))
            ccp_list.add(CcpModel("za", "27", "South Africa"))
            ccp_list.add(CcpModel("zm", "260", "Zambia"))
            ccp_list.add(CcpModel("zw", "263", "Zimbabwe"))


        return ccp_list
    }

    fun close_ccp()
    {
       dialog?.dismiss()
    }

}