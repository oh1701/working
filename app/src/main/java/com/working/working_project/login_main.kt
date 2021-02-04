package com.working.working_project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import com.working.working_project.databinding.ActivityLoginMainBinding

class login_main : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityLoginMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.joinMemberBtn.setOnClickListener {
            val intent = Intent(this, join_member_frag::class.java)
            startActivity(intent)
        }

        val firebaseAuth = FirebaseAuth.getInstance()

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
            Toast.makeText(this, "아이디가 존재한다.", Toast.LENGTH_SHORT).show()
        }
    }
}