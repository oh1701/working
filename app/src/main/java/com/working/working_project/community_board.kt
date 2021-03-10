package com.working.working_project

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.working.working_project.databinding.ActivityCommunityBinding
import com.working.working_project.databinding.ActivityCommunityBoardBinding

class community_board : Fragment() {

    lateinit var binding:ActivityCommunityBoardBinding
    lateinit var firebaseDatabase:DatabaseReference
    var user: FirebaseUser? = null
    var commu_title:String = ""
    var commu_indata:String = ""
    var count = -1
    lateinit var all_board:DatabaseReference

    var key_list= arrayOfNulls<String>(9999)
    var value_list= arrayOfNulls<String>(9999)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = ActivityCommunityBoardBinding.inflate(layoutInflater, container, false)
        firebaseDatabase = FirebaseDatabase.getInstance().getReference()
        user = FirebaseAuth.getInstance().currentUser
        all_board = FirebaseDatabase.getInstance().getReference("all_board") //all_board 아래 경로의 숫자 확인하기 위해 데이터 불러옴

        return binding.root
    }

    override fun onResume() {
        val connectivityManager = activity!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var network_check = connectivityManager.activeNetworkInfo
        var inconnect:Boolean = network_check?.isConnectedOrConnecting == true

        if(inconnect == false){ // 와이파이 , 데이터 미연결시 실행


            var alter = AlertDialog.Builder(activity!!)
            alter.setTitle("권한 허용").setMessage("인터넷이 연결되어있지 않습니다. \n해당 기능이 꺼져 있을 시, 게시물이 등록되지 않습니다.")

            alter.setPositiveButton("데이터 켜기") { DialogInterface, i ->
                var intent = Intent(android.provider.Settings.ACTION_DATA_USAGE_SETTINGS)
                startActivity(intent)
            }

            alter.setNegativeButton("취소"){DialogInterface, i ->
            }

            alter.show()
        }
        else {
            var i = -1

            all_board.addListenerForSingleValueEvent(object : ValueEventListener { //all_board 값 가져오기
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children) {
                        i++
                        key_list[i] = ds.key.toString()
                        value_list[i] = ds.value.toString()
                        Log.d("값은", "$ds") // 어레이리스트에 ds.key와 value 담기.
                        Log.d("key값은", "${key_list[i]}")
                        Log.d("value값은", "${value_list[i]}")
                        Log.d("i 값은", i.toString())
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

            Log.d("리즘", "onresume")
            binding.boardLay.visibility = View.VISIBLE


            binding.register.setOnClickListener {
                commu_title = binding.commuTitle.text.toString()
                commu_indata = binding.commuIndata.text.toString()

                if (commu_title.length == 0)
                    Log.d("제목 미입력", "제목 미입력")
                else if (commu_indata.length == 0)
                    Log.d("내용 미입력", "내용 미입력")
                else if (user != null) { // 유저가 null도 아니고, 제목과 내용에 문자가 미입력도 아닐 시 실행.
                    Log.d("유저 이름", user!!.email.toString())
                    val username = user!!.email.toString().split("@") // 유저 이메일주소에서 @를 뺀 곳 까지 문자열 자르기. 파이어베이스 데이터베이스 차일드에 특문 기록 안되는듯.
                    Log.d("유저 이름", username[0]) // 0번에 @이전의 문자열이 기록되어있음.

                    for (i in 0..9999) {
                        var bc = key_list[i].toString().split(" 번,")

                        if (key_list[0] != null && bc[0] == "null") { //현재 가진 데이터의 경로(Key)의 값이 0 ~ 999 중에 없는 것을 찾는다
                            Log.d("없는 것은", "$i 임") // 없는 숫자가 있을 경우 그 값을 출력

                            firebaseDatabase.child("all_board").child("$i 번, ${commu_title}").setValue(commu_indata) // 자기 자신이 쓴 글 확인용인 board에 추가
                            firebaseDatabase.child("my_board").child(username[0]).child("$commu_title").setValue(commu_indata) // 자기 자신이 쓴 글 확인용인 board에 추가
                                    .addOnCompleteListener {
                                        Toast.makeText(activity!!, "성공", Toast.LENGTH_SHORT).show()

                                        binding.boardLay.visibility = View.GONE
                                        val ft = childFragmentManager.beginTransaction().replace(R.id.board_frame, community()).commit()
                                        ft
                                    }

                            break // 반복 종료
                        }

                        if(key_list[0] == null){
                            firebaseDatabase.child("all_board").child("$i 번, ${commu_title}").setValue(commu_indata) // 자기 자신이 쓴 글 확인용인 board에 추가
                            firebaseDatabase.child("my_board").child(username[0]).child("$commu_title").setValue(commu_indata) // 자기 자신이 쓴 글 확인용인 board에 추가
                                    .addOnCompleteListener {
                                        Toast.makeText(activity!!, "성공", Toast.LENGTH_SHORT).show()

                                        binding.boardLay.visibility = View.GONE
                                        val ft = childFragmentManager.beginTransaction().replace(R.id.board_frame, community()).commit()
                                        ft
                                    }
                            break
                        }
                    }

                    // 유저 이메일 @이전의 것까지를 아이디로, 그 하위를 제목으로, 그 하위를 내용으로 지정
                }

            }

            binding.backPressed.setOnClickListener {
                binding.boardLay.visibility = View.GONE
                val ft = childFragmentManager.beginTransaction().replace(R.id.board_frame, community()).commit()
                ft
            }
        }
            super.onResume()
    }
}