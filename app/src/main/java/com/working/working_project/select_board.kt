package com.working.working_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.working.working_project.databinding.ActivitySelectBoardBinding

class select_board : Fragment() {
    /*var position = 0
    var size = 0
    var title = ""
    var content = ""*/

    lateinit var binding:ActivitySelectBoardBinding
    lateinit var firebaseDatabase: DatabaseReference// 파이어베이스 실시간 db 관리 객체 얻어오기(Root 가져옴.) ()는 최상위 값.
    var user: FirebaseUser? = null
    lateinit var board_comment: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = ActivitySelectBoardBinding.inflate(inflater, container, false)
        firebaseDatabase = FirebaseDatabase.getInstance().getReference()
        user = FirebaseAuth.getInstance().currentUser

        board_comment = FirebaseDatabase.getInstance().getReference().child("comment")//all_board 아래 경로의 숫자 확인하기 위해 데이터 불러옴

        Log.d("룰루라랄룰랄", d.toString())
        Log.d("룰루라랄룰랄", select_title.toString())
        Log.d("룰루라랄룰랄", select_content.toString())

        //댓글창 설정. 댓글 쓰면 community로 나가지고 다시 들어오면 onresume하면서 댓글 데이터 읽어온다. 답글은 안되고 덧글로 새로 추가하는 식만 생기게 하기.
        //다른 사람이 쓴 댓글은 데이터 읽어와서 리사이클러뷰로 추가하기. 만약 자신이 덧글을 쓰면 리프레쉬시켜준다.
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val username = user!!.email.toString().split("@")

        board_comment.addListenerForSingleValueEvent(object : ValueEventListener {
            var a = 0
            override fun onDataChange(snapshot: DataSnapshot) {
                for(ad in snapshot.children){
                    Log.d("확인이여여", a.toString())
                    Log.d("확인이여여", ad.toString())
                    a++
                    //여기에 리사이클러뷰 텍스트 추가하기.
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
        binding.commuTitle.text = select_title.toString()
        binding.commuIndata.text = select_content.toString()

        binding.backPressed.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.main_frame, community()).commit()
        }

        binding.register.setOnClickListener {
            board_comment.child("$full_title").child("$username").setValue(binding.bottomText.text.toString())
            val ft = fragmentManager!!.beginTransaction()
            ft.detach(this).attach(this).commit() // 프래그먼트 리프레쉬 하기
        }

        //Log.d("확인", position.toString() + "와" + size.toString() + "와" + title + content)
    }
}