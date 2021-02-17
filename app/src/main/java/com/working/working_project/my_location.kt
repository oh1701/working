package com.working.working_project

import android.Manifest
import android.content.Context
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

class my_location : Fragment(), OnMapReadyCallback, inter_run_information {

    lateinit var googleMap:GoogleMap
    lateinit var nx:String
    lateinit var ny:String
    lateinit var StartLatLng:LatLng // 버튼 누르면 true, false로 바뀌는것 사용해서 스타트 버튼 위치 지정.
    lateinit var EndLatLng:LatLng
    lateinit var lm:LocationManager
    var middleLatLng: LatLng? = null
    var distance:Float = 0F

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
            }

            if(walk_checkd == 1) {
                val walk_line_option = PolylineOptions().add(StartLatLng).add(middleLatLng).width(8F).color(BLUE).geodesic(true) // 시작 ~ 끝까지 라인의 옵션. 굵기, 색상, 표시할지 구분
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(middleLatLng, 17F))

                googleMap.addPolyline(walk_line_option) // 현재 위치로 계속 업데이트해줘야함.
            }
            else{
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

        lm = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val isGPSEnabled: Boolean =
                lm.isProviderEnabled(LocationManager.GPS_PROVIDER) // gps 권한 여부 Boolean 표현
        val isNetworkEnabled: Boolean =
                lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) // 네트워크 권한 여부 Boolean 표현


        binding.runStart.setOnClickListener {
            if (Build.VERSION.SDK_INT >= 26 && //빌드 SDK 버전이 26 이상이고, 퍼미션 체크를 했을때 퍼미션을 허가받았는지 확인. GRANTED (허가받은)
                    ContextCompat.checkSelfPermission
                    (activity!!.applicationContext,
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this.activity!!,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        0) //허용하지 않았다면 request 코드 0을 부여.
            } else {
                when { // 프로바이더 제공자 활성화 여부 체크
                    isNetworkEnabled -> {
                        val location =
                                lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) // 인터넷 기반으로 위치를 찾는다
                        //getLastKnownLocation(매개변수) -> 매개변수에 담긴 문자열이 위치 정보 제공자. 위치값 얻지 못하면 null 반환, 값 가져오면 관련된 정보를 location 객체에 담아 전달.

                        ny = location?.longitude!!.toString() //경도
                        nx = location.latitude.toString() // 위도

                        Toast.makeText(activity!!, "현재위치 불러옴", Toast.LENGTH_SHORT).show()
                    }

                    isGPSEnabled -> {
                        val location =
                                lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) // GPS 기반으로 위치를 찾는다
                        //getLastKnownLocation(매개변수) -> 매개변수에 담긴 문자열이 위치 정보 제공자. 위치값 얻지 못하면 null 반환, 값 가져오면 관련된 정보를 location 객체에 담아 전달.

                        ny = location?.longitude!!.toString() //경도
                        nx = location.latitude.toString() // 위도

                        Toast.makeText(activity!!, "현재위치 불러옴", Toast.LENGTH_SHORT).show()

                    }

                    else -> {
                    }
                }
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        2000, //몇초
                        1F, // 몇미터
                        gpsLocationListener) //위치 업데이트*/


                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        2000, //몇초
                        1F, // 몇미터
                        gpsLocationListener)

                binding.runEnd.setOnClickListener {
                    lm.removeUpdates(gpsLocationListener)  //위치 업데이트 끝
                }
            }//위치 찾기 끝

            run()

            val google_map = childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment // fragment 아래의 fragment라 그런지 chiled 사용해서 되었음.
            google_map.getMapAsync(this) //getMapAsync 로 호출,
        }

        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) { //OnmapReadyCallback이 호출시
        this.googleMap = googleMap
        addmarker()
    }


    fun run() {

        binding.runStart.setOnClickListener {
            if (walk_checkd != 1) {
                googleMap.clear()
                addmarker()

                StartLatLng = LatLng(nx.toDouble(), ny.toDouble())

                lat1 = nx.toDouble() //라티튜드
                lng1 = ny.toDouble() // 롱티튜드

                walk_checkd = 1
                Toast.makeText(activity, "달리기 시작", Toast.LENGTH_SHORT).show()

            }
        }

        binding.runEnd.setOnClickListener {
            if (walk_checkd != 2) {
                walk_checkd = 2
                Toast.makeText(activity, "달리기 종료", Toast.LENGTH_SHORT).show()

                if (middleLatLng != null) {
                    EndLatLng = middleLatLng as LatLng //최종 위치는 middle 이 마지막으로 찍힌 값
                    distance = runEnd(lat1, lng1, lat2, lng2) //거리는 runend 함수의 리턴값.

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

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng, 17F)) //위도 경도에 맞는 위치로 카메라를 이동시킴.

        val marker = MarkerOptions() //마커 설정
        marker.position(LatLng)
        marker.title("시작 위치")

        googleMap.addMarker(marker) // 지정해던 설정으로 마커 추가
    }

}