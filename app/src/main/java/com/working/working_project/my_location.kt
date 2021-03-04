package com.working.working_project

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Color.BLUE
import android.graphics.Insets.add
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.working.working_project.databinding.ActivityMyLocationBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.concurrent.timer
import kotlin.concurrent.timerTask
import kotlin.properties.Delegates

// fragment 에서 context 를 사용하려고하면 activity가 아니기 때문에 불가능. 그러므로 getActivity. activity 함수를 사용하여 액티비티를 얻어와야 한다.

// count 계산이 조금 이상하다. // 1초, 1m로 설정하고 카운트가 이전 카운트와 20m이상 차이날시 기록하지 않게하기.
// 네비게이션에 알람 추가, 옆 네비에서 삭제.

class my_location : Fragment(), OnMapReadyCallback, inter_run_information {

    private var user: FirebaseUser? = null
    private lateinit var firebaseDatabase: DatabaseReference
    var googleMap: GoogleMap? = null
    private lateinit var information:DatabaseReference

    lateinit var StartLatLng: LatLng // 버튼 누르면 true, false로 바뀌는것 사용해서 스타트 버튼 위치 지정.
    lateinit var lm: LocationManager

    var isGPSEnabled: Boolean = false
    var isNetworkEnabled: Boolean = false

    var middleLatLng: LatLng? = null
    var walk_line_option: PolylineOptions? = null
    var poly_lat1: LatLng? = null
    var poly_lat2: LatLng? = null
    var positi: Int = 0 // 위치 서비스 켜져있을경우 1 , 아닐경우 0
    var count:Float = 0.0F

    var gps = 0 // 최초 위치 파악용

    var lat1:Double? = null // 위치 시작
    var lat2:Double? = null // 위치 시작
    var lng1:Double? = null // 위치 종료
    var lng2:Double? = null // 위치 종료

    var poly_check = 0
    var add_check = 0

    val now = LocalDateTime.now()
    var move_count_sub:Double? = null
    val dateformat = DateTimeFormatter.ofPattern("yyyyMMdd")
    var move_object_percent:Double? = null



    private var key_list= arrayOfNulls<String>(99)
    private var value_list= arrayOfNulls<String>(99)


    //위치 변동 시 처음 위치로 기록하고 그 이후 기록을 처음위치부터 시작하기.

    //var walk_checkd:Boolean = false
    var walk_checkd = 0 // 달리기 버튼, 종료 버튼 확인용
    private var infor_checkd = 0 // 정보가 비어있는지 없는지 확인용.

