package com.working.working_project

import android.app.*
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.location.*
import android.media.RingtoneManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.working.working_project.databinding.ActivityMainBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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

            val dateformat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val nowdate =  LocalDateTime.now().format(dateformat)
            Log.d("확인111", nowdate.toString())

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
            information = FirebaseDatabase.getInstance().getReference("member").child("${username[0]}").child(nowdate.toString())

            var shared_text: SharedPreferences = getSharedPreferences("main_tema", 0)
            var tema_checkd = shared_text.getString("main_tema", "")
            when (tema_checkd){
                "base_tema" -> binding.drawerlay.setBackgroundResource(R.drawable.main_grade)
                "black_tema" -> binding.drawerlay.setBackgroundColor(Color.parseColor("#1B1C1C"))
                "olive_tema" -> binding.drawerlay.setBackgroundColor(Color.parseColor("#2F4939"))
            }

            var ft = supportFragmentManager
                .beginTransaction() // 기본 프래그먼트 부여
                .add(R.id.main_frame, weather_frag())
                .commit()
            ft

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
            
            //기록
            R.id.navi_my -> {
                supportFragmentManager.beginTransaction().replace(R.id.main_frame, my_information()).commit()
            }
            R.id.navi_my_get_board -> {
                supportFragmentManager.beginTransaction().replace(R.id.main_frame, my_board()).commit()
            }

            //설정
            R.id.navi_information -> {
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
            
            R.id.navi_tema_change -> {
                val view = layoutInflater.inflate(R.layout.app_tema, null)

                var base_tema = view.findViewById<RadioButton>(R.id.radio1)
                var black_tema = view.findViewById<RadioButton>(R.id.radio2)
                var olive_tema = view.findViewById<RadioButton>(R.id.radio3)
                var choice = view.findViewById<Button>(R.id.choice)
                var cancel = view.findViewById<Button>(R.id.cancel)

                var shared_text: SharedPreferences = getSharedPreferences("main_tema", 0)


                var dialog = Dialog(this)
                dialog.setContentView(view)

                var width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 330F, resources.displayMetrics).toInt() //다이얼로그 넓이 지정
                var height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 400F, resources.displayMetrics).toInt() //다이얼로그 높이 지정
                var dialog_layout = dialog.window!!.attributes as WindowManager.LayoutParams // 레이아웃 파람스 지정

                dialog_layout.width = width
                dialog_layout.height = height//WindowManager.LayoutParams.MATCH_PARENT
                dialog.window!!.attributes = dialog_layout

                choice.setOnClickListener {
                    if(base_tema.isChecked) {
                        binding.drawerlay.setBackgroundResource(R.drawable.main_grade)

                        val edit = shared_text.edit()
                        edit.remove("main_tema")
                        edit.putString("main_tema", "base_tema")
                        edit.apply() // 저장완료

                        dialog.dismiss()
                    }
                    else if(black_tema.isChecked) {
                        binding.drawerlay.setBackgroundColor(Color.parseColor("#1B1C1C"))
                        val edit = shared_text.edit()
                        edit.remove("main_tema")
                        edit.putString("main_tema", "black_tema")
                        edit.apply()

                        dialog.dismiss()
                    }
                    else if(olive_tema.isChecked) {
                        binding.drawerlay.setBackgroundColor(Color.parseColor("#2F4939"))
                        val edit = shared_text.edit()
                        edit.remove("main_tema")
                        edit.putString("main_tema", "olive_tema")
                        edit.apply()

                        dialog.dismiss()
                    }
                }
                cancel.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.show()

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
        //val ft = supportFragmentManager

        if (binding.drawerlay.isDrawerOpen(GravityCompat.START)) // drawerlayout이 GravityCompat.START 식으로 열려있는 경우
        {
            binding.drawerlay.closeDrawers() // 닫아준다
        }
        /*else if(ft.backStackEntryCount > 0) {
                ft.popBackStack()
            Log.d("확인", "여기이이이")
        }*/
        else {
            if(user != null) {
                moveTaskToBack(true)
                finishAndRemoveTask()
                android.os.Process.killProcess(android.os.Process.myPid())
            }
            else
                 super.onBackPressed()
        }
    }

    private fun set_frag(fragNum: Int) { // 프래그먼트 지정

        val ft = supportFragmentManager

        when(fragNum){
            0 -> {
                ft.beginTransaction().replace(R.id.main_frame, weather_frag()).commit()
                binding.runningRecommend.text = "날씨 현황 확인하기"
            }
            1 -> {
                ft.beginTransaction().replace(R.id.main_frame, my_location()).addToBackStack(null).commit()
                binding.runningRecommend.text = "나의 위치"
                
            }
            2 -> {
                ft.beginTransaction().replace(R.id.main_frame, my_information()).addToBackStack(null).commit()
                binding.runningRecommend.text = "나의 정보"
            }
            3 -> {
                ft.beginTransaction().replace(R.id.main_frame, community()).addToBackStack(null).commit()
                binding.runningRecommend.text = "대화 나누기"
            }
        }
    }
}
