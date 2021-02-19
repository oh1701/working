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
import com.working.working_project.databinding.ActivityMyLocationBinding
import java.util.*
import kotlin.concurrent.timer
import kotlin.properties.Delegates

// fragment 에서 context 를 사용하려고하면 activity가 아니기 때문에 불가능. 그러므로 getActivity. activity 함수를 사용하여 액티비티를 얻어와야 한다.
//현재 문제점: polyline 이 처음 위치와 업데이트되는 지금의 위치가 계속 중첩되어 나타남.
// and GPS만 위치 업데이트가 먹힌다.

class my_location : Fragment(), OnMapReadyCallback, inter_run_information {

    var googleMap:GoogleMap? = null
    var nx:String = "0"
    var ny:String = "0"
    lateinit var StartLatLng:LatLng // 버튼 누르면 true, false로 바뀌는것 사용해서 스타트 버튼 위치 지정.
    lateinit var EndLatLng:LatLng
    lateinit var lm:LocationManager

    var isGPSEnabled: Boolean = false
    var isNetworkEnabled: Boolean = false

    var middleLatLng: LatLng? = null
    var distance:Float = 0F
    var walk_line_option:PolylineOptions? = null
    var poly_lat1:LatLng? = null
    var poly_lat2:LatLng? = null
    var positi:Int = 0

    var lat1 by Delegates.notNull<Double>()
    var lat2 by Delegates.notNull<Double>()
    var lng1 by Delegates.notNull<Double>()
    var lng2 by Delegates.notNull<Double>()


    //var walk_checkd:Boolean = false
    var walk_checkd = 0

