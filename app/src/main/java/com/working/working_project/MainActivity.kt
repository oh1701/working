package com.working.working_project

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.working.working_project.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.properties.Delegates

val gpsLocationListener = object : LocationListener {
    override fun onLocationChanged(location: Location) {
        val provider: String = location.provider
        val longitude: Double = location.longitude // 경도
        val latitude: Double = location.latitude // 위도
        val altitude: Double = location.altitude // 고도
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onProviderEnabled(provider: String) {
    }

    override fun onProviderDisabled(provider: String) {
    }
}

val data_type = "JSON"
val num_Of_rows = 10
val page_No = 1
/*
var base_time = 2300
var base_date = 20210130
var nx = "60"
var ny = "127"*/

var base_time by Delegates.notNull<Int>() //by Delegates.notNull<Int>()
var base_date by Delegates.notNull<Int>()
var nx by Delegates.notNull<String>() // 위도
var ny by Delegates.notNull<String>() // 경도
val fcstValue = -1.0
var fcstDate by Delegates.notNull<Int>()
var fcstTime by Delegates.notNull<Int>()

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val time1 = "0210".toInt()
    val time2 = "0510".toInt()
    val time3 = "0810".toInt()
    val time4 = "1110".toInt()
    val time5 = "1410".toInt()
    val time6 = "1710".toInt()
    val time7 = "2010".toInt()
    val time8 = "2310".toInt()

    var TO_GRID = 0
    var TO_GPS = 1

    var loca_keung by Delegates.notNull<Double>()
    var loca_we by Delegates.notNull<Double>()

    // 23시 기준, 153개 조회

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

                val now = LocalDateTime.now() // 날짜, 시간을 표시하는 코드. now는 현재를 알아냄

                val timeformat = DateTimeFormatter.ofPattern("HHmm") // 데이트타임 형식을 패턴식으로 변환, HH = 시. mm = 분
                val dateformat = DateTimeFormatter.ofPattern("yyyyMMdd") // 데이트타임 형식을 패턴식으로 변환, yyyy = 년도. MM 월, dd 일.

                val nowtime = now.format(timeformat) // == 현재 시간
                val nowdate = now.format(dateformat) // == 현재 날짜

                base_time = nowtime.toInt()
                base_date = nowdate.toInt()

                if (base_time < time1) //23시 만들기
                    base_time = time8
                else if (base_time < time2) //02시 만들기
                    base_time = time1
                else if (base_time < time3) //05시 만들기
                    base_time = time2
                else if (base_time < time4) //08시 만들기
                    base_time = time3
                else if (base_time < time5) //11시 만들기
                    base_time = time4
                else if (base_time < time6) //14시 만들기
                    base_time = time5
                else if (base_time < time7) //17시 만들기
                    base_time = time6
                else if (base_time < time8) //20시 만들기
                    base_time = time7
                else base_time = time8 // 23시 만들기

                Log.d("발표 시간", "$base_time")
                Log.d("발표 날짜", "$base_date")

                fcstDate = nowdate.toInt()

                Log.d("현재 날짜", "$fcstDate")
                fcstTime = base_time

                binding.btn.setOnClickListener {


                    // 위치 (위도, 경도 구하기 시작.)
                    val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager

                    val isGPSEnabled: Boolean = lm.isProviderEnabled(LocationManager.GPS_PROVIDER) // gps 권한 여부 Boolean 표현
                    val isNetworkEnabled: Boolean = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) // 네트워크 권한 여부 Boolean 표현

                    if (Build.VERSION.SDK_INT >= 26 && //빌드 SDK 버전이 23 이상이고, 퍼미션 체크를 했을때 퍼미션을 허가받았는지 확인. GRANTED (허가받은)
                            ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0) //허용하지 않았다면 request 코드 0을 부여.
                    } else {
                        when { // 프로바이더 제공자 활성화 여부 체크
                            isNetworkEnabled -> {
                                val location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) // 인터넷 기반으로 위치를 찾는다
                                //getLastKnownLocation(매개변수) -> 매개변수에 담긴 문자열이 위치 정보 제공자. 위치값 얻지 못하면 null 반환, 값 가져오면 관련된 정보를 location 객체에 담아 전달.
                                val getLongitude = location?.longitude!! //경도
                                val getLatitude = location.latitude // 위도

                                binding.text.text = getLongitude.toString()
                                binding.text2.text = getLatitude.toString()

                                loca_keung = getLongitude
                                loca_we = getLatitude

                                Toast.makeText(this, "현재위치 불러옴", Toast.LENGTH_SHORT).show()
                            }

                            isGPSEnabled -> {
                                val location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) // GPS 기반으로 위치를 찾는다
                                //getLastKnownLocation(매개변수) -> 매개변수에 담긴 문자열이 위치 정보 제공자. 위치값 얻지 못하면 null 반환, 값 가져오면 관련된 정보를 location 객체에 담아 전달.

                                val getLongitude = location?.longitude!! //경도
                                val getLatitude = location.latitude // 위도

                                binding.text.text = getLongitude.toString()
                                binding.text2.text = getLatitude.toString()

                                loca_keung = getLongitude
                                loca_we = getLatitude

                                Log.d("위치", getLongitude.toString())
                                Log.d("위치", getLatitude.toString())

                                Toast.makeText(this, "현재위치 불러옴", Toast.LENGTH_SHORT).show()
                            }

                            else -> {

                            }

                            //몇초 간격과 몇미터를 이동했을 시에 호출되는 부분 - 주기적으로 위치 업데이트 시 사용
                            // *** 주기적 업데이트 사용하다가 사용안할시 반드시 해제 필요 ***
                            /* lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                 1000, //몇초
                 1F, // 몇미터
                 gpsLocationListener)
                 lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                 1000, //몇초
                 1F, // 몇미터
                 gpsLocationListener)

                 lm.removeUpdates(gpsLocationListener) // 해제 부분

                 */
                        }
                        lm.removeUpdates(gpsLocationListener)
                    }


                    //위도, 경도 구하기 끝

                    /*변환 확인*/
                    val tmp = convertGRID_GPS(TO_GRID, loca_we, loca_keung)
                    val tmp2 = convertGRID_GPS(TO_GRID, 37.01130555555556, 127.259875)
                    val tmp3 = convertGRID_GPS(TO_GRID, 33.500946412305076, 126.54663058817043)
                    Log.d(">>", "x = " + tmp.x + ", y = " + tmp.y)
                    Log.d(">>", "x = " + tmp2.x + ", y = " + tmp2.y)
                    Log.d(">>", "x = " + tmp3.x + ", y = " + tmp3.y)

                    //lat_x =  위도, lat_y = 경도

                    val int_tmp_x = tmp.x.toInt()
                    val int_tmp_y = tmp.y.toInt()

                    Log.d("위도", int_tmp_x.toString())
                    Log.d("경도", int_tmp_y.toString())

                    nx = int_tmp_x.toString() // nx에 위도를 격자로 변환한 값 넣어주기
                    ny = int_tmp_y.toString() // ny에 경도를 격자로 변환한 값 넣어주기

                    Log.d("nx", nx)

                    val call = ApiObject.retrofitService.GetWeather(data_type, num_Of_rows, page_No, base_date, base_time, nx, ny, fcstValue, fcstDate, fcstTime) // 날씨 api 불러오기
                    call.enqueue(object : retrofit2.Callback<WEATHER> { // enqueue == 데이터를 입력하는 함수
                        override fun onResponse(call: Call<WEATHER>, response: Response<WEATHER>) { //연결 성공 시
                            if (response.isSuccessful) {

                                Log.d("api 작동 1 :", response.body().toString())
                                Log.d("api 작동 2 : ", response.body()!!.response.body.items.item.toString())
                                Log.d("api 작동 3 : ", response.body()!!.response.body.items.item[0].category)

                                // POP = 강수확률 , PTY = 강수형태, R06 = 6시간 강수량, REH = 습도, SKY = 하늘상태, T3H 3시간 기온,

                                var rain_probabillity = "강수 확률"
                                var rain_form = "강수 형태"
                                var humidity = "습도"
                                var sky_weather = "하늘 상태"
                                var Temperature = "기온"

                                for (i in response.body()!!.response.body.items.item.indices) {

                                    if (response.body()!!.response.body.items.item[i].category == "POP") {
                                        rain_probabillity = response.body()!!.response.body.items.item[i].fcstValue.toString()
                                    }

                                    if (response.body()!!.response.body.items.item[i].category == "REH") {
                                        humidity = response.body()!!.response.body.items.item[i].fcstValue.toString()
                                    }

                                    if (response.body()!!.response.body.items.item[i].category == "T3H") {
                                        Temperature = response.body()!!.response.body.items.item[i].fcstValue.toString() // 기온
                                    }

                                    if (response.body()!!.response.body.items.item[i].category == "SKY") {
                                        when (response.body()!!.response.body.items.item[i].fcstValue.toInt()) {
                                            1 -> sky_weather = "맑음"
                                            3 -> sky_weather = "구름 많음"
                                            4 -> sky_weather = "흐림"
                                        }
                                    }

                                    if (response.body()!!.response.body.items.item[i].category == "PTY") {
                                        when (response.body()!!.response.body.items.item[i].fcstValue.toInt()) { // 강수형태
                                            0 -> rain_form = "맑음"
                                            1 -> rain_form = "비"
                                            2 -> rain_form = "진눈개비"
                                            3 -> rain_form = "눈"
                                            4 -> rain_form = "소나기"
                                            5 -> rain_form = "빗방울"
                                            6 -> rain_form = "진눈개비"
                                            7 -> rain_form = "눈날림"
                                        }
                                    }
                                }

                                Log.d("결과는 이렇습니다. : ", "현재 시간 $fcstTime 의 하늘 상태는 $sky_weather 이며 강수 형태는 $rain_form 입니다. 강수 확률은 $rain_probabillity % 이며, 기온은 $Temperature 입니다. 습도는 $humidity 입니다.")
                                Log.d("발표 시간", "$base_time")
                                Log.d("발표 날짜", "$base_date")
                                Log.d("현재 시간", "$fcstTime")
                                Log.d("현재 날짜", "$fcstDate")
                                Log.d("현재 위도", "$nx")
                                Log.d("현재 경도", "$ny")

                            }
                        }

                        override fun onFailure(call: Call<WEATHER>, t: Throwable) { //연결 성공 시
                            t.message?.let { Log.d("api fail :", it) }
                        }
                    })

                }


                binding.btn2.setOnClickListener {
                    Toast.makeText(this, "현재 위도는 $nx, 경도는 $ny 입니다.", Toast.LENGTH_LONG).show()
                }

                binding.naviSetting.setOnClickListener {
                    binding.drawerlay.openDrawer(GravityCompat.START)  // 왼쪽에서 화면 나옴
                }

                binding.naviView.setNavigationItemSelectedListener(this) //네비게이션 아이템 클릭 속성 부여.

                binding.move.setOnClickListener {
                    var intent = Intent(this, login_main::class.java)
                    startActivity(intent)
                }


        } //Oncreate 끝

    override fun onNavigationItemSelected(item: MenuItem): Boolean { //네비게이션 아이템 선택 시
        val drawer = findViewById<DrawerLayout>(R.id.drawerlay)
        var d = 0

        when (item.itemId) {
            R.id.navi_schedule -> Log.d("확인", "확인")
            R.id.navi_alarm -> Toast.makeText(applicationContext, "네비", Toast.LENGTH_SHORT).show()
            R.id.navi_weather -> Toast.makeText(applicationContext, "네비", Toast.LENGTH_SHORT).show()
            R.id.navi_my -> Toast.makeText(applicationContext, "네비", Toast.LENGTH_SHORT).show()
            R.id.navi_pet -> Toast.makeText(applicationContext, "네비", Toast.LENGTH_SHORT).show()
            R.id.navi_with -> Toast.makeText(applicationContext, "네비", Toast.LENGTH_SHORT).show()
            R.id.navi_my_everyday -> Toast.makeText(applicationContext, "네비", Toast.LENGTH_SHORT).show()
            R.id.navi_Alim -> Toast.makeText(applicationContext, "네비", Toast.LENGTH_SHORT).show()

        }

        drawer.closeDrawers()
        return false
    }

    override fun onBackPressed() { //뒤로가기 선택 시
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (binding.drawerlay.isDrawerOpen(GravityCompat.START)) // drawerlayout이 GravityCompat.START 식으로 열려있는 경우
        {
            binding.drawerlay.closeDrawers() // 닫아준다
        }
        else {
            super.onBackPressed()
        }
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

