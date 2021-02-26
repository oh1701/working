package com.working.working_project

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.working.working_project.databinding.ActivityMainBinding
import com.working.working_project.databinding.ActivityWeatherFragBinding
import retrofit2.Call
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.properties.Delegates

class weather_frag : Fragment() {
    lateinit var binding:ActivityWeatherFragBinding

    val data_type = "JSON"
    var num_Of_rows = 0
    var page_No = 0

    var base_time by Delegates.notNull<String>() //by Delegates.notNull<Int>()
    var base_date by Delegates.notNull<String>()
    var nx by Delegates.notNull<String>() // 위도 격자
    var ny by Delegates.notNull<String>() // 경도 격자
    val fcstValue = -1.0 // 임의 지정
    var fcstDate by Delegates.notNull<String>()
    var fcstTime by Delegates.notNull<String>()
    var positi = 0
    var check_loca = 0
    lateinit var address: List<Address>

    var TO_GRID = 0
    var TO_GPS = 1

    var loca_keung = 0.0
    var loca_we = 0.0

    val firebaseAuth = FirebaseAuth.getInstance()

    val gpsLocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            val provider: String = location.provider
            val longitude: Double = location.longitude // 경도
            val latitude: Double = location.latitude // 위도
            val altitude: Double = location.altitude // 고도