    val gpsLocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) { //위치 값이 변경되면 실행되는 함수
            location?.let {
                middleLatLng = LatLng(it.latitude, it.longitude)
                Log.d("중간 값은", "${middleLatLng?.latitude} and ${middleLatLng?.longitude}")
                lat2 = it.latitude
                lng2 = it.longitude

                poly_lat1 = middleLatLng // 가장 최근의 위치

                if(walk_checkd == 0) {
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(middleLatLng, 17F))
                }
            }

            if(walk_checkd == 1 && positi == 1) {
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

    private lateinit var binding:ActivityMyLocationBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = ActivityMyLocationBinding.inflate(layoutInflater, container, false)

        loca()

        return binding.root
    }

    override fun onStart() {
        Log.d("확인", "onStart")//gps 설정 화면 이동시 onstart로 재구성되므로 여기에 runstart 문 설정.
        binding.runStart.setOnClickListener {
            loca()

            if(positi == 1) {
                val google_map = childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment // fragment 아래의 fragment라 그런지 chiled 사용해서 되었음.
                google_map.getMapAsync(this) //getMapAsync 로 호출,

                run()
            }
        }

        super.onStart()
    }

    override fun onMapReady(googleMap: GoogleMap) { //OnmapReadyCallback이 호출시
        this.googleMap = googleMap
        addmarker()
    }


    private fun run() {

            if (walk_checkd != 1 && positi == 1) {
                googleMap?.clear()
                addmarker()

                Log.d("확인 포지 run", positi.toString())

                lat1 = nx.toDouble() //라티튜드
                lng1 = ny.toDouble() // 롱티튜드

                walk_checkd = 1
                Toast.makeText(activity, "달리기 시작", Toast.LENGTH_SHORT).show()
        }

        binding.runEnd.setOnClickListener {
            if (walk_checkd != 2) {
                walk_checkd = 2
                Toast.makeText(activity, "달리기 종료", Toast.LENGTH_SHORT).show()

                positi = 0
                Log.d("확인 runend 포지", positi.toString())

                if (middleLatLng != null) {
                    EndLatLng = middleLatLng as LatLng //최종 위치는 middle 이 마지막으로 찍힌 값
                    distance = runEnd(lat1, lng1, lat2, lng2) //거리는 runend 함수의 리턴값.

                    Toast.makeText(activity, "이동한 거리는 $distance", Toast.LENGTH_SHORT).show()
                    Log.d("거리 확인", distance.toString())
                }
                else
                {
                    Toast.makeText(activity, "이동 거리가 너무 짧아 기록을 할 수 없습니다.", Toast.LENGTH_SHORT).show()
                    Log.d("거리 확인", "일정 거리를 이동하지 않음.")
                }

                /*if(walk_checkd == true) {
                walk_checkd = false

                EndLatLng = LatLng(nx.toDouble(), ny.toDouble()) // 종료 위치 지정, 1번만 설정된다.

                //when에서 측정한 값들 저장 후 다른 곳으로 정보 전달하기 (아마 인터페이스 사용)
                Toast.makeText(activity!!, "확인됨 : $EndLatLng", Toast.LENGTH_SHORT).show()
            }
            else{

            }*/

            }
        }
    }

    private fun loca(){
        if (Build.VERSION.SDK_INT >= 26 && //빌드 SDK 버전이 26 이상이고, 퍼미션 체크를 했을때 퍼미션을 허가받았는지 확인. GRANTED (허가받은)
                ContextCompat.checkSelfPermission(activity!!.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.activity!!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0) //허용하지 않았다면 request 코드 0을 부여.
            Log.d("지금 여기임", "여기임")
        }
        else {
            lm = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            isGPSEnabled =
                    lm.isProviderEnabled(LocationManager.GPS_PROVIDER) // gps 권한 여부 Boolean 표현
            isNetworkEnabled =
                    lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) // 네트워크 권한 여부 Boolean 표현

            when { // 프로바이더 제공자 활성화 여부 체크
                isNetworkEnabled -> {
                    val location =
                            lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) // 인터넷 기반으로 위치를 찾는다
                    //getLastKnownLocation(매개변수) -> 매개변수에 담긴 문자열이 위치 정보 제공자. 위치값 얻지 못하면 null 반환, 값 가져오면 관련된 정보를 location 객체에 담아 전달.

                    ny = location?.longitude!!.toString() //경도
                    nx = location.latitude.toString() // 위도

                    positi = 1

                    Log.d("지금 여기임1", "여기임1")
                }

                isGPSEnabled -> {
                    val location =
                            lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) // GPS 기반으로 위치를 찾는다
                    //getLastKnownLocation(매개변수) -> 매개변수에 담긴 문자열이 위치 정보 제공자. 위치값 얻지 못하면 null 반환, 값 가져오면 관련된 정보를 location 객체에 담아 전달.

                    ny = location?.longitude!!.toString() //경도
                    nx = location.latitude.toString() // 위도

                    positi = 1

                    Log.d("지금 여기임2", "여기임2")
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
        }
    }
    fun runEnd(lat1 : Double, lng1 : Double, lat2 : Double, lng2:Double): Float { //lat1, lng1의 위경도를 파악 후 lat2, lng2와 비교. 거리를 계산하는 함수

        val myLoc = Location(LocationManager.NETWORK_PROVIDER)
        val targetLoc = Location(LocationManager.NETWORK_PROVIDER)
        myLoc.latitude= lat1
        myLoc.longitude = lng1

        targetLoc.latitude= lat2
        targetLoc.longitude = lng2

        return myLoc.distanceTo(targetLoc) //거리를 계산 후 Float 형(미터) 로 반환한다.

    }

    fun addmarker(){
        val LatLng = LatLng(nx.toDouble(), ny.toDouble()) //위도 경도 지정
        Log.d("nx는 :", nx)
        Log.d("ny는 :", ny)

        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng, 17F)) //위도 경도에 맞는 위치로 카메라를 이동시킴.

        val marker = MarkerOptions() //마커 설정
        marker.position(LatLng)
        marker.title("시작 위치")

        googleMap?.addMarker(marker) // 지정해던 설정으로 마커 추가
    }

    fun polyline(){
        if(walk_line_option == null) {
            StartLatLng = LatLng(nx.toDouble(), ny.toDouble())
            walk_line_option = PolylineOptions().add(StartLatLng).add(middleLatLng).width(8F).color(BLUE).geodesic(true) // 시작 ~ 끝까지 라인의 옵션. 굵기, 색상, 표시할지 구분
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(middleLatLng, 17F)) // 폴리라인이 1번 설정되었을 경우 그다음부터는 middleLat 과 middleLat2를 설정해야함.
            googleMap?.addPolyline(walk_line_option) // 현재 위치로 계속 업데이트해줘야함.

            poly_lat2 = middleLatLng // 가장 최근 위치의 1번째 전 위치
        }
        else{
            if(poly_lat1 != poly_lat2) {
                val walk_line_option2 = PolylineOptions().add(poly_lat2).add(poly_lat1).width(8F).color(BLUE).geodesic(true)
                googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(poly_lat1, 17F)) //
                googleMap?.addPolyline(walk_line_option2) //

                poly_lat2 = middleLatLng //가장 최근 위치의 1번째 전 위치
            }
        }
    }

}