    val gpsLocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) { //위치 값이 변경되면 실행되는 함수
            location?.let {
                if (gps == 0) { // lastlocation이 잘 안먹혀서 이거로 설정했음.  gps 변수는 최초 위치 저장용으로 설정해놨음.

                    Log.d("확인 gps", "gps 부분이다.")

                    gps = 1

                    middleLatLng = LatLng(it.latitude, it.longitude)
                    run() //실행 순서가 run(), onmapready, locationchanged 순서라 run을 가장 늦게 주려면 여기에 줘야함.
                }

                if(lat2 != null && lng2 != null) {
                    lat1 = lat2!! //lat2가 변하기 전에 기록
                    lng1 = lng2!! // lng2가 변하기 전에 기록
                }


                lat2 = it.latitude
                lng2 = it.longitude

                middleLatLng = LatLng(it.latitude, it.longitude)

                Log.d("확인미들", middleLatLng.toString())

                if (walk_checkd == 0 && googleMap != null) {
                    googleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(middleLatLng, 17F))
                }
            }

            if (walk_checkd == 1 && positi == 1) {
                Log.d("확인", "폴리라인 변경에서")
                if(lat1 != null && lng2 != null && lat2 != null && lng2 != null){
                    count += String.format("%.1f", runEnd(lat1!!, lng1!!, lat2!!, lng2!!)).toFloat() // lat1과 lat2의 위치 비교 후 카운트에 더하기.
                    Log.d("카운트는", count.toString())
                }

                polyline()
            }
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        }

        override fun onProviderEnabled(provider: String) {
        }

        override fun onProviderDisabled(provider: String) {
        }
    }

    private lateinit var binding: ActivityMyLocationBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = ActivityMyLocationBinding.inflate(layoutInflater, container, false)

        firebaseDatabase = FirebaseDatabase.getInstance().getReference()
        user = FirebaseAuth.getInstance().currentUser
        val username = user!!.email.toString().split("@")
        information = FirebaseDatabase.getInstance().getReference("member").child("${username[0]}")


        information.addListenerForSingleValueEvent(object : ValueEventListener { // 값 가져오기
            var i = -1
            var infor_count = 0

            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds in snapshot.children) {
                    i++
                    key_list[i] = ds.key
                    value_list[i] = ds.value.toString()
                    Log.d("값은", "$ds") // 어레이리스트에 ds.key와 value 담기.

                    when (key_list[i]) { // 반복문은 key값이 있는만큼 반복하므로 안에서 찾으면 된다. 없을시 else 실행.
                        "이름" -> {
                            infor_checkd++
                        }
                        "성별" -> {
                            infor_checkd++
                        }
                        "나이" -> {
                            infor_checkd++
                        }
                        "목표설정" -> {
                            infor_checkd++
                        }
                        "목표까지" -> {
                            infor_checkd++
                            move_object_percent = value_list[i]?.toDouble() // 목표까지 거리계산한 것 가져오기
                        }
                        "운동거리" -> {
                            infor_checkd++
                            move_count_sub = value_list[i]?.toDouble() // 운동거리 가져오기
                        }
                        else -> {
                        }
                    }
                }

                    if(infor_checkd >= 6)
                    {
                        for(i in value_list.indices){
                            if(value_list[i] == "0.0")
                                infor_count++
                            if(infor_count >= 3)
                            {
                                var a = AlertDialog.Builder(activity!!)
                                a.setTitle("정보를 입력해주세요.")
                                a.setMessage("해당 기능을 정상적으로 이용하기 위해서는 정보를 입력해주셔야합니다.")
                                a.setPositiveButton("이동") { DialogInterface, i ->
                                    var intent = Intent(activity!!, information_registor::class.java)
                                    startActivity(intent)
                                }
                                a.setNegativeButton("취소"){ dialogInterface: DialogInterface, i: Int ->
                                    false
                                }

                                a.show()
                                break
                            }

                        }
                    }

                    if(infor_checkd <= 5) {
                        information.child("이름").setValue("0.0")
                        information.child("성별").setValue("0.0")
                        information.child("나이").setValue("0.0")
                        information.child("목표설정").setValue("0.0")
                        information.child("목표까지").setValue("0.0")
                        information.child("운동거리").setValue("0.0")

                        var a = AlertDialog.Builder(activity!!)
                        a.setTitle("정보를 입력해주세요.")
                        a.setMessage("해당 기능을 정상적으로 이용하기 위해서는 정보를 입력해주셔야합니다.")
                        a.setPositiveButton("이동") { DialogInterface, i ->
                            var intent = Intent(activity!!, information_registor::class.java)
                            startActivity(intent)
                        }
                        a.setNegativeButton("취소") { dialogInterface: DialogInterface, i: Int ->
                            false
                        }

                        a.show()
                    }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        return binding.root
    }

    override fun onResume() {

        binding.runStart.setOnClickListener {
            if (Build.VERSION.SDK_INT >= 26 && //빌드 SDK 버전이 26 이상이고, 퍼미션 체크를 했을때 퍼미션을 허가받았는지 확인. GRANTED (허가받은)
                    ContextCompat.checkSelfPermission(activity!!.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this.activity!!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0) //허용하지 않았다면 request 코드 0을 부여.
                Log.d("지금 여기임", "여기임")
            } else {
                lm = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager

                isGPSEnabled =
                        lm.isProviderEnabled(LocationManager.GPS_PROVIDER) // gps 권한 여부 Boolean 표현
                isNetworkEnabled =
                        lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) // 네트워크 권한 여부 Boolean 표현

                when { // 프로바이더 제공자 활성화 여부 체크

                    isGPSEnabled -> {
                        positi = 1

                        Log.d("지금 여기임2", "여기임2")
                    }

                    isNetworkEnabled -> {
                        positi = 1

                        Log.d("지금 여기임1", "여기임1")
                    }

                    else -> {// 위치 서비스가 켜져 있지 않은 경우
                        var alter = AlertDialog.Builder(activity!!)
                        alter.setTitle("권한 허용").setMessage("위치 서비스가 켜져있지 않습니다. 기능을 켜주시기 바랍니다.")
                        alter.setPositiveButton("켜기") { DialogInterface, i ->
                            var intent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                            startActivity(intent)
                        }
                        Log.d("지금 여기임3", "여기임3")
                        alter.show()

                        positi = 0
                    }
                }

                if (positi == 1) {

                    val google_map = childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment // fragment 아래의 fragment라 그런지 chiled 사용해서 되었음.
                    google_map.getMapAsync(this) //getMapAsync 로 호출,


                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            2000, //몇초
                            1F, // 몇미터
                            gpsLocationListener)

                    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            2000, //몇초
                            1F, // 몇미터
                            gpsLocationListener) //위치 업데이트
                }
            }
        }

        super.onResume()
    }

    override fun onMapReady(googleMap: GoogleMap) { //OnmapReadyCallback이 호출시
        this.googleMap = googleMap
    }


    private fun run() {

        Log.d("확인 포지 run", positi.toString())
        Log.d("확인 포지 walk_checkd", walk_checkd.toString())
        Log.d("확인 포지 googleMap", googleMap.toString())

        if (walk_checkd != 1 && positi == 1 && googleMap != null) {
            googleMap!!.clear()
            add_check = 1
            addmarker()

            Log.d("확인 포지 run", positi.toString())

            walk_checkd = 1
            Toast.makeText(activity, "달리기 시작", Toast.LENGTH_SHORT).show()

        }

        binding.runEnd.setOnClickListener {

            if (walk_checkd == 1) {
                walk_checkd = 2
                add_check = 2

                fun information() {
                    move_count_sub = move_count_sub?.plus(count)
                    information.child("운동거리").setValue(String.format("%.1f", move_count_sub))
                    information.child("목표까지").setValue(String.format("%.1f", move_object_percent?.minus(count.toDouble())))
                    move_object_percent = move_object_percent!! - count.toDouble()
                }

                information()

                addmarker()
                lm.removeUpdates(gpsLocationListener)
                Toast.makeText(activity, "달리기 종료", Toast.LENGTH_SHORT).show()

                positi = 0 //초기화 작업
                gps = 0 // 초기화 작업

                if (middleLatLng != null) {
                    Toast.makeText(activity, "이동한 거리는 ${count}m입니다.", Toast.LENGTH_SHORT).show()
                    Log.d("거리 확인", "$count")
                } else {
                    Toast.makeText(activity, "이동 거리가 너무 짧아 기록을 할 수 없습니다.", Toast.LENGTH_SHORT).show()
                    Log.d("거리 확인", "일정 거리를 이동하지 않음.")
                }

                walk_line_option = null
                middleLatLng = null
                poly_lat1 = null
                poly_lat2 = null
                poly_check = 0
                add_check = 0
                lat1 = 0.0
                lat2 = 0.0
                lng1 = 0.0
                lng2 = 0.0
            }
            else {
                Toast.makeText(activity, "시작 버튼을 누르지 않은 상태입니다.", Toast.LENGTH_SHORT).show()
            }

           // member_information(String.format("%.2f", count), now.format(dateformat))  이동 거리와 운동한 날짜 기록한다.
        }
    }


    fun runEnd(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Float { //lat1, lng1의 위경도를 파악 후 lat2, lng2와 비교. 거리를 계산하는 함수

        lateinit var myLoc: Location
        lateinit var targetLoc: Location
        when {
            isGPSEnabled -> {
                myLoc = Location(LocationManager.GPS_PROVIDER)
                targetLoc = Location(LocationManager.GPS_PROVIDER)

                myLoc.latitude = lat1
                myLoc.longitude = lng1

                targetLoc.latitude = lat2
                targetLoc.longitude = lng2

                Log.d("gps 위치 확인 lat1", "$lat1")
                Log.d("gps 위치 확인 lng1", "$lng1")
                Log.d("gps 위치 확인 lat2", "$lat2")
                Log.d("gps 위치 확인 lng2", "$lng2")

            }

            isNetworkEnabled -> {
                myLoc = Location(LocationManager.NETWORK_PROVIDER)
                targetLoc = Location(LocationManager.NETWORK_PROVIDER)
                myLoc.latitude = lat1
                myLoc.longitude = lng1

                targetLoc.latitude = lat2
                targetLoc.longitude = lng2

                Log.d("Network 위치 확인 lat1", "$lat1")
                Log.d("Network 위치 확인 lng1", "$lng1")
                Log.d("Network 위치 확인 lat2", "$lat2")
                Log.d("Network 위치 확인 lng2", "$lng2")

            }
        }

        return myLoc.distanceTo(targetLoc) //거리를 계산 후 Float 형(미터) 로 반환한다.}
    }

    fun addmarker() {
            val LatLng = middleLatLng!!//LatLng(nx.toDouble(), ny.toDouble()) //위도 경도 지정

            val marker = MarkerOptions() //마커 설정
            marker.position(LatLng)
            googleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng, 17F)) //위도 경도에 맞는 위치로 카메라를 이동시킴.

            if(add_check == 1){
                marker.title("시작 위치")
                googleMap!!.addMarker(marker) // 지정해던 설정으로 마커 추가
            }
            else if(add_check == 2){
                marker.title("종료 위치")
                googleMap!!.addMarker(marker) // 지정해던 설정으로 마커 추가
            }
    }

    fun polyline() {
        if (poly_check == 0) {
            StartLatLng = middleLatLng!!//LatLng(nx.toDouble(), ny.toDouble())
            Log.d("확인 널", "널 확인")
            poly_check = 1
        }

        if (middleLatLng != null) {
            poly_lat1 = middleLatLng
            walk_line_option = PolylineOptions().add(StartLatLng).add(poly_lat1!!).width(8F).color(BLUE).geodesic(true) // 시작 ~ 끝까지 라인의 옵션. 굵기, 색상, 표시할지 구분
            Log.d("확인 성공", "성공중")
            Log.d("확인 성공", "$StartLatLng, $poly_lat1")

            StartLatLng = poly_lat1!!
            googleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(StartLatLng, 17F))
            googleMap!!.addPolyline(walk_line_option)
        }
    }
}
