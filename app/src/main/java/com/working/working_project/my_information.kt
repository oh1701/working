
package com.working.working_project

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.working.working_project.databinding.ActivityMyInformationBinding
import java.util.*

class my_information : Fragment(), inter_run_information {

    lateinit var binding: ActivityMyInformationBinding
    lateinit var firebaseDatabase:DatabaseReference// 파이어베이스 실시간 db 관리 객체 얻어오기(Root 가져옴.) ()는 최상위 값.
    var user: FirebaseUser? = null
    lateinit var name:DatabaseReference

    lateinit var positive_btn: Button
    lateinit var negative_btn: Button
    lateinit var name_text: TextView
    lateinit var age_text: TextView
    lateinit var gender_text: TextView
    lateinit var move_object_text: TextView
    lateinit var move_count_text: TextView
    lateinit var name_edit: EditText
    lateinit var age_edit: EditText
    lateinit var gender_edit: EditText
    lateinit var move_object_edit: EditText
    lateinit var my_object_mind: EditText
    lateinit var dialog_view: View
    lateinit var dialog: Dialog

    private var key_list= arrayOfNulls<String>(99)
    private var value_list= arrayOfNulls<String>(99)
    private var infor_list = arrayOfNulls<String>(3)

    override fun onCreateView(layoutInflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding = ActivityMyInformationBinding.inflate(layoutInflater, container, false)

        firebaseDatabase = FirebaseDatabase.getInstance().getReference()
        user = FirebaseAuth.getInstance().currentUser
        val username = user!!.email.toString().split("@")
        name = FirebaseDatabase.getInstance().getReference("member").child("${username[0]}") //all_board 아래 경로의 숫자 확인하기 위해 데이터 불러옴

        name.addListenerForSingleValueEvent(object : ValueEventListener {
            var i = -1
            override fun onDataChange(snapshot: DataSnapshot) {
                i++
                for (ds in snapshot.children) {
                    key_list[i] = ds.key
                    value_list[i] = ds.value.toString()
                    when (key_list[i]) {
                        "목표설정" -> binding.moveObject.text = "목표 거리\n" + value_list[i].toString() + " m"
                        "목표까지" -> binding.moveObjectPercent.text = "남은 거리\n" + value_list[i].toString() + " m"
                        "운동거리" -> binding.moveWalked.text = "운동 거리\n" + value_list[i].toString() + " m"
                        "다짐" -> binding.promise.text = value_list[i].toString()
                        "이름" -> infor_list[0] = value_list[i].toString()
                        "나이" -> infor_list[1] = value_list[i].toString()
                        "성별" -> infor_list[2] = value_list[i].toString()
                    }
                }

                binding.nameAge.text = infor_list[0] + "\t/\t" + infor_list[1] + "\t/\t" + infor_list[2]
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        binding.basicImage.setBackgroundColor(0)

        Glide.with(this).load(R.drawable.naver_icon).apply(RequestOptions.circleCropTransform()).into(binding.basicImage)
        // 글라이드를 이용, apply의 requestoption 중 circlecroptransform을 사용하여 이미지를 원형으로 자른다.

        binding.inforReset.setOnClickListener {
            dialog()
        }

        return binding.root
    }

    fun dialog(){
        dialog_view = layoutInflater.inflate(R.layout.activity_information_registor, null)

        name_text = dialog_view.findViewById(R.id.text_1)
        age_text = dialog_view.findViewById(R.id.text_2)
        gender_text = dialog_view.findViewById(R.id.text_3)
        move_object_text = dialog_view.findViewById(R.id.text_4)
        move_count_text = dialog_view.findViewById(R.id.text_5)

        gender_edit = dialog_view.findViewById(R.id.edit_1)
        name_edit = dialog_view.findViewById(R.id.edit_2)
        age_edit = dialog_view.findViewById(R.id.edit_3)
        move_object_edit = dialog_view.findViewById(R.id.edit_4)
        my_object_mind = dialog_view.findViewById(R.id.edit_5)

        positive_btn = dialog_view.findViewById(R.id.positive_btn)
        negative_btn = dialog_view.findViewById(R.id.negative_btn)

        var calendar = Calendar.DAY_OF_WEEK_IN_MONTH

        positive_btn.setOnClickListener {
            when (gender_edit.text.toString()) {
                "남자" -> {
                    name.child("이름").setValue(name_edit.text.toString())
                    name.child("나이").setValue(age_edit.text.toString())
                    name.child("성별").setValue(gender_edit.text.toString())
                    name.child("목표설정").setValue(move_object_edit.text.toString() + ".0")
                    name.child("목표까지").setValue(move_object_edit.text.toString() + ".0")
                    name.child("다짐").setValue(my_object_mind.text.toString())
                    dialog.dismiss()
                    val ft = fragmentManager!!.beginTransaction()
                    ft.detach(this).attach(this).commit() // 프래그먼트 리프레쉬 하기
                }
                "여자" -> {
                    name.child("이름").setValue(name_edit.text.toString())
                    name.child("나이").setValue(age_edit.text.toString())
                    name.child("성별").setValue(gender_edit.text.toString())
                    name.child("목표설정").setValue(move_object_edit.text.toString() + ".0")
                    name.child("목표까지").setValue(move_object_edit.text.toString() + ".0")
                    name.child("다짐").setValue(my_object_mind.text.toString())
                    dialog.dismiss()
                    val ft = fragmentManager!!.beginTransaction()
                    ft.detach(this).attach(this).commit() // 프래그먼트 리프레쉬 하기
                }
                else -> {
                    Toast.makeText(activity!!, "남자 or 여자로만 입력해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        negative_btn.setOnClickListener {
            dialog.dismiss()
        }

        dialog = Dialog(activity!!)
        dialog.setContentView(dialog_view)

        dialog.show()
    }

}