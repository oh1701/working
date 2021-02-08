package com.working.working_project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.working.working_project.databinding.ActivityLoginMainBinding

class login_main : AppCompatActivity() {

    val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityLoginMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.joinMemberBtn.setOnClickListener {
            val intent = Intent(this, join_member_frag::class.java)
            startActivity(intent)
        }

        binding.join.setOnClickListener {

            if(binding.idSel.length() > 0 && binding.passSel.length() > 0)

            {
                firebaseAuth.signInWithEmailAndPassword(binding.idSel.text.toString(), binding.passSel.text.toString())
                        .addOnCompleteListener(this) {
                            if (it.isSuccessful) {
                                movepage(firebaseAuth.currentUser) //파이어베이스 유저정보 확인
                            } else {
                                Toast.makeText(this, "아이디나 패스워드가 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
                            }

                        }
            }

            else
            {
                Toast.makeText(this, "아이디나 패스워드를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }

        }
    }

    fun movepage(user: FirebaseUser?){
        if(user != null){ //유저가 파이어베이스에 존재할시
            Toast.makeText(this, "아이디 확인을 위해 최초 1회, 이메일 인증이 필요합니다.", Toast.LENGTH_SHORT).show()

            if(firebaseAuth.currentUser!!.isEmailVerified){ // 이메일 인증 확인하기.
                Toast.makeText(this, "이미 이메일 인증을 하였습니다..", Toast.LENGTH_SHORT).show()  //돼있으면
                //메인화면 이동
            }

            else
            { //안되어있으면
                firebaseAuth.currentUser!!.sendEmailVerification() //이메일 인증 전송
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(this, "이메일 인증을 전송 하였습니다..", Toast.LENGTH_SHORT).show()
                            }
                            else
                            {
                                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show() // 오류 메세지 송출
                            }
                        }
            }
        }
    }

    /*private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
user = mAuth.currentUser?.uid.toString()
만약 사용자가 앱에 로그인되어 있다면, 해당 사용자의 uid가 표기되고, 로그인되어있지 않으면 null이 표기됩니다.
     */

}