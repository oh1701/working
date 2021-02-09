package com.working.working_project

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.working.working_project.databinding.ActivityJoinMemberFragBinding

/*
                                        if(networkInfo == null)
                                        {
                                            Toast.makeText(this, "네트워크 연결 상태를 확인해주세요.", Toast.LENGTH_SHORT).show()
                                        }
                                        else {
                                            Log.d("오류", it.exception.toString())
                                            Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
                                        }*/
class join_member_frag : AppCompatActivity() {

    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityJoinMemberFragBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val cm : ConnectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = cm.activeNetworkInfo?: null

        if(intent.hasExtra("join_member"))
        {
            fun create_email() { // 회원가입

                if (binding.emailInput.length() >= 1 && binding.passwordInput.length() >= 1) {

                    if (binding.emailInput.text.contains("@") && binding.emailInput.text.contains(".com") && binding.passwordInput.length() >= 6 &&
                            binding.passwordOk.text.toString() == binding.passwordInput.text.toString()) {


                        firebaseAuth.createUserWithEmailAndPassword(binding.emailInput.text.toString(), binding.passwordInput.text.toString()) // 회원 생성
                                .addOnCompleteListener(this)
                                    {
                                        if (it.isSuccessful) {
                                            Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
                                            Log.d("회원가입 성공", "회원가입 성공")
                                            finish()

                                        } else {
                                            try {
                                                throw it.exception!!
                                            } catch (firebaseException: FirebaseException) { //네트워크 미연결 시 나타나는 메세지.
                                                Toast.makeText(this, "네트워크 미연결", Toast.LENGTH_SHORT).show()
                                            }

                                        }

                                    }

                    }


                    else if (binding.passwordInput.length() < 6) {
                        Toast.makeText(this, "비밀번호를 6자리 이상 입력해주세요.", Toast.LENGTH_SHORT).show()// 비밀번호 6자리 이상 사용해야한다는 메세지.
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

        else if(intent.hasExtra("src_pass"))
        {
            binding.saveId.text = "비밀번호 변경"
            binding.passwordInput.visibility = View.GONE
            binding.passwordOk.visibility = View.GONE

            fun password_find(){ //비밀번호 찾기
                if(binding.emailInput.length() > 0)
                firebaseAuth.sendPasswordResetEmail(binding.emailInput.text.toString())
                    .addOnCompleteListener{
                        if(it.isSuccessful)
                        {
                            Toast.makeText(this, "비밀번호 변경 메일이 전송되었습니다.", Toast.LENGTH_SHORT).show()
                        }
                        else{
                                Toast.makeText(this, "이메일 전송이 실패하였습니다. 가입된 이메일이 아니거나, 네트워크 상태가 불량합니다.", Toast.LENGTH_SHORT).show()
                                Log.d("오류", it.exception.toString())
                        }
                    }

                else
                    Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }

            binding.saveId.setOnClickListener {
                password_find()
            }
        }






    }
}