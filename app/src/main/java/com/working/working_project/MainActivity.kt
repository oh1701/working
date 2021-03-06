package com.working.working_project

import android.app.*
import android.content.Intent
import android.location.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.working.working_project.databinding.ActivityMainBinding
import java.util.*

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
            R.id.navi_Alim -> {
                datepicker()
            }

            R.id.navi_login_logout -> {
                if (firebaseAuth.currentUser != null) {
                    firebaseAuth.signOut()
                    Toast.makeText(this, "로그아웃", Toast.LENGTH_SHORT).show()
                    var intent = Intent(this, login_main_frag::class.java)
                    startActivity(intent)
                } else
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

    fun datepicker(){
        var alarm = getSystemService(ALARM_SERVICE) as AlarmManager

        var calendar = Calendar.getInstance()

        var year1 = calendar.get(Calendar.YEAR)
        var month1 = calendar.get(Calendar.MONTH)
        var day1 = calendar.get(Calendar.DAY_OF_MONTH)
        var hour1 = calendar.get(Calendar.HOUR_OF_DAY)
        var minute1 = calendar.get(Calendar.MINUTE)

        val datePicker = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
            Toast.makeText(this, "$year-${month + 1}-$dayOfMonth 저장되었음.", Toast.LENGTH_SHORT).show()

            year1 = year
            month1 = month + 1
            day1 = dayOfMonth

            val timePicker = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                hour1 = hourOfDay
                minute1 = minute

                calendar.set(year1, month1, day1, hour1, minute1, 0) // 캘린더에 년도 ~ 분 값 넣기.


                if (calendar.before(Calendar.getInstance())) {
                    Toast.makeText(this, "현재 일보다 이전 시간입니다.", Toast.LENGTH_SHORT).show()
                }
                else{
                    Log.d("확인", "$year1,$month1,$day1,$hour1,$minute1")
                    Toast.makeText(this, "$hourOfDay 시-$minute 분 저장되었음.", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, alarmrecive::class.java)
                    val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                    if (Build.VERSION.SDK_INT >= 23) { //sdk 버전에 따른 알람설정
                        alarm.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                    } else {
                        alarm.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                    }

                }
            }, hour1, minute1, false)
            timePicker.show()

        }, year1, month1, day1)

        datePicker.show()
    }

}

