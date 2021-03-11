package com.working.working_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.working.working_project.databinding.ActivityCommunityBinding
import com.working.working_project.databinding.ActivityMyBoardBinding

class my_board : Fragment() {
    lateinit var binding: ActivityMyBoardBinding
    lateinit var firebaseDatabase: DatabaseReference// 파이어베이스 실시간 db 관리 객체 얻어오기(Root 가져옴.) ()는 최상위 값.
    var user: FirebaseUser? = null
    lateinit var board_data: DatabaseReference
    lateinit var all_board: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = ActivityMyBoardBinding.inflate(layoutInflater, container, false)
        firebaseDatabase = FirebaseDatabase.getInstance().getReference()
        user = FirebaseAuth.getInstance().currentUser
        val username = user!!.email.toString().split("@")

        all_board = FirebaseDatabase.getInstance().getReference("all_board") //all_board 아래 경로의 숫자 확인하기 위해 데이터 불러옴
        board_data = FirebaseDatabase.getInstance().getReference("my_board").child("${username[0]}")

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        var re_array: ArrayList<board_list> = arrayListOf()
        var re_number = mutableListOf<String>()
        var re_content = mutableListOf<String>()
        val username = user!!.email.toString().split("@")

        if (board_data != null) {
            board_data.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children){
                        re_number.add(ds.key.toString())
                        re_content.add(ds.value.toString())
                    }

                    for(i in re_number.size - 1 downTo 0){
                        re_array.add(board_list(re_number[i].toString(), "작성자 : ${username[0].toString()}")) // 파이어베이스 db는 순서를 정렬할수 없기에 역순으로 넣어서 최신것을 위로가게 만듦
                    }
                    (binding.myGetboard.adapter as recycle_board).notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("확인", "실패")
                }

            })
            binding.myGetboard.layoutManager = LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
            binding.myGetboard.setHasFixedSize(true)
            binding.myGetboard.adapter = recycle_board(re_array, activity!!.supportFragmentManager, re_number, re_content)
        }

        binding.btn1.setOnClickListener {
            var ft = activity!!.supportFragmentManager.beginTransaction().replace(R.id.main_frame, community()).commit()
            ft
        }
    }
}