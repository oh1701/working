package com.working.working_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.working.working_project.databinding.ActivityCommunityBinding

class community : Fragment() { // 로그인이 되어 있을 시에만 사용가능한 프래그먼트로 설정.

    // 로그인 -> 메인화면 -> 1. 글작성 2. 최신글 3. 나의 글 4. 나의 댓글 만들기 or  << 위험하니 글 말고 다이어트 순위를 가까운 지역 내 순위를 정한 후 기록 설정.
    // 순서대로 전송하기 위해서는 가장앞에 번호를 추가, 이후 아이디로 타이틀.

    lateinit var binding:ActivityCommunityBinding
    lateinit var firebaseDatabase:DatabaseReference// 파이어베이스 실시간 db 관리 객체 얻어오기(Root 가져옴.) ()는 최상위 값.
    var user: FirebaseUser? = null
    lateinit var board_data:DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = ActivityCommunityBinding.inflate(layoutInflater, container, false)
        firebaseDatabase = FirebaseDatabase.getInstance().getReference()
        user = FirebaseAuth.getInstance().currentUser

        board_data = FirebaseDatabase.getInstance().getReference("board")

        if(board_data != null) {
            board_data.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(ds in snapshot.children)
                        Log.d("확인", ds.toString())

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("확인", "실패")
                }

            })
        }

        /*val list_board = arrayListOf(
            recycle_board.board_list()
        )*/


        return binding.root
    }

    override fun onResume() {
        Log.d("리즘", "onresume")
        binding.commuConst.visibility = View.VISIBLE

        binding.btn1.setOnClickListener {
            if(user == null)
            {
                Toast.makeText(activity!!, "로그인 되어있지 않음", Toast.LENGTH_SHORT).show()
            }
            else {

                binding.commuConst.visibility = View.GONE

                var ft = childFragmentManager.beginTransaction()
                    .replace(R.id.community_frag, community_board()).commit()
                ft
                Log.d("실행", "실행중")
            }
        }

        binding.btn2.setOnClickListener {
            if(user == null)
            {
                Toast.makeText(activity!!, "로그인 되어있지 않음", Toast.LENGTH_SHORT).show()
            }

            else {

                binding.commuConst.visibility = View.GONE

                var ft = childFragmentManager.beginTransaction()
                    .replace(R.id.community_frag, community_board()).commit()
                ft
                Log.d("실행", "실행중")
            }

        }

        super.onResume()
    }

    fun run_rank(){ // my_location 클래스에서 종료 시, 합산되는 데이터들은 정보로 이동함. 이곳에서는 정보의 데이터들을 가져와 다른 사람들과 비교하여 순위를 매김.
        val run_rank = firebaseDatabase.child("run_rank")
    }

    /*fun latest(){ //최신 글
        val latest = firebaseDatabase.child("latest")

    }

    fun my_writing(){ // 내가 쓴 글
        val my_writing = firebaseDatabase.child("my_writing")

    }

    fun my_comment(){ // 댓글
        val comment = firebaseDatabase.child("my_writing")

    }

    fun writing(){
        val writing = firebaseDatabase.child("writing")
        writing.child("title")
        writing.child("body")
    }*/
}