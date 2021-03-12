package com.working.working_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
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
        var comment_id_check = mutableListOf<String>()
        var comment_content_check = mutableListOf<String>()
        var com_rec:ArrayList<comment> = arrayListOf()
        var checkd = 0 // 데이터 받았는지 확인용

        board_comment.child(full_title).addListenerForSingleValueEvent(object : ValueEventListener {
            var a = 0
            override fun onDataChange(snapshot: DataSnapshot) {
                for(ad in snapshot.children){
                    comment_id_check.add(a, ad.key.toString())
                    comment_content_check.add(a, ad.value.toString())
                    com_rec.add(comment(ad.key.toString(), ad.value.toString()))

                    Log.d("확인이여여", a.toString())
                    Log.d("확인이여여", ad.toString())
                    a++
                    checkd = 1
                    //여기에 리사이클러뷰 텍스트 추가하기.
                }

                binding.commentRecycler.layoutManager = LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
                binding.commentRecycler.setHasFixedSize(true)
                binding.commentRecycler.adapter = comment_recycler(com_rec)
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
            if(binding.bottomText.text.isNotEmpty()) {
                if (checkd != 0) {
                    var i = comment_id_check.size
                    board_comment.child(full_title).child("$i, " + username[0]).setValue(binding.bottomText.text.toString()) //번호를 집어넣어서 최신 댓글이 아래로가게, 아이디가 같아도 안지워지게 정렬.
                    binding.bottomText.text = null
                } else {
                    board_comment.child(full_title).child("0, " + username[0]).setValue(binding.bottomText.text.toString()) // 댓글이 아예 없으면 새로운 댓글 생성
                    binding.bottomText.text = null
                }

                val ft = fragmentManager!!.beginTransaction()
                ft.detach(this).attach(this).commit() // 프래그먼트 리프레쉬 하기
            }
            else
                Toast.makeText(activity!!, "댓글을 입력해주세요.", Toast.LENGTH_SHORT).show()
        }

        //Log.d("확인", position.toString() + "와" + size.toString() + "와" + title + content)
    }
}