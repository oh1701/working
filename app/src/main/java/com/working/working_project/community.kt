package com.working.working_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.working.working_project.databinding.ActivityCommunityBinding

class community : Fragment() { // 로그인이 되어 있을 시에만 사용가능한 프래그먼트로 설정.

    // 로그인 -> 메인화면 -> 1. 글작성 2. 최신글 3. 나의 글 4. 나의 댓글 만들기 or  << 위험하니 글 말고 다이어트 순위를 가까운 지역 내 순위를 정한 후 기록 설정.
    lateinit var binding:ActivityCommunityBinding
    val firebaseDatabase = FirebaseDatabase.getInstance().getReference() // 파이어베이스 실시간 db 관리 객체 얻어오기(Root 가져옴.) ()는 최상위 값.

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = ActivityCommunityBinding.inflate(layoutInflater, container, false)



        binding.btn1.setOnClickListener {
            val getedt = binding.edt.text.toString()

            firebaseDatabase.child("user2").setValue(getedt) // 최상위의 하위 객체 생성 후 입력한 텍스트값 값으로 넣어줌. 
                    .addOnSuccessListener { //성공
                        Toast.makeText(activity, "저장 완료", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { //실패
                        Toast.makeText(activity, "저장 실패, 이유 ${it}", Toast.LENGTH_SHORT).show()
                    }
        }

        return binding.root
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