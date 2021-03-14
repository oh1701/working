package com.working.working_project

import android.content.ClipData
import android.view.inspector.IntFlagMapping
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

val retrofit = Retrofit.Builder().baseUrl("http://apis.data.go.kr/1360000/VilageFcstInfoService/") // url 지정
    .addConverterFactory(GsonConverterFactory.create())
    .build()

object ApiObject {
    val retrofitService: WeatherInterface by lazy {
        retrofit.create(WeatherInterface::class.java)
    }
}

interface WeatherInterface{

    @GET("getUltraSrtFcst?serviceKey=Y%2FPMwCd%2Fy9DOSNXSeH3LU3N9Y4XN3rt12jptcwyTHxqGfE8ZeVDZ%2F0t%2FsCbgexqc2Y657jHsq5QNS32qTluCGA%3D%3D") // 초단기 예보
    fun GetWeather(
        @Query("dataType") data_type : String, //쿼리는 주소 '?' 뒷부분의 속성을 작성할 수 있음.
        @Query("numOfRows") num_Of_rows : Int, //Query는 직역하면 질의문이라는 의미
        @Query("pageNo") page_No : Int,
        @Query("base_date") base_date : String,
        @Query("base_time") base_time :String,
        @Query("nx") nx : String,
        @Query("ny") ny:String,
        @Query("fcstValue") fcstValue:Double,
        @Query("fcstDate") fcstDate:String,
         @Query("fcstTime") fcstTime:String
    ): Call<WEATHER> //WEATHER 는 DATA CLASS
}

data class WEATHER( //활용가이드 응답메세지를 변형시킨것.
    val response : RESPONSE
)

data class RESPONSE( //활용가이드 응답메세지를 변형시킨것.
    val header: HEADER,
    val body:BODY
)

data class HEADER( //활용가이드 응답메세지를 변형시킨것.
    val resultCode : Int,
    val resultMsg : String
)

data class BODY( //활용가이드 응답메세지를 변형시킨것.
    val dataType : String,
    val items : ITEMS,
    val totalCount : String
)

data class ITEMS( //활용가이드 응답메세지를 변형시킨것.
    val item : List<ITEM>
)

data class ITEM( //활용가이드 응답메세지를 변형시킨것.
    val basedate: String, // 발표 날짜
    val baseTime: String, //발표 시간
    val category : String,
    val fcstDate : String,
    val fcstTime : String,//항목
    val fcstValue:Double
)