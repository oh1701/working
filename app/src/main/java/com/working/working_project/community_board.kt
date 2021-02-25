package com.working.working_project

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = ActivityCommunityBoardBinding.inflate(layoutInflater, container, false)
        firebaseDatabase = FirebaseDatabase.getInstance().getReference()
        user = FirebaseAuth.getInstance().currentUser

        val all_board = FirebaseDatabase.getInstance().getReference("all_board")

        all_board.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(ds in snapshot.children) {
                    Log.d("확인", "$ds")
                    if(ds.key == null) {
                    }
                }

                    
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        /*for(i in 0 .. 9999) {
            if () {
                firebaseDatabase.child("all_board").child("$i").child("1").setValue("1") // 전체 게시판인 all_board에도 추가.
                Log.d("확인", "이것의 번째는 $i 입니다.")
                break
            }
        }*/
        return binding.root
    }

    override fun onResume() {
        Log.d("리즘", "onresume")
        binding.boardLay.visibility = View.VISIBLE

        binding.register.setOnClickListener {

            commu_title = binding.commuTitle.text.toString()
            commu_indata = binding.commuIndata.text.toString()

            if(commu_title.length == 0)
                Log.d("제목 미입력", "제목 미입력")
            else if(commu_indata.length == 0)
                Log.d("내용 미입력", "내용 미입력")
            else if(user != null) {
                Log.d("유저 이름", user!!.email.toString())
                val username = user!!.email.toString().split("@") // 유저 이메일주소에서 @를 뺀 곳 까지 문자열 자르기. 파이어베이스 데이터베이스 차일드에 특문 기록 안되는듯.
                Log.d("유저 이름", username[0]) // 0번에 @이전의 문자열이 기록되어있음.


                // 유저 이메일 @이전의 것까지를 아이디로, 그 하위를 제목으로, 그 하위를 내용으로 지정
                firebaseDatabase.child("all_board").child(username[0]).child("$commu_title").setValue(commu_indata) // 자기 자신이 쓴 글 확인용인 board에 추가
                    .addOnCompleteListener {
                        Toast.makeText(activity!!, "성공", Toast.LENGTH_SHORT).show()
                    }
            }

        }

        binding.backPressed.setOnClickListener {
            binding.boardLay.visibility = View.GONE
            val ft = childFragmentManager.beginTransaction().replace(R.id.board_frame, community()).commit()
            ft
        }
        super.onResume()
    }


}