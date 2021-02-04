package com.working.working_project

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.working.working_project.databinding.ActivityJoinMemberFragBinding

class join_member_frag : AppCompatActivity() {

    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityJoinMemberFragBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fun create_email() {

            if (binding.emailInput.length() >= 1 && binding.passwordInput.length() >= 1 && binding.nameInput.length() >= 1) {

                if (binding.emailInput.text.contains("@") && binding.emailInput.text.contains(".com") && binding.passwordInput.length() >= 6 &&
                        binding.passwordOk.text.toString() == binding.passwordInput.text.toString()) {
                    firebaseAuth.createUserWithEmailAndPassword(binding.emailInput.text.toString(), binding.passwordInput.text.toString()) // 회원 생성
                            .addOnCompleteListener(this) {
                                if (it.isSuccessful) {
                                    Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
                                    Log.d("회원가입 성공", "회원가입 성공")
                                    finish()
                                } else {
                                    Log.d("회원가입 실패", "회원가입 실패")
                                    Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
                                }
                            }

                }
                else if (binding.passwordInput.length() < 6) {
                    Toast.makeText(this, "비밀번호를 6자리 이상 입력해주세요.", Toast.LENGTH_SHORT).show()// @, . 가 사용되어야한다는 글자 띄우기.
                }
                else if (binding.passwordOk.text.toString() != binding.passwordInput.text.toString()) {
                    Toast.makeText(this, "비밀번호가 다릅니다.", Toast.LENGTH_SHORT).show()// 비밀번호가 다름.
                }
                else if (binding.emailInput.text.contains("@") == false && binding.emailInput.text.contains(".com") == false) {
                    Toast.makeText(this, "이메일 형식으로 입력해주세요.", Toast.LENGTH_SHORT).show()// 이메일 형식이 아닐경우 띄움.
                }

            }
            else {
                Toast.makeText(this, "글자를 입력해주세요.", Toast.LENGTH_SHORT).show()// 팝업창으로 글자가 1자 이상 입력되어야 한다는 글자 띄우기.
            }
        }


        binding.saveId.setOnClickListener {
            create_email()
        }






    }
}