
package com.working.working_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.working.working_project.databinding.ActivityCommunityBinding
import com.working.working_project.databinding.ActivityMainBinding
import com.working.working_project.databinding.ActivityMyInformationBinding

class my_information : Fragment(), inter_run_information {

    lateinit var binding: ActivityMyInformationBinding
    val firebaseDatabase = FirebaseDatabase.getInstance().getReference() //파이어베이스를 만들때는 생명주기 아래에 해야 되는듯. 안그러니까 버그가 걸린다.

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = ActivityMyInformationBinding.inflate(layoutInflater, container, false)

        binding.basicImage.setBackgroundColor(0)
        Glide.with(this).load(R.drawable.naver_icon).apply(RequestOptions.circleCropTransform()).into(binding.basicImage)
        // 글라이드를 이용, apply의 requestoption 중 circlecroptransform을 사용하여 이미지를 원형으로 자른다.

        firebaseDatabase.child("member").child("이름").setValue(binding.memberName.text.toString())
        firebaseDatabase.child("member").child("성별").setValue(binding.memberGender.text.toString())
        firebaseDatabase.child("member").child("나이").setValue(binding.memberAge.text.toString())
        firebaseDatabase.child("member").child("운동날짜").setValue(binding.memberRunningDay.text.toString())
        firebaseDatabase.child("member").child("운동거리").setValue(binding.memberRunningMeter.text.toString())
        firebaseDatabase.child("member").child("목표까지").setValue(binding.memberRunningObject.text.toString())

        return binding.root
    }

}