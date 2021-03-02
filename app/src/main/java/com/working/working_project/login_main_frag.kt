package com.working.working_project

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.gms.dynamic.SupportFragmentWrapper
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.working.working_project.databinding.ActivityLoginMainBinding
import com.working.working_project.databinding.ActivityMyLocationBinding

class login_main_frag : AppCompatActivity() {

    val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginMainBinding.inflate(layoutInflater)
        
        setContentView(binding.root)

        binding.joinMemberBtn.setOnClickListener {
            val intent = Intent(this, join_member_frag::class.java)
            intent.putExtra("join_member", "join_member")
            startActivity(intent)
        }

        binding.idSrcBtn.setOnClickListener {
            val intent = Intent(this, join_member_frag::class.java)
            intent.putExtra("src_pass", "src_pass")
            startActivity(intent)
        }

        binding.join.setOnClickListener {

            if (binding.idSel.length() > 0 && binding.passSel.length() > 0) {
                firebaseAuth.signInWithEmailAndPassword(binding.idSel.text.toString(), binding.passSel.text.toString())
                        .addOnCompleteListener(this) {
                            if (it.isSuccessful) { // 성공시
                                movepage(firebaseAuth.currentUser) //파이어베이스 유저정보 확인
                            } else {
                                try {
                                    throw it.exception!!
                                } catch (FirebaseAuth_notsrc_member: FirebaseAuthInvalidUserException) { // 회원이 등록되어 있지 않음.
                                    Toast.makeText(this, "아이디가 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
                                } catch (FirebaseAuth_notsrc_password: FirebaseAuthInvalidCredentialsException) { // 회원이 등록되어 있지 않음.
                                    Toast.makeText(this, "패스워드가 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
                                } catch (firebaseException: FirebaseException) { //네트워크 미연결 시 나타나는 메세지.
                                    Toast.makeText(this, "네트워크를 확인해주세요.", Toast.LENGTH_SHORT).show()
                                }


                                Log.d("오류", it.exception.toString())
                            }

                        }
            }
            else {
                Toast.makeText(this, "아이디나 패스워드를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }

        }
    }

    fun movepage(user: FirebaseUser?) {

        if (firebaseAuth.currentUser!!.isEmailVerified) { // 이메일 인증 됐을 시

            Toast.makeText(this, "성공적으로 로그인 되었습니다.", Toast.LENGTH_SHORT).show()  //돼있으면
            //메인화면 이동
            /*val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("로그인", firebaseAuth.currentUser!!)
            startActivity(intent)*/

            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        } else { //이메일 인증 확인 안됐을 시
            if (user != null) { //유저가 파이어베이스에 존재할시
                Toast.makeText(this, "아이디 확인을 위해 최초 1회, 이메일 인증이 필요합니다.", Toast.LENGTH_SHORT).show()

                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("이메일 인증이 필요합니다.")
                dialog.setMessage("확인을 위해 최초 1회, 이메일 인증이 필요합니다. 인증하시겠습니까?")

                dialog.setPositiveButton("인증하기") //인증하기 버튼 누를 시
                { DialogInterface, i ->

                    firebaseAuth.currentUser!!.sendEmailVerification() //이메일 인증 전송
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Toast.makeText(this, "이메일 인증을 전송 하였습니다.", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this, "이메일 전송이 실패하였습니다. 가입된 이메일이 아니거나, 네트워크 상태가 불량합니다.", Toast.LENGTH_SHORT).show() // 오류 메세지 송출
                                    Log.d("오류", it.exception.toString())
                                }
                            }
                }

                dialog.setNegativeButton("취소") //취소 버튼 누를 시

                { DialogInterface, i ->

                    false
                }

                dialog.show()
            } else {
                Toast.makeText(this, "존재하지 않는 아이디입니다.", Toast.LENGTH_SHORT).show()
            }
        }

        /*private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
user = mAuth.currentUser?.uid.toString()
만약 사용자가 앱에 로그인되어 있다면, 해당 사용자의 uid가 표기되고, 로그인되어있지 않으면 null이 표기.
     */

    }
}