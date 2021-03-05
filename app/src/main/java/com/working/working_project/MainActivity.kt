package com.working.working_project

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.location.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import com.google.android.gms.dynamic.SupportFragmentWrapper
import com.google.android.gms.maps.GoogleMap
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthSettings
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.working.working_project.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

// 출시하기 전 날씨 APi 이용 저작권 어떻게 하는지 확인하기 (표시해야하는지 등)

// 달력과 함께 날마다 달렸을 시, v자로 체크 기록을 보여주는 장소.
// 해당 날을 터치 시 정보창으로 이동하여 해당 날의 거리와 시간을 기록한다.



class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var binding: ActivityMainBinding
    val firebaseAuth = FirebaseAuth.getInstance()
    private var user: FirebaseUser? = null
    private lateinit var firebaseDatabase: DatabaseReference
    private lateinit var information: DatabaseReference

    lateinit var positive_btn:Button
    lateinit var negative_btn:Button
    lateinit var name_text:TextView
    lateinit var age_text:TextView
    lateinit var gender_text:TextView
    lateinit var move_object_text:TextView
    lateinit var move_count_text:TextView
    lateinit var name_edit:EditText
    lateinit var age_edit:EditText
    lateinit var gender_edit:EditText
    lateinit var move_object_edit:EditText
    lateinit var my_object_mind:EditText
    lateinit var dialog_view: View
    lateinit var dialog: Dialog
    // 23시 기준, 153개 조회

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

            dialog_view = layoutInflater.inflate(R.layout.activity_information_registor, null)

            name_text = dialog_view.findViewById(R.id.text_1)
            age_text = dialog_view.findViewById(R.id.text_2)
            gender_text = dialog_view.findViewById(R.id.text_3)
            move_object_text = dialog_view.findViewById(R.id.text_4)
            move_count_text = dialog_view.findViewById(R.id.text_5)

            gender_edit = dialog_view.findViewById(R.id.edit_1)
            name_edit = dialog_view.findViewById(R.id.edit_2)
            age_edit = dialog_view.findViewById(R.id.edit_3)
            move_object_edit = dialog_view.findViewById(R.id.edit_4)
            my_object_mind = dialog_view.findViewById(R.id.edit_5)

            positive_btn = dialog_view.findViewById(R.id.positive_btn)
            negative_btn = dialog_view.findViewById(R.id.negative_btn)

            dialog = Dialog(this)
            dialog.setContentView(dialog_view)

            firebaseDatabase = FirebaseDatabase.getInstance().getReference()
            user = FirebaseAuth.getInstance().currentUser
            val username = user!!.email.toString().split("@")
            information = FirebaseDatabase.getInstance().getReference("member").child("${username[0]}")

            val ft = supportFragmentManager.beginTransaction()
           var a = ft.replace(R.id.main_frame, weather_frag()).commit()
            a

            binding.naviSetting.setOnClickListener {
                binding.drawerlay.openDrawer(GravityCompat.START)  // 왼쪽에서 화면 나옴
            }

            binding.naviView.setNavigationItemSelectedListener(this) //네비게이션 아이템 클릭 속성 부여.

            binding.bottomNavi.setOnNavigationItemSelectedListener { //바텀 네비게이션 아이템 클릭 속성 부여
                when (it.itemId) {
                    R.id.navi1 -> {
                        set_frag(0)
                        return@setOnNavigationItemSelectedListener true
                    }
                    R.id.navi2 -> {
                        set_frag(1)
                        return@setOnNavigationItemSelectedListener true
                    }
                    R.id.navi3 -> {
                        set_frag(2)
                        return@setOnNavigationItemSelectedListener true
                    }
                    R.id.navi4 -> {
                        set_frag(3)
                        return@setOnNavigationItemSelectedListener true
                    }
                    else -> return@setOnNavigationItemSelectedListener false
                }
            }
        }

    override fun onNavigationItemSelected(item: MenuItem): Boolean { //네비게이션 아이템 선택 시
        val drawer = findViewById<DrawerLayout>(R.id.drawerlay)
        var d = 0

        when (item.itemId) {
            R.id.navi_schedule -> Log.d("확인", "확인")
            R.id.navi_alarm -> Toast.makeText(applicationContext, "네비", Toast.LENGTH_SHORT).show()
            R.id.navi_my -> {
                positive_btn.setOnClickListener {
                    when (gender_edit.text.toString()) {
                        "남자" -> {
                            information.child("이름").setValue(name_edit.text.toString())
                            information.child("나이").setValue(age_edit.text.toString())
                            information.child("성별").setValue(gender_edit.text.toString())
                            information.child("목표설정").setValue(move_object_edit.text.toString() + ".0")
                            information.child("목표까지").setValue(move_object_edit.text.toString() + ".0")
                            information.child("다짐").setValue(my_object_mind.text.toString())
                            dialog.dismiss()
                        }
                        "여자" -> {
                            information.child("이름").setValue(name_edit.text.toString())
                            information.child("나이").setValue(age_edit.text.toString())
                            information.child("성별").setValue(gender_edit.text.toString())
                            information.child("목표설정").setValue(move_object_edit.text.toString() + ".0")
                            information.child("목표까지").setValue(move_object_edit.text.toString() + ".0")
                            information.child("다짐").setValue(my_object_mind.text.toString())
                            dialog.dismiss()
                        }
                        else -> {
                            Toast.makeText(this, "남자 or 여자로만 입력해주세요.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                negative_btn.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
            }
            R.id.navi_pet -> Toast.makeText(applicationContext, "네비", Toast.LENGTH_SHORT).show()
            R.id.navi_tema_change -> {
                var dialog = AlertDialog.Builder(this)
                dialog.setTitle("앱 테마 변경")

            }
            R.id.navi_Alim -> Toast.makeText(applicationContext, "네비", Toast.LENGTH_SHORT).show()

            R.id.navi_login_logout -> {
                if (firebaseAuth.currentUser != null) {
                    firebaseAuth.signOut()
                    Toast.makeText(this, "로그아웃", Toast.LENGTH_SHORT).show()
                    var intent = Intent(this, login_main_frag::class.java)
                    startActivity(intent)
                }
                else
                    Toast.makeText(this, "로그인 상태가 아닙니다.", Toast.LENGTH_SHORT).show()
            }
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

    private fun set_frag(fragNum: Int) { // 프래그먼트 지정

        val ft = supportFragmentManager.beginTransaction()

        when(fragNum){
            0 -> {
                ft.replace(R.id.main_frame, weather_frag()).commit()
                binding.runningRecommend.text = "날씨 현황 확인하기"
            }
            1 -> {
                ft.replace(R.id.main_frame, my_location()).commit()
                binding.runningRecommend.text = "나의 위치"
                
            }
            2 -> {
                ft.replace(R.id.main_frame, my_information()).commit()
                binding.runningRecommend.text = "나의 정보"
            }
            3 -> {
                ft.replace(R.id.main_frame, community()).commit()
                binding.runningRecommend.text = "대화 나누기"
            }
        }
    }

}