            location?.let{
                if (loca_we == 0.0 && loca_keung == 0.0) {
                    loca_we = it.latitude
                    loca_keung = it.longitude

                    positi = 1
                    Log.d("확인1", "gpsLocationListener")
                    Log.d("loca_we", loca_we.toString())
                    Log.d("loca_keung", loca_keung.toString())
                    check_loca = 1
                }
            }
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        }

        override fun onProviderEnabled(provider: String) {
        }

        override fun onProviderDisabled(provider: String) {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = ActivityWeatherFragBinding.inflate(layoutInflater, container, false)

        val now = LocalDateTime.now() // 날짜, 시간을 표시하는 코드. now는 현재를 알아냄

        val timeformat = DateTimeFormatter.ofPattern("HHmm") // 데이트타임 형식을 패턴식으로 변환, H = 시간을 24시간으로 표현한 것. mm = 분
        val dateformat = DateTimeFormatter.ofPattern("yyyyMMdd") // 데이트타임 형식을 패턴식으로 변환, yyyy = 년도. MM 월, dd 일.

        var nowtime = now.format(timeformat) // == 현재 시간
        val nowdate = now.format(dateformat) // == 현재 날짜\

        Log.d("바뀌기 전 시간", nowtime)

        base_time = nowtime
        base_date = nowdate

        if (base_time.toInt() > now.format(DateTimeFormatter.ofPattern("H45")).toInt())  // 현재 시간이 30분 이후라면
            base_time = now.format(DateTimeFormatter.ofPattern("H45")) // 발표 시간을 현재 시, 30분으로 맞춤
        else
            base_time = (now.format(DateTimeFormatter.ofPattern("H45")).toInt() - 100).toString() // 현재 시간이 45분 이전이라면, 발표 시간을 1시간 전 45분으로 맞춤.

        if (base_time.toInt() < 100) { // 00시 일경우
            base_time = now.format(DateTimeFormatter.ofPattern("2345")) // 발표시간은 23시 45분 것.
            base_date = (base_date.toInt() - 1).toString() // 날짜는 이전 날을 사용
        }

        Log.d("발표 시간", "$base_time")
        Log.d("발표 날짜", "$base_date")

        fcstDate = nowdate
        fcstTime = nowtime

        Log.d("현재 시간은", "$fcstTime")
        Log.d("현재 날짜", "$fcstDate")

        return binding.root
    }

    override fun onResume() {

        Log.d("확인1", "onresume")

        var geocoder = Geocoder(activity!!) // 위도와 경도를 받아 주소를 나타내주는 함수

        if (Build.VERSION.SDK_INT >= 26 && //빌드 SDK 버전이 26 이상이고, 퍼미션 체크를 했을때 퍼미션을 허가받았는지 확인. GRANTED (허가받은)
                ContextCompat.checkSelfPermission(activity!!.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0) //허용하지 않았다면 request 코드 0을 부여.
        }
        else {
            val lm = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            // 위치 (위도, 경도 구하기 시작.)
            val isGPSEnabled: Boolean =
                    lm.isProviderEnabled(LocationManager.GPS_PROVIDER) // gps 권한 여부 Boolean 표현
            val isNetworkEnabled: Boolean =
                    lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) // 네트워크 권한 여부 Boolean 표현

            when { // 프로바이더 제공자 활성화 여부 체크
                isGPSEnabled -> {
                    Toast.makeText(activity!!, "현재위치 불러옴", Toast.LENGTH_SHORT).show()

                    positi = 1
                    Log.d("확인1", "isGPSEnabled")

                }

                isNetworkEnabled -> {

                    positi = 1
                    Log.d("확인1", "isNetworkEnabled")
                }

                else -> {
                    var alter = AlertDialog.Builder(activity!!)
                    alter.setTitle("권한 허용").setMessage("위치 서비스가 켜져있지 않습니다. 기능을 켜주시기 바랍니다.")
                    alter.setPositiveButton("켜기") { DialogInterface, i ->
                        var intent =
                                Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        startActivity(intent)
                    }
                    Log.d("확인1", "else")
                    alter.show()

                    positi = 0
                }
            }

            if (positi == 1) {
                lm.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        1000, //몇초
                        0F, // 몇미터
                        gpsLocationListener
                )

                lm.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        1000, //몇초
                        0F, // 몇미터
                        gpsLocationListener
                )

                if (loca_we != 0.0 && loca_keung != 0.0) {
                    lm.removeUpdates(gpsLocationListener)
                    Log.d("확인1", "removeUpdates")
                }
            }
        }


        //위도, 경도 구하기 끝


        /*변환 확인*/
        binding.btn.setOnClickListener {
            if (positi == 1 && loca_we != 0.0 && loca_keung != 0.0) {

                val tmp = convertGRID_GPS(TO_GRID, loca_we, loca_keung)
                val tmp2 = convertGRID_GPS(TO_GRID, 37.01130555555556, 127.259875)
                val tmp3 = convertGRID_GPS(TO_GRID, 33.500946412305076, 126.54663058817043)
                Log.d(">>", "x = " + tmp.x + ", y = " + tmp.y)
                Log.d(">>", "x = " + tmp2.x + ", y = " + tmp2.y)
                Log.d(">>", "x = " + tmp3.x + ", y = " + tmp3.y)

                //lat_x =  위도, lat_y = 경도

                val int_tmp_x = tmp.x.toInt()
                val int_tmp_y = tmp.y.toInt()

                Log.d("loca_we", loca_we.toString())
                Log.d("loca_keung", loca_keung.toString())

                Log.d("위도", int_tmp_x.toString())
                Log.d("경도", int_tmp_y.toString())

                nx = int_tmp_x.toString() // nx에 위도를 격자로 변환한 값 넣어주기
                ny = int_tmp_y.toString() // ny에 경도를 격자로 변환한 값 넣어주기

                Log.d("nx", nx)

                when (base_time) {
                    "0045" -> num_Of_rows = 6
                    "0145" -> num_Of_rows = 5
                    "0245" -> num_Of_rows = 4
                    "0345" -> num_Of_rows = 6
                    "0445" -> num_Of_rows = 5
                    "0545" -> num_Of_rows = 4
                    "0645" -> num_Of_rows = 6
                    "0745" -> num_Of_rows = 5
                    "0845" -> num_Of_rows = 4
                    "0945" -> num_Of_rows = 6
                    "1045" -> num_Of_rows = 5
                    "1145" -> num_Of_rows = 4
                    "1245" -> num_Of_rows = 6
                    "1345" -> num_Of_rows = 5
                    "1445" -> num_Of_rows = 4
                    "1545" -> num_Of_rows = 6
                    "1645" -> num_Of_rows = 5
                    "1745" -> num_Of_rows = 4
                    "1845" -> num_Of_rows = 6
                    "1945" -> num_Of_rows = 5
                    "2045" -> num_Of_rows = 4
                    "2145" -> num_Of_rows = 6
                    "2245" -> num_Of_rows = 5
                    "2345" -> num_Of_rows = 4

                }

                page_No = 0

                var rain_form = arrayOfNulls<String>(3) //"강수 형태"
                var humidity = arrayOfNulls<String>(3) //"습도"
                var sky_weather = arrayOfNulls<String>(3) //"하늘 상태"
                var Temperature = arrayOfNulls<String>(3) //"기온"

                for (i in 0..9) {
                    page_No++
                    val call = ApiObject.retrofitService.GetWeather(data_type, num_Of_rows, page_No, base_date, base_time, nx, ny, fcstValue, fcstDate, fcstTime) // 날씨 api 불러오기

                    call.enqueue(object : retrofit2.Callback<WEATHER> { // enqueue == 데이터를 입력하는 함수
                        override fun onResponse(call: Call<WEATHER>, response: Response<WEATHER>) { //연결 성공 시

                            if (response.isSuccessful) {

                                // 배열 [2]가 널일때 까지만 넣기.

                                //Log.d("api 작동 1 :", response.body().toString())
                                //Log.d("api 작동 2 : ", response.body()!!.response.body.items.item.toString())
                                //Log.d("api 작동 3 : ", response.body()!!.response.body.items.item[0].category)

                                // POP = 강수확률 , PTY = 강수형태, R06 = 6시간 강수량, REH = 습도, SKY = 하늘상태, T3H 3시간 기온,

                                var total = num_Of_rows - 1

                                for (e in 0..total) {

                                    for (j in 0..2) { // 지금 시간 ~ 2시간 후 시간 여기부분 고치기

                                        if (response.body()!!.response.body.items.item[e].category == "REH") { // 습도
                                            if (humidity[j] == null)
                                                humidity.set(j, response.body()!!.response.body.items.item[e].fcstValue.toString())
                                        }

                                        if (response.body()!!.response.body.items.item[e].category == "T1H") {
                                            if (Temperature[j] == null)
                                                Temperature.set(j, response.body()!!.response.body.items.item[e].fcstValue.toString()) // 기온
                                        }

                                        if (response.body()!!.response.body.items.item[e].category == "SKY") { // 하늘 상태
                                            if (sky_weather[j] == null) {
                                                when (response.body()!!.response.body.items.item[e].fcstValue.toInt()) {
                                                    1 -> sky_weather.set(j, "맑음")
                                                    3 -> sky_weather.set(j, "구름 많음")
                                                    4 -> sky_weather.set(j, "흐림")
                                                }
                                            }
                                        }

                                        if (response.body()!!.response.body.items.item[e].category == "PTY") {
                                            if (rain_form[j] == null) {
                                                when (response.body()!!.response.body.items.item[e].fcstValue.toInt()) { // 강수형태
                                                    0 -> rain_form.set(j, "비 없음")
                                                    1 -> rain_form.set(j, "비")
                                                    2 -> rain_form.set(j, "진눈개비")
                                                    3 -> rain_form.set(j, "눈")
                                                    4 -> rain_form.set(j, "소나기")
                                                    5 -> rain_form.set(j, "빗방울")
                                                    6 -> rain_form.set(j, "진눈개비")
                                                    7 -> rain_form.set(j, "눈날림")
                                                }
                                            }
                                        }

                                        if (i == 9 && e == total && j == 2) {
                                            binding.text.text = "결과는 이렇습니다. : 현재 시간 ${response.body()!!.response.body.items.item[0].fcstTime} 의 하늘 상태는 ${sky_weather[0]} 이며 강수 형태는 ${rain_form[0]} 입니다. " +
                                                    "기온은 ${Temperature[0]} 입니다. 습도는 ${humidity[0]} 입니다."
                                            Log.d("확인", "결과는 이렇습니다. : 현재 시간 ${response.body()!!.response.body.items.item[0].fcstTime} 의 하늘 상태는 ${sky_weather[0]} 이며 강수 형태는 ${rain_form[0]} 입니다. " +
                                                    "기온은 ${Temperature[0]} 입니다. 습도는 ${humidity[0]} 입니다.")
                                        }

                                    }

                                }
                            }
                        }

                        override fun onFailure(call: Call<WEATHER>, t: Throwable) { //연결 실패시 시
                            Log.d("api fail :", t.toString())
                            Toast.makeText(activity!!, "데이터를 받아올 수 없습니다.", Toast.LENGTH_SHORT).show()
                        }

                    })

                }


                Log.d("발표 시간", "$base_time")
                Log.d("발표 날짜", "$base_date")
                Log.d("현재 시간", "$fcstTime")
                Log.d("현재 날짜", "$fcstDate")
                Log.d("현재 위도", "$nx")
                Log.d("현재 경도", "$ny")

            }
        }

        binding.btn2.setOnClickListener {

            if(loca_we != null && loca_keung != null) {
                address = geocoder.getFromLocation(loca_we, loca_keung, 1)
                Log.d("찾은 주소", address.get(0).toString())
            }

            var subject_area: String

            if (address.get(0).subAdminArea != null) {

                var admin = address.get(0).adminArea.toString()
                var subadmin = address.get(0).subAdminArea.toString()
                var locality = address.get(0).locality.toString()
                var thorough = address.get(0).thoroughfare.toString()
                subject_area = admin + subadmin + locality + thorough
            } else {
                var admin = address.get(0).adminArea.toString()
                var locality = address.get(0).locality.toString()
                var thorough = address.get(0).thoroughfare.toString()
                subject_area = "$admin " + "$locality " + "$thorough"
            }

            Log.d("찾은 주소 지번 빼고", subject_area)
            binding.text2.text = subject_area
        }

        super.onResume()
    }

    private fun convertGRID_GPS(mode: Int, lat_X: Double, lng_Y: Double): LatXLngY { //위, 경도를 격자로 변환
        val RE = 6371.00877 // 지구 반경(km)
        val GRID = 5.0 // 격자 간격(km)
        val SLAT1 = 30.0 // 투영 위도1(degree)
        val SLAT2 = 60.0 // 투영 위도2(degree)
        val OLON = 126.0 // 기준점 경도(degree)
        val OLAT = 38.0 // 기준점 위도(degree)
        val XO = 43.0 // 기준점 X좌표(GRID)
        val YO = 136.0 // 기준점 Y좌표(GRID)

        //
        // LCC DFS 좌표변환 ( code : "TO_GRID"(위경도->좌표, lat_X:위도,  lng_Y:경도), "TO_GPS"(좌표->위경도,  lat_X:x, lng_Y:y) )
        //
        val DEGRAD = Math.PI / 180.0
        val RADDEG = 180.0 / Math.PI
        val re = RE / GRID
        val slat1 = SLAT1 * DEGRAD
        val slat2 = SLAT2 * DEGRAD
        val olon = OLON * DEGRAD
        val olat = OLAT * DEGRAD

        var sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5)
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn)
        var sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5)
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn
        var ro = Math.tan(Math.PI * 0.25 + olat * 0.5)
        ro = re * sf / Math.pow(ro, sn)
        val rs = LatXLngY()
        if (mode == TO_GRID) {
            rs.lat = lat_X
            rs.lng = lng_Y
            var ra = Math.tan(Math.PI * 0.25 + lat_X * DEGRAD * 0.5)
            ra = re * sf / Math.pow(ra, sn)
            var theta = lng_Y * DEGRAD - olon
            if (theta > Math.PI) theta -= 2.0 * Math.PI
            if (theta < -Math.PI) theta += 2.0 * Math.PI
            theta *= sn
            rs.x = Math.floor(ra * Math.sin(theta) + XO + 0.5)
            rs.y = Math.floor(ro - ra * Math.cos(theta) + YO + 0.5)
        } else {
            rs.x = lat_X
            rs.y = lng_Y
            val xn = lat_X - XO
            val yn = ro - lng_Y + YO
            var ra = Math.sqrt(xn * xn + yn * yn)
            if (sn < 0.0) {
                ra = -ra
            }
            var alat = Math.pow(re * sf / ra, 1.0 / sn)
            alat = 2.0 * Math.atan(alat) - Math.PI * 0.5
            var theta = 0.0
            if (Math.abs(xn) <= 0.0) {
                theta = 0.0
            } else {
                if (Math.abs(yn) <= 0.0) {
                    theta = Math.PI * 0.5
                    if (xn < 0.0) {
                        theta = -theta
                    }
                } else theta = Math.atan2(xn, yn)
            }
            val alon = theta / sn + olon
            rs.lat = alat * RADDEG
            rs.lng = alon * RADDEG
        }
        return rs
    }


    internal class LatXLngY {
        var lat = 0.0
        var lng = 0.0
        var x = 0.0
        var y = 0.0
    }
